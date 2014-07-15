package de.fau.cs.mad.fly.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.overlay.TouchScreenOverlay;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * controls the flight of the player regarding to user-input 
 * @author Sebastian
 *
 */
public class FlightController implements InputProcessor {
    
    private boolean useSensorData;
    private boolean useRolling;
    private boolean inTouch = false;
    
    private Player player;
    
    private float startRoll, startAzimuth;
    
    private float rollFactor = 0.0f;
    private float azimuthFactor = 0.0f;
    
    private int currentEvent = -1;
    
    private float screenHeight = Gdx.graphics.getHeight();
    private float screenWidth = Gdx.graphics.getWidth();
    
    // variables for Sensor input smoothing
    private int bufferSize;
    private List<Float> rollInput;
    private List<Float> pitchInput;
    private List<Float> azimuthInput;
    
    private float roll;
    private float pitch;
    private float azimuth;
    
    float maxRotate = 45.f;
    
    private float centerX = TouchScreenOverlay.X_POS_OF_STEERING_CIRCLE + screenWidth / 2;
    private float centerY = -TouchScreenOverlay.Y_POS_OF_STEERING_CIRCLE + screenHeight / 2;
    private float radius = TouchScreenOverlay.RADIUS_OF_STEERING_CIRCLE;
    
    private Matrix3 mX;
    private Matrix3 mY;
    private Matrix3 mZ;
    
    private Vector3 newFront;
    
    private static final Vector3 z = new Vector3(0.f, 0.f, 1.f);
    
    public FlightController(Player player) {
        this.player = player;
        
        Preferences preferences = player.getSettingManager().getPreferences();
        this.useSensorData = !preferences.getBoolean(SettingManager.USE_TOUCH);
        this.useRolling = preferences.getBoolean(SettingManager.USE_ROLL_STEERING);
        
        this.bufferSize = 8;
        
        player.getPlane().setRolling(useRolling);
        
        mX = new Matrix3();
        mY = new Matrix3();
        mZ = new Matrix3();
        
        newFront = new Vector3();
        
        resetSteering();
        resetBuffers();
    }
    
    /**
     * sets the parameter that indicates whether the player wants to control the game by sensor or touch-screen
     * @param useSensorData	      true if sensor-values should be used, false if touch-screen should be used
     */
    public void setUseSensorData(boolean useSensorData) {
        this.useSensorData = useSensorData;
    }
    
    /**
     * sets the parameter that indicates whether the player should roll or fly curves
     * @param useRolling	true if player should roll instead of flying curves
     */
    public void setUseRolling(boolean useRolling) {
        this.useRolling = useRolling;
        player.getPlane().setRolling(useRolling);
    }
    
    /**
     * setter for the size of buffers used for averaging the sensorvalues
     * @param bufferSize
     */
    public void setBufferSize(int bufferSize) {
        resetBuffers();
        
        this.bufferSize = bufferSize;
        
    }
    
    /**
     * getter for the factor by which the player rotate up/down
     * @return
     */
    public float getRollFactor() {
        return rollFactor;
    }
    
    /**
     * getter for the factor by which the player should fly curves
     * @return
     */
    public float getAzimuthFactor() {
        return azimuthFactor;
    }
    
    public void resetSteering() {
        float roll = Gdx.input.getRoll();
        float pitch = Gdx.input.getPitch();
        float azimuth = Gdx.input.getAzimuth();
        
        Gdx.app.log("FlightController.resetSteering", "roll=" + roll + " pitch=" + pitch + " azimuth=" + azimuth);
        startAzimuth = computeAzimuth(roll, pitch, azimuth);
        Gdx.app.log("FlightController.resetSteering", "roll=" + roll + " pitch=" + pitch + " azimuth=" + azimuth);
        startRoll = Gdx.input.getRoll();
    }
    
    /**
	 * computes the new position to fly to regarding to the userinput
	 * @param delta		time since last frame
	 */
    public void update(float delta) {
        // rotating the camera according to UserInput
        if (useSensorData) {
            interpretSensorInput();
        }
        player.getPlane().rotate(rollFactor, azimuthFactor, delta * 60.f);
    }
    
    private void resetBuffers() {
        rollInput = new ArrayList<Float>();
        pitchInput = new ArrayList<Float>();
        azimuthInput = new ArrayList<Float>();
    }
    
    /**
     * Interprets the rotation of the smartphone
     */
    private void interpretSensorInput() {
        roll = Gdx.input.getRoll();
        pitch = Gdx.input.getPitch();
        azimuth = Gdx.input.getAzimuth();
        
        // Gdx.app.log("myApp", "roll: " + roll + "; pitch: " + pitch +
        // "; azimuth: " + azimuth);
        
        // removing oldest element in buffers
        if (rollInput.size() >= bufferSize) {
            rollInput.remove(0);
            pitchInput.remove(0);
            azimuthInput.remove(0);
        }
        
        // adding newest sensor-data to buffers
        rollInput.add(roll);
        pitchInput.add(pitch);
        azimuthInput.add(azimuth);
        
        roll = average(rollInput);
        pitch = average(pitchInput);
        azimuth = average(azimuthInput);
        
        azimuth = computeAzimuth(roll, pitch, azimuth);
        
        float difRoll = roll - startRoll;
        if (Math.abs(difRoll) > 180) {
            difRoll -= Math.signum(difRoll) * 360;
        }
        
        float difAzimuth = azimuth - startAzimuth;
        if (Math.abs(difAzimuth) > 180) {
            difAzimuth -= Math.signum(difAzimuth) * 360;
        }
        
        // capping the rotation to a maximum of 90 degrees
        if (Math.abs(difRoll) > maxRotate) {
            difRoll = maxRotate * Math.signum(difRoll);
        }
        
        if (Math.abs(difAzimuth) > maxRotate) {
            difAzimuth = maxRotate * Math.signum(difAzimuth);
        }
        
        rollFactor = 0.0f;
        azimuthFactor = 0.0f;
        
        // camera rotation according to smartphone rotation
        setAzimuthFactor(difAzimuth / -maxRotate);
        setRollFactor(difRoll / -maxRotate);
    }
    
    /**
     * Setter for the {@link #azimuthDir}. Values greater than the azimuthSpeed
     * of the plane are reduced to the azimuth speed of the plane.
     * 
     * @param azimuthFactor
     */
    private void setAzimuthFactor(float azimuthFactor) {
        this.azimuthFactor = limitSpeed(azimuthFactor, player.getPlane().getAzimuthSpeed());
    }
    
    /**
     * Setter for the {@link #rollDir}. Values greater than the rollingSpeed of
     * the plane are reduced to the azimuth speed of the plane.
     * 
     * @param rollFactor
     */
    private void setRollFactor(float rollFactor) {
        this.rollFactor = limitSpeed(rollFactor, player.getPlane().getRollingSpeed());
    }
    
    /**
     * computes the rotation around z-Axis relative to the smartphone
     * 
     * @param roll
     * @param pitch
     * @param azimuth
     * @return
     */
    private float computeAzimuth(float roll, float pitch, float azimuth) {
        mX.idt();
        mY.idt();
        mZ.idt();
        
        roll = roll * (float) Math.PI / 180.f;
        pitch = pitch * (float) Math.PI / 180.f;
        azimuth = azimuth * (float) Math.PI / 180.f;
        
        float cos = (float) Math.cos(pitch);
        float sin = (float) Math.sin(pitch);
        
        float[] values = { 1.f, 0.f, 0.f, 0.f, cos, sin, 0.f, -sin, cos };
        mY.set(values);
        
        cos = (float) Math.cos(roll);
        sin = (float) Math.sin(roll);
        float[] values2 = { cos, 0.f, -sin, 0.f, 1.f, 0.f, sin, 0.f, cos };
        mX.set(values2);
        
        cos = (float) Math.cos(azimuth);
        sin = (float) Math.sin(azimuth);
        float[] values3 = { cos, sin, 0.f, -sin, cos, 0.f, 0.f, 0.f, 1.f };
        mZ.set(values3);
        
        newFront.set(0.f, 1.f, 0.f).mul(mZ.mul(mY.mul(mX)));
        
        return (float) Math.acos(z.dot(newFront.x, newFront.y, newFront.z) / (float) Math.sqrt(newFront.x * newFront.x + newFront.y * newFront.y + newFront.z * newFront.z)) * 180.f / (float) Math.PI;
    }
    
    private float average(List<Float> input) {
        float result = 0.0f;
        int size = input.size();
        for (int i = 0; i < size; i++) {
            result += input.get(i);
        }
        
        return result / (float) input.size();
    }
    
    @Override
    public boolean keyDown(int keycode) {
        // nothing should happen here
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        // nothing should happen here
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        // nothing should happen here
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // using the touchscreen to rotate camera
        
        if (button == Buttons.LEFT && !useSensorData) {
            
            float xDif = screenX - centerX;
            float yDif = screenY - centerY;
            float length = (float) Math.sqrt(xDif * xDif + yDif * yDif);
            
            if (length <= radius) {
                setAzimuthFactor(-xDif / screenWidth / 0.075f);
                setRollFactor(-yDif / screenHeight / 0.075f);
                inTouch = true;
            } else {
            	inTouch = false;
            }
            
            currentEvent = pointer;
        } else {
            setAzimuthFactor(0);
            setRollFactor(0);
        }
        
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // set camera rotation to 0 when finger is lifted from touchscreen
        if (button == Buttons.LEFT) {
            rollFactor = 0;
            azimuthFactor = 0;
        }
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // changing camera rotation when finger is dragged on the touchscreen
        if (pointer == currentEvent) {
            
            float xDif = screenX - centerX;
            float yDif = screenY - centerY;
            float length = (float) Math.sqrt(xDif * xDif + yDif * yDif);
            
            if (length <= radius) {
            	setAzimuthFactor(-xDif / screenWidth / 0.075f);
            	setRollFactor(-yDif / screenHeight / 0.075f);
            } else if(inTouch) {
            	setAzimuthFactor(-xDif / length * radius / screenWidth / 0.075f);
            	setRollFactor(-yDif / length * radius / screenHeight / 0.075f);
            }
            
        }
        return false;
    }
    
    private float limitSpeed(float wantedSpeed, float speedLimit) {
        if (wantedSpeed > 0) {
            return Math.min(wantedSpeed, speedLimit);
        }
        return Math.max(wantedSpeed, -speedLimit);
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // nothing should happen here
        return false;
    }
    
    @Override
    public boolean scrolled(int amount) {
        // nothing should happen here
        return false;
    }
}
