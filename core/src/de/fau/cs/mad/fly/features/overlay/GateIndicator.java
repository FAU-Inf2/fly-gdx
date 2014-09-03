package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.GateCircuit;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.UI;

/**
 * This class implements the function to show in the game small arrows that
 * indicate the direction of the next gates.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GateIndicator implements IFeatureInit, IFeatureDraw {
    
    /** Local copy of the gate position to calculate with. */
    private Vector3 gatePosition;
    
    /** Projected target point on near plane of the camera. */
    private Vector3 pointOnNearPlane;
    
    /** Points from camera.position to target */
    private Vector3 toGate;
    
    /** Vector that points upwards in 2d screen space. */
    private final static Vector3 screenUp = new Vector3(0, 1, 0);
    
    /** Vector that points right in 2d screen space. */
    private final static Vector3 screenRight = new Vector3(1, 0, 0);
    
    /**
     * Vector used for calculating the angle between two vectors. Made member to
     * avoid recreation every frame
     */
    private static Vector3 tmp1 = new Vector3();
    
    /** Local copy of the camera for performance reasons. */
    private Camera camera;
    
    /** Texture that is used to draw the indicator for the next gate */
    private final TextureRegion arrow;
    
    /** Factor to scale the arrow according to the resolution */
    private float scalingFactor;
    
    /** max Factor to scale the arrow according to the resolution */
    private float maxScalingFactor;
    
    /** min Factor to scale the arrow according to the resolution */
    private float minScalingFactor;
    
    /** Batch that is used for rendering the indicators */
    private final Batch batch;
    
    /** Private copy of the level for performance reasons */
    private Level level;
    
    /**
     * Distance from the arrows to the mid of the screen. It is limited by the
     * reset-steering button, because it should not overlay this button.
     */
    private static final int radius = 250;
    
    /**
     * Horizontal center of the screen. It defines the mid point of the circle
     * the arrows cover.
     */
    private final float horizontalCenter = (float) Gdx.graphics.getWidth() / 2.0f;
    
    /**
     * Vertical center of the screen. It defines the mid point of the circle the
     * arrows cover.
     */
    private final float verticalCenter = (float) Gdx.graphics.getHeight() / 2.0f;
    
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
    
    public GateIndicator(final TextureRegion arrow) {
        this.arrow = arrow;
        this.arrowWidth = arrow.getRegionWidth();
        this.arrowHeigth = arrow.getRegionHeight();
        this.originX = arrow.getRegionWidth() / 2;
        this.originY = -radius;
        this.startPosX = horizontalCenter - arrow.getRegionWidth() / 2;
        this.batch = new SpriteBatch();
        this.scalingFactor = Gdx.graphics.getWidth() / UI.Window.REFERENCE_WIDTH;
        this.maxScalingFactor = scalingFactor * 2f;
        this.minScalingFactor = scalingFactor;
        this.toGate = new Vector3();
    }
    
    @Override
    public void init(final GameController gameController) {
        this.level = gameController.getLevel();
        this.camera = gameController.getCamera();
    }
    
    @Override
    public void draw(float delta) {
        GateCircuit gateCircuit = level.getGateCircuit();
        int numberOfGates = gateCircuit.currentGates().length;
        boolean drawIndicator = true;
        int i = 0;
        
        // only show gate indicator when none of the next gates are visible
        for (; i < numberOfGates; i++) {
            GameObject gate = gateCircuit.getGateGoalById(gateCircuit.currentGates()[i]);
            if (gate.isVisible(camera)) {
                drawIndicator = false;
                i = numberOfGates;
            }
        }
        
        if (drawIndicator) {
            batch.begin();
            for (i = 0; i < numberOfGates; i++) {
                gatePosition = gateCircuit.getGateGoalById(gateCircuit.currentGates()[i]).getPosition();
                pointOnNearPlane = camera.project(gatePosition.cpy());
                pointOnNearPlane.set(pointOnNearPlane.x - Gdx.graphics.getWidth() / 2, pointOnNearPlane.y - Gdx.graphics.getHeight() / 2, 0);
                
                toGate.set(gatePosition).sub(camera.position);
                // flip the pointOnNearPlane because for angles > 90 the vector
                // is calculated wrong
                if (angleBetweenTwoVectors(camera.direction, toGate) > 90) {
                    pointOnNearPlane.scl(-1);
                }
                angle = angleBetweenTwoVectors(screenUp, pointOnNearPlane);
                
                // as the angle is only computed from 0 to 180°, it is
                // necessary to flip the direction, if it has the other
                // direction than the reference vector. Otherwise the indicator
                // would only point left
                if (pointOnNearPlane.hasSameDirection(screenRight)) {
                    angle = 360 - angle;
                }
                
                // calculate the size of the arrow according to the distance of
                // the target
                scalingFactor = 10/toGate.len();
                scalingFactor = Math.min(scalingFactor, maxScalingFactor);
                scalingFactor = Math.max(scalingFactor, minScalingFactor);
                batch.draw(arrow, startPosX, startPosY, originX, originY, arrowWidth, arrowHeigth, scalingFactor, scalingFactor, angle);
            }
            batch.end();
        }
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
