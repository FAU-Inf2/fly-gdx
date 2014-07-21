package de.fau.cs.mad.fly.features.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;

/**
 * This class implements the function to show in the game small arrows that
 * indicate the direction of the next gates.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GateIndicator implements IFeatureInit, IFeatureDraw, IFeatureRender {
    
    private GameController gameController;
    private ModelBatch modelBatch;
    private Environment environment;
    private Vector3 targetPosition;
    private Vector3 vectorToTarget;
    private Vector3 cross;
    private Vector3 cameraDirection;
    private Vector3 up;
    private Vector3 down;
    private Vector3 gatePositionRelativeToCamera;
    
    private GameObject arrowModel;
    private Camera camera;
    
    private TextureRegion arrow;
    private Batch batch;
    
    private Level level;
    
    /**
     * Radius of the circle, the arrows cover when indicating the next gate. It
     * is limited by the reset-steering button, because it should not overlay
     * this button.
     */
    private final int radius = 250;
    /**
     * Horizontal center of the screen. It defines the mid point of the circle
     * the arrows cover.
     */
    private final float midX = Gdx.graphics.getWidth() / 2;
    /**
     * Vertical center of the screen. It defines the mid point of the circle the
     * arrows cover.
     */
    private final float midY = Gdx.graphics.getHeight() / 2;
    /**
     * Horizontal starting position, where the arrow is placed when angle = 0°
     */
    private final float startPosX;
    /**
     * Vertical starting position, where the arrow is placed when angle = 0°
     */
    private final float startPosY = midY + radius;
    
    private final float arrowHeigth;
    private final float arrowWidth;
    
    private final float originX;
    private final float originY;
    private float angle = 0;
    
    public GateIndicator(final Skin skin) {
        arrow = skin.getRegion("arrow");
        arrowWidth = arrow.getRegionWidth();
        arrowHeigth = arrow.getRegionHeight();
        originX = arrow.getRegionWidth() / 2;
        originY = -radius;
        startPosX = midX - arrow.getRegionWidth() / 2;
        this.batch = new SpriteBatch();
    }
    
    @Override
    public void init(final GameController gameController) {
        this.gameController = gameController;
        Assets.load(Assets.arrow);
        arrowModel = new GameObject(Assets.manager.get(Assets.arrow));
        this.level = gameController.getLevel();
        modelBatch = gameController.getBatch();
        environment = gameController.getLevel().getEnvironment();
        camera = gameController.getCamera();
        
        vectorToTarget = new Vector3();
        cross = new Vector3();
        cameraDirection = new Vector3();
        up = new Vector3();
        down = new Vector3();
        gatePositionRelativeToCamera = new Vector3();
    }
    
    @Override
    public void render(float delta) {
        int[] nextLevels = gameController.getLevel().currentGates();
        int len = nextLevels.length;
        Level.Gate gate;
        for (int i = 0; i < len; i++) {
            gate = gameController.getLevel().getGateById(nextLevels[i]);
            targetPosition = gate.goal.getPosition();
            cameraDirection.set(camera.direction);
            up.set(camera.up);
            down.set(up).scl(-1.4f);
            
            // The arrow should be in the middle of the screen, a little before
            // the camera, that it is always visible and below the vertical
            // midpoint.
            gatePositionRelativeToCamera = cameraDirection.scl(3).add(camera.position).add(down);
            
            vectorToTarget.set(targetPosition).sub(camera.position).scl(-1).nor();
            
            // calculate orthogonal up vector
            up.crs(vectorToTarget).crs(vectorToTarget).nor();
            
            cross.set(vectorToTarget).crs(up).nor();
            
            // create local coordinate system for the arrow. All axes have to be
            // normalized, otherwise, the arrow is scaled.
            float[] values = { up.x, up.y, up.z, 0f, cross.x, cross.y, cross.z, 0f, vectorToTarget.x, vectorToTarget.y, vectorToTarget.z, 0f, 0f, 0f, 0f, 1f };
            
            arrowModel.transform.set(values).trn(gatePositionRelativeToCamera);
            modelBatch.render(arrowModel, environment);
        }
    }
    
    @Override
    public void draw(float delta) {
        int[] gates = level.currentGates();
        Vector3 midPoint = new Vector3();
        Vector3 tmp = new Vector3();
        Vector3 projectedPoint;
        Vector3 vectorToProjectedPoint;
        up.set(camera.up);
        cameraDirection.set(camera.direction);
        // vector orthogonal to up and cameraDirection
        cross.set(up).crs(cameraDirection).nor();
        GameObject gate;
        batch.begin();
        for (int i = 0; i < gates.length; i++) {
            gate = level.getGateById(gates[i]).goal;
            if (!gate.isVisible(gameController.getCamera())) {
                projectedPoint = projectPointToPlane(gate.getPosition(), cross, up);
                tmp.set(cameraDirection).nor().scl(camera.near);
                midPoint.set(camera.position).add(tmp);
                vectorToProjectedPoint = projectedPoint.sub(camera.position);
                angle = angleBetweenTwoVectors(up, vectorToProjectedPoint);
                if (vectorToProjectedPoint.hasOppositeDirection(cross)) {
                    angle = 360 - angle;
                }
                batch.draw(arrow, startPosX, startPosY, originX, originY, arrowWidth, arrowHeigth, 1f, 1f, angle);
            }
        }
        batch.end();
    }
    
    /**
     * Calculates the result of the orthogonal projection of the point to the
     * plane, which is defined by planeX and planeY
     */
    public static Vector3 projectPointToPlane(Vector3 point, Vector3 planeX, Vector3 planeY) {
        Vector3 x = new Vector3();
        Vector3 y = new Vector3();
        x.set(planeX);
        y.set(planeY);
        Vector3 first = new Vector3();
        Vector3 second = new Vector3();
        
        y.scl(first.set(point).dot(y) / y.len2());
        x.scl(second.set(point).dot(x) / x.len2());
        Vector3 result = new Vector3();
        return result.set(y).add(x);
    }
    
    public static float angleBetweenTwoVectors(Vector3 v1, Vector3 v2) {
        Vector3 tmp = new Vector3();
        return (float) (Math.acos(tmp.set(v1).dot(v2) / (v1.len() * v2.len())) * 180 / Math.PI);
    }
}
