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
import de.fau.cs.mad.fly.res.GateCircuit;
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
     * Vector that points right, compared to the camera direction. It should
     * always have the length 1.
     */
    private Vector3 cross;
    /**
     * Local copy of the camera direction to calculate with.
     */
    private Vector3 midPoint;
    
    // the following 4 Vectors are made members, to avoid recreating them every
    // frame
    private static Vector3 x = new Vector3();
    private static Vector3 y = new Vector3();
    private static Vector3 tmp1 = new Vector3();
    private static Vector3 tmp2 = new Vector3();
    
    /** Local copy of the camera for performance reasons. */
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
    
    /** Height of the arrow to indicate the next gate */
    private final float arrowHeigth;
    
    /** Width of the arrow to indicate the next gate */
    private final float arrowWidth;
    
    /** X-Coordinate of origin relative to the arrow, used for rotation */
    private final float originX;
    
    /** Y-Coordinate of origin relative to the arrow, used for rotation */
    private final float originY;
    
    /** Rotation angle of the arrow. */
    private float angle = 0;
    
    /** Reference to the current gate. */
    private GameObject gate;
    
    /**
     * Flag which indicates weather batch.begin() has been called. Used to avoid
     * unnecessary batch calls in the case nothing has to be drawn.
     */
    private boolean batchBegin;
    
    
    
    public GateIndicator(final Skin skin) {
        this.arrow = skin.getRegion("arrow");
        this.arrowWidth = arrow.getRegionWidth();
        this.arrowHeigth = arrow.getRegionHeight();
        
        this.originX = arrow.getRegionWidth() / 2;
        this.originY = -radius;
        
        this.startPosX = horizontalCenter - arrow.getRegionWidth() / 2;
        this.midPoint = new Vector3();
        
        this.batch = new SpriteBatch();
    }
    
    @Override
    public void init(final GameController gameController) {
        this.level = gameController.getLevel();
        this.camera = gameController.getCamera();
        this.vectorToTarget = new Vector3();
        this.cross = new Vector3();
        this.batchBegin = false;
    }
    
    @Override
    public void draw(float delta) {
        // vector orthogonal to up and cameraDirection
        cross.set(camera.up).crs(camera.direction);
        GateCircuit gateCircuit = level.getGateCircuit();
        
        for (int i = 0; i < gateCircuit.currentGates().length; i++) {
            gate = gateCircuit.getGateById(gateCircuit.currentGates()[i]).goal;
            // only draw a gate indicator when the next gate is not visible. If
            // it is visible, the gate itself is highlighted
            if (!gate.isVisible(camera)) {
                // get the midpoint in the near plane to compute the vector to
                // the projected point
                midPoint.set(camera.direction).scl(camera.near).add(camera.position);
                vectorToTarget.set(projectPointToPlane(gate.getPosition(), cross, camera.up));
                vectorToTarget.sub(camera.position);
                angle = angleBetweenTwoVectors(camera.up, vectorToTarget);
                // as the angle is only computed from 0 to 180°, it is necessary
                // to flip the direction, if it has the other direction than the
                // reference vector. Otherwise the indicator would only point
                // left
                if (vectorToTarget.hasOppositeDirection(cross)) {
                    angle = 360 - angle;
                }
                if (!batchBegin) {
                    batch.begin();
                    batchBegin = true;
                }
                batch.draw(arrow, startPosX, startPosY, originX, originY, arrowWidth, arrowHeigth, 1f, 1f, angle);
            }
        }
        if (batchBegin) {
            batch.end();
            batchBegin = false;
        }
    }
    
    /**
     * Projects a point onto a plane.
     * <p>
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
