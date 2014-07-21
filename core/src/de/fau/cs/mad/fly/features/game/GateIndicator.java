package de.fau.cs.mad.fly.features.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Level;

/**
 * This class implements the function to show in the game small arrows that
 * indicate the direction of the next gates.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GateIndicator implements IFeatureInit, IFeatureDraw {
    
    /**
     * Vector that points from the midpoint of the near plane to the projected
     * point on the near plane.
     */
    private Vector3 vectorToTarget;
    /**
     * Vector that points right, compared to the camera direction. It should always have the length 1.
     */
    private Vector3 cross;
    /**
     * Local copy of the camera direction to calculate with.
     */
    private Vector3 cameraDirection;
    private Vector3 up;
    private Vector3 midPoint;
    
    // the following 4 Vectors are made members, to avoid recreating them every
    // frame
    private static Vector3 x = new Vector3();
    private static Vector3 y = new Vector3();
    private static Vector3 tmp1 = new Vector3();
    private static Vector3 tmp2 = new Vector3();
    
    private Camera camera;
    
    /** Texture that is used to draw the indicator for the next gate */
    private final TextureRegion arrow;
    /** Batch that is used for rendering the indicators */
    private final Batch batch;
    
    /** Private copy of the level for performance reasons */
    private Level level;
    
    /**
     * Distance from the arrows to the mid of the screen. It is limited by the
     * reset-steering button, because it should not overlay this button.
     */
    private final int radius = 250;
    
    /**
     * Horizontal center of the screen. It defines the mid point of the circle
     * the arrows cover.
     */
    private final float horizontalCenter = Gdx.graphics.getWidth() / 2;
    /**
     * Vertical center of the screen. It defines the mid point of the circle the
     * arrows cover.
     */
    private final float verticalCenter = Gdx.graphics.getHeight() / 2;
    /**
     * Horizontal starting position, where the arrow is placed when angle = 0°
     */
    private final float startPosX;
    /**
     * Vertical starting position, where the arrow is placed when angle = 0°
     */
    private final float startPosY = verticalCenter + radius;
    
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
        startPosX = horizontalCenter - arrow.getRegionWidth() / 2;
        midPoint = new Vector3();
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
            // only draw a gate indicator when the next gate is not visible. If
            // it is visible, the gate itself is highlighted
            if (!gate.isVisible(camera)) {
                // get the midpoint in the near plane to compute the vector to
                // the projected point
                midPoint.set(camera.position).add(cameraDirection.scl(camera.near));
                vectorToTarget = projectPointToPlane(gate.getPosition(), cross, up).sub(camera.position);
                angle = angleBetweenTwoVectors(up, vectorToTarget);
                // as the angle is only computed from 0 to 180°, it is necessary
                // to flip the direction, if it has the other direction than the
                // reference vector. Otherwise the indicator would only point
                // left
                if (vectorToTarget.hasOppositeDirection(cross)) {
                    angle = 360 - angle;
                }
                batch.draw(arrow, startPosX, startPosY, originX, originY, arrowWidth, arrowHeigth, 1f, 1f, angle);
            }
        }
        batch.end();
    }
    
    /**
     * A plane is defined by the two plane-vectors. The point outside the plane
     * is projected orthogonally to the plane on the plane. The result is a
     * point the plane with the smallest distance to the given point outside the
     * plane.
     * <p>
     * The order of the plane vectors does not matter.
     * 
     * @param planeX
     *            one of the vectors defining the plane
     * @param planeY
     *            the other vector defining the plane
     * @param pointOutsideThePlane
     *            point that is projected onto the plane
     */
    public static Vector3 projectPointToPlane(Vector3 pointOutsideThePlane, Vector3 planeX, Vector3 planeY) {
        x.set(planeX);
        y.set(planeY);
        y.scl(tmp1.set(pointOutsideThePlane).dot(y) / y.len2());
        x.scl(tmp2.set(pointOutsideThePlane).dot(x) / x.len2());
        return y.add(x);
    }
    
    /**
     * Calculates the angle between two vectors.
     * <p>
     * Value is always positive. The order of vectors does not matter.
     * 
     * @param v1
     *            one vector
     * @param v2
     *            other vector the vector in between is calculated
     * @return angle in degrees (between 0 and 180°)
     */
    public static float angleBetweenTwoVectors(Vector3 v1, Vector3 v2) {
        tmp1.set(v1);
        return (float) (Math.acos(tmp1.dot(v2) / (v1.len() * v2.len())) * 180 / Math.PI);
    }
}
