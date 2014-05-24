package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;

/**
 * Displays the 3D-world.
 * 
 * @author Tobias Zangl
 */
public class GameScreen implements Screen, InputProcessor {
	private final Fly game;

	private float startRoll, startAzimuth;

	private PerspectiveCamera camera;
	private float cameraSpeed = 2.0f;

	private float rollDir = 0.0f;
	private float azimuthDir = 0.0f;
	private boolean useSensorData;
	
	private GameOverlay gameOverlay;

	public GameScreen(final Fly game) {
		this.game = game;
		
		gameOverlay = new GameOverlay(game);

		useSensorData = !game.getSettingManager().getCheckBoxValue("useTouch");
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
		Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
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

		game.getLevel().render(camera);
		
		gameOverlay.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		useSensorData = !game.getSettingManager().getCheckBoxValue("useTouch");
		
		setUpCamera();
		
		game.getLevel().initLevel();
		
		gameOverlay.initOverlay();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}

	/**
	 * Sets up the camera for the initial view.
	 */
	private void setUpCamera() {

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

		camera.position.set(game.getLevel().start.position);
		camera.lookAt(game.getLevel().start.viewDirection);
		camera.near = 0.1f;
		// within a sphere it should not happen that not everything of this
		// sphere is displayed. Therefore use the diameter as far plane
		camera.far = game.getLevel().radius * 2;
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
		//camera.rotate(camera.direction, 1.0f * -azimuthDir);

		// rotation around camera.up (turning left/right)
		//camera.rotate(camera.up, 1.0f * pitchDir);
		camera.rotate(camera.up, 1.0f * azimuthDir);
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
		
		return (float) Math.acos(z.dot(new Vector3(0, newFront.y, newFront.z)) / (float) Math.sqrt(newFront.y * newFront.y + newFront.z * newFront.z)) * 180.f/ (float) Math.PI;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			game.setMainMenuScreen();
		}
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

	int currentEvent = -1;
	
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
