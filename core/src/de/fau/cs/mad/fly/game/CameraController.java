package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.Debug;
import de.fau.cs.mad.fly.player.Player;

public class CameraController implements InputProcessor {

	private boolean useSensorData;
	private boolean useRolling;
	private boolean useLowPass;
	private boolean useAveraging;

	private Player player;
	private PerspectiveCamera camera;

	private float startRoll, startAzimuth;

	private float rollDir = 0.0f;
	private float azimuthDir = 0.0f;

	private int currentEvent = -1;

	// variables for Sensor input smoothing
	private float alpha = 0.15f;
	private int bufferSize;
	private ArrayList<Float> rollInput;
	private ArrayList<Float> rollOutput;
	private ArrayList<Float> pitchInput;
	private ArrayList<Float> pitchOutput;
	private ArrayList<Float> azimuthInput;
	private ArrayList<Float> azimuthOutput;

	public CameraController(Player player) {
		this.player = player;
		
		this.useSensorData = !player.getSettingManager().getCheckBoxValue("useTouch");
		this.useRolling = player.getSettingManager().getCheckBoxValue("useRoll");
		this.useLowPass = player.getSettingManager().getCheckBoxValue("useLowPass");
		this.useAveraging = player.getSettingManager().getCheckBoxValue("useAveraging");

		this.bufferSize = (int) player.getSettingManager().getSliderValue("bufferSlider");
		this.alpha = player.getSettingManager().getSliderValue("alphaSlider") / 100.f;

		setUpCamera();
	}

	public PerspectiveCamera getCamera() {
		return camera;
	}

	public void setUseSensorData(boolean useSensorData) {
		this.useSensorData = useSensorData;
	}

	public void setUseRolling(boolean useRolling) {
		this.useRolling = useRolling;
	}

	public void setUseLowPass(boolean useLowPass) {
		this.useLowPass = useLowPass;
	}
	
	public void setUseAveraging(boolean useAveraging) {
		this.useAveraging = useAveraging;
	}
	
	public void setBufferSize(int bufferSize) {
		resetBuffers();
		
		this.bufferSize = bufferSize;
	
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getRollDir() {
		return rollDir;
	}

	public float getAzimuthDir() {
		return azimuthDir;
	}

	/**
	 * recomputes camera position and rotation
	 * 
	 * @param delta
	 *            - time since last frame
	 * @return
	 */
	public PerspectiveCamera recomputeCamera(float delta) {
		// rotating the camera according to UserInput
		if (useSensorData) {
			interpretSensorInput();
		}

		// rotating the camera
		rotateCamera(rollDir, azimuthDir);
		camera.update();

		// move the camera (first person flight)
		Vector3 dir = new Vector3(camera.direction.x, camera.direction.y,
				camera.direction.z);
		camera.translate(dir.scl(player.getPlane().getSpeed() * delta));
		camera.update();

		return camera;
	}

	/**
	 * Sets up the camera for the initial view.
	 */
	public void setUpCamera() {

		// initializing Roll- and Pitch-Values for later comparison
		float roll = Gdx.input.getRoll();
		float pitch = Gdx.input.getPitch();
		float azimuth = Gdx.input.getAzimuth();

		startAzimuth = computeAzimuth(roll, pitch, azimuth);
		startRoll = Gdx.input.getRoll();

		// setting up the camera
		float screenHeight = Gdx.graphics.getHeight();
		float screenWidth = Gdx.graphics.getWidth();
		camera = new PerspectiveCamera(67, screenWidth, screenHeight);

		camera.position.set(player.getLastLevel().start.position);
		camera.lookAt(player.getLastLevel().start.viewDirection);
		camera.near = 0.1f;
		// within a sphere it should not happen that not everything of this
		// sphere is displayed. Therefore use the diameter as far plane
		camera.far = player.getLastLevel().radius * 2;
		camera.update();

		resetBuffers();
	}

	private void resetBuffers() {
		rollInput = new ArrayList<Float>();
		rollOutput = new ArrayList<Float>();
		pitchInput = new ArrayList<Float>();
		pitchOutput = new ArrayList<Float>();
		azimuthInput = new ArrayList<Float>();
		azimuthOutput = new ArrayList<Float>();
	}

	/**
	 * Interprets the rotation of the smartphone and calls camera rotation
	 */
	private void interpretSensorInput() {
		float roll = Gdx.input.getRoll();
		float pitch = Gdx.input.getPitch();
		float azimuth = Gdx.input.getAzimuth();

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

		if (useLowPass) {
			rollOutput = lowPassFilter(rollInput, rollOutput, alpha);
			pitchOutput = lowPassFilter(pitchInput, pitchOutput, alpha);
			azimuthOutput = lowPassFilter(azimuthInput, azimuthOutput, alpha);
			
			if (useAveraging) {
				roll = average(rollOutput);
				pitch = average(pitchOutput);
				azimuth = average(azimuthOutput);
			}
		} else {
			if (useAveraging) {
				roll = average(rollInput);
				pitch = average(pitchInput);
				azimuth = average(azimuthInput);
			}
		}

		azimuth = computeAzimuth(roll, pitch, azimuth);

		float difRoll = roll - startRoll;
		if (Math.abs(difRoll) > 180) {
			difRoll -=  Math.signum(difRoll) * 360;
		}
		
		float difAzimuth = azimuth - startAzimuth;
		if (Math.abs(difAzimuth) > 180) {
			difAzimuth -=  Math.signum(difAzimuth) * 360;
		}
		

		// capping the rotation to a maximum of 90 degrees
		if (Math.abs(difRoll) > 90) {
			difRoll = 90 * Math.signum(difRoll);
		}

		if (Math.abs(difAzimuth) > 90) {
			difRoll = 90 * Math.signum(difAzimuth);
		}

		rollDir = 0.0f;
		azimuthDir = 0.0f;

		// camera rotation according to smartphone rotation
		setAzimuthDir(difAzimuth / -90.0f);
		setRollDir(difRoll / -90.0f);
	}

	/**
	 * Setter for the {@link #azimuthDir}. Values greater than the azimuthSpeed
	 * of the plane are reduced to the azimuth speed of the plane.
	 * 
	 * @param azimuthDir
	 */
	private void setAzimuthDir(float azimuthDir) {
		this.azimuthDir = limitSpeed(azimuthDir, player.getPlane()
				.getAzimuthSpeed());
	}
	
	/**
	 * Setter for the {@link #rollDir}. Values greater than the rollingSpeed
	 * of the plane are reduced to the azimuth speed of the plane.
	 * 
	 * @param rollDir
	 */
	private void setRollDir(float rollDir) {
		this.rollDir = limitSpeed(rollDir, player.getPlane()
				.getRollingSpeed());
	}

	/**
	 * Rotates the camera according to rollDir and pitchDir
	 * 
	 * @param rollDir
	 *            - defines if the camera should be rotated up or down
	 * @param azimuthDir
	 *            - defines if the camera should be rotated left or right
	 */
	private void rotateCamera(float rollDir, float azimuthDir) {
		// rotation up or down
		camera.rotate(camera.direction.cpy().crs(camera.up), 1.0f * rollDir);

		// rotation around camera.direction/viewDirection (roll)
		if (useRolling) {
			camera.rotate(camera.direction, 1.0f * -azimuthDir);
		} else {
			// rotation around camera.up (turning left/right)
			camera.rotate(camera.up, 1.0f * azimuthDir);
		}
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
		Matrix3 mX = new Matrix3();
		Matrix3 mY = new Matrix3();
		Matrix3 mZ = new Matrix3();

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

		Matrix3 mat = mZ.mul(mY.mul(mX));

		Vector3 newFront = new Vector3(0.f, 1.f, 0.f).mul(mat);

		Vector3 z = new Vector3(0.f, 0.f, 1.f);

		return (float) Math.acos(z.dot(new Vector3(newFront.x, newFront.y,
				newFront.z))
				/ (float) Math.sqrt(newFront.x * newFront.x + newFront.y
						* newFront.y + newFront.z * newFront.z))
				* 180.f / (float) Math.PI;
	}

	private ArrayList<Float> lowPassFilter(ArrayList<Float> input,
			ArrayList<Float> output, float alpha) {
		float result = 0.0f;

		/*
		 * if(output.size() <= 2){ return input; }
		 */

		if (output.size() < bufferSize) {
			output.add(0.0f);
			output.set(output.size() - 1, input.get(output.size() - 1));
		}

		for (int i = 1; i < output.size(); i++) {
			result = output.get(i) + alpha * (input.get(i) - output.get(i));
			output.set(i, result);
		}

		if (output.size() > bufferSize) {
			output.remove(0);
		}

		return output;
	}

	private float average(ArrayList<Float> input) {
		float result = 0.0f;

		for (int i = 0; i < input.size(); i++) {
			result += input.get(i);
		}

		return result / (float) input.size();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// using the touchscreen to rotate camera
		
		if (button == Buttons.LEFT && !useSensorData) {
			float width = (float) Gdx.graphics.getWidth();
			float height = (float) Gdx.graphics.getHeight();

			float xPosition = ((float) screenX) / width;
			float yPosition = ((float) screenY) / height;

			setAzimuthDir(0.5f - xPosition);
			setRollDir(0.5f - yPosition);

			currentEvent = pointer;
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// set camera rotation to 0 when finger is lifted from touchscreen
		if (button == Buttons.LEFT) {
			rollDir = 0;
			azimuthDir = 0;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// changing camera rotation when finger is dragged on the touchscreen
		if (pointer == currentEvent) {

			float width = (float) Gdx.graphics.getWidth();
			float height = (float) Gdx.graphics.getHeight();

			float xPosition = ((float) screenX) / width;
			float yPosition = ((float) screenY) / height;

			setAzimuthDir(0.5f - xPosition);
			setRollDir(0.5f - yPosition);
		}
		return false;
	}

	private float limitSpeed(float wantedSpeed, float speedLimit) {
		if (wantedSpeed > 0) {
			return Math.min(wantedSpeed, speedLimit);
		}
		return Math.max(wantedSpeed, (-1 * speedLimit));
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
