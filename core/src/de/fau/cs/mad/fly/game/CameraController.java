package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Player;

public class CameraController implements InputProcessor {
	
	private boolean useSensorData;
	private boolean useRolling;
	
	private Player player;
	private PerspectiveCamera camera;

	private float startRoll, startAzimuth;
	private float cameraSpeed = 2.0f;

	private float rollDir = 0.0f;
	private float azimuthDir = 0.0f;

	private int currentEvent = -1;
	
	public CameraController(boolean useSensorData, Player player){
		this.useSensorData = useSensorData;
		this.player = player;
		
		useRolling = player.getSettingManager().getCheckBoxValue("useRoll");
		
		setUpCamera();
	}
	
	public PerspectiveCamera getCamera(){
		return camera;
	}
	
	public void setUseSensorData(boolean useSensorData){
		this.useSensorData = useSensorData;
	}
	
	public void setUseRolling(boolean useRolling){
		this.useRolling = useRolling;
	}
	
	public PerspectiveCamera recomputeCamera(float delta){
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
		camera.translate(dir.scl(cameraSpeed * delta));
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

	}

	/**
	 * Interprets the rotation of the smartphone and rotates the camera
	 * accordingly
	 */
	private void interpretSensorInput() {
		float roll = Gdx.input.getRoll();
		float pitch = Gdx.input.getPitch();
		float azimuth = Gdx.input.getAzimuth();
		
		azimuth = computeAzimuth(roll, pitch, azimuth);

		float difRoll = roll - startRoll;
		float difAzimuth = azimuth - startAzimuth;

		rollDir = 0.0f;
		azimuthDir = 0.0f;

		// camera rotation according to smartphone rotation
		rollDir = difRoll * -0.1f;
		azimuthDir = difAzimuth * -0.1f;
	}

	/**
	 * Rotates the camera according to rollDir and pitchDir
	 * 
	 * @param rollDir
	 *            defines if the camera should be rotated up or down
	 * @param pitchDir
	 *            defines if the camera should be rotated left or right
	 */
	private void rotateCamera(float rollDir, float azimuthDir) {
		// rotation up or down
		camera.rotate(camera.direction.cpy().crs(camera.up), 1.0f * rollDir);

		// rotation around camera.direction/viewDirection (roll)
		if(useRolling) {
			camera.rotate(camera.direction, 1.0f * -azimuthDir);
		} else {
			// rotation around camera.up (turning left/right)
			//camera.rotate(camera.up, 1.0f * pitchDir);
			camera.rotate(camera.up, 1.0f * azimuthDir);
		}
	}
	
	private float computeAzimuth(float roll, float pitch, float azimuth){
		Matrix3 mX = new Matrix3();
		Matrix3 mY = new Matrix3();
		Matrix3 mZ = new Matrix3();
		
		roll = roll * (float) Math.PI / 180.f;
		pitch = pitch * (float) Math.PI / 180.f;
		azimuth = azimuth * (float) Math.PI / 180.f;
		
		float cos = (float) Math.cos(pitch);
		float sin = (float) Math.sin(pitch);
		
		float[] values = {1.f,0.f,0.f,   0.f, cos, sin,   0.f, -sin, cos};
		mY.set(values);
		
		cos = (float) Math.cos(roll);
		sin = (float) Math.sin(roll);
		float[] values2 = {cos,0.f,-sin,   0.f, 1.f, 0.f,   sin, 0.f, cos};
		mX.set(values2);
		
		cos = (float) Math.cos(azimuth);
		sin = (float) Math.sin(azimuth);
		float[] values3 = {cos,sin,0.f,   -sin, cos, 0.f,   0.f, 0.f, 1.f};
		mZ.set(values3);
		
		Matrix3 mat = mZ.mul(mY.mul(mX));
		
		Vector3 newFront = new Vector3(0.f,1.f,0.f).mul(mat);
		
		Vector3 z = new Vector3(0.f,0.f,1.f);
		
		return (float) Math.acos(z.dot(new Vector3(newFront.x, newFront.y, newFront.z)) / (float) Math.sqrt(newFront.x * newFront.x + newFront.y * newFront.y + newFront.z * newFront.z)) * 180.f/ (float) Math.PI;
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
		// TODO Auto-generated method stub
		
		if (button == Buttons.LEFT && !useSensorData) {

			float width = (float) Gdx.graphics.getWidth();
			float height = (float) Gdx.graphics.getHeight();

			float xPosition = ((float) screenX) / width;
			float yPosition = ((float) screenY) / height;

			azimuthDir = 5 * (0.5f - xPosition);
			rollDir = 5 * (0.5f - yPosition);
			
			currentEvent = pointer;
		}

		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		if (button == Buttons.LEFT) {
			rollDir = 0;
			azimuthDir = 0;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		if (pointer == currentEvent) {

			float width = (float) Gdx.graphics.getWidth();
			float height = (float) Gdx.graphics.getHeight();

			float xPosition = ((float) screenX) / width;
			float yPosition = ((float) screenY) / height;
			
			azimuthDir = 5 * (0.5f - xPosition);
			rollDir = 5 * (0.5f - yPosition);
		}
		return false;
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
