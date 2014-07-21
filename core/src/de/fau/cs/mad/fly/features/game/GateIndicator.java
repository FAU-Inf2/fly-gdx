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
public class GateIndicator implements IFeatureInit, IFeatureDraw {
    
    private Vector3 vectorToTarget;
    private Vector3 cross;
    private Vector3 cameraDirection;
    private Vector3 up;
    private Vector3 midPoint;
    private Vector3 tmp;
    
    private static Vector3 x = new Vector3();
    private static Vector3 y = new Vector3();
    private static Vector3 tmp1 = new Vector3();
    private static Vector3 tmp2 = new Vector3();
    
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
        midPoint = new Vector3();
        tmp = new Vector3();
        this.batch = new SpriteBatch();
    }
    
    @Override
    public void init(final GameController gameController) {
        this.level = gameController.getLevel();
        
        camera = gameController.getCamera();
        
        vectorToTarget = new Vector3();
        cross = new Vector3();
        cameraDirection = new Vector3();
        up = new Vector3();
    }
    
    @Override
    public void draw(float delta) {
        int[] gates = level.currentGates();
        up.set(camera.up);
        cameraDirection.set(camera.direction);
        // vector orthogonal to up and cameraDirection
        cross.set(up).crs(cameraDirection).nor();
        GameObject gate;
        batch.begin();
        for (int i = 0; i < gates.length; i++) {
            gate = level.getGateById(gates[i]).goal;
            if (!gate.isVisible(camera)) {
                tmp.set(cameraDirection).nor().scl(camera.near);
                midPoint.set(camera.position).add(tmp);
                vectorToTarget = projectPointToPlane(gate.getPosition(), cross, up).sub(camera.position);
                angle = angleBetweenTwoVectors(up, vectorToTarget);
                if (vectorToTarget.hasOppositeDirection(cross)) {
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
        x.set(planeX);
        y.set(planeY);
        y.scl(tmp1.set(point).dot(y) / y.len2());
        x.scl(tmp2.set(point).dot(x) / x.len2());
        return y.add(x);
    }
    
    public static float angleBetweenTwoVectors(Vector3 v1, Vector3 v2) {
        tmp1.set(v1);
        return (float) (Math.acos(tmp1.dot(v2) / (v1.len() * v2.len())) * 180 / Math.PI);
    }
}
