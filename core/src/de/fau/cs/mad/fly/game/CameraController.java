package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.player.Player;

/**
 * controls the camera following the player
 * @author Sebastian
 *
 */
public class CameraController {
	
	private PerspectiveCamera camera;
	private float cameraOffset;
	private Player player;
	
	private float screenHeight = Gdx.graphics.getHeight();
	private float screenWidth = Gdx.graphics.getWidth();
	
	public CameraController(Player player) {
		this.player = player;
		
		this.cameraOffset = 0.2f;
		
		setUpCamera();
	}
	
	/**
	 * getter for the camera
	 * @return	the camera following the player
	 */
	public PerspectiveCamera getCamera() {
		return camera;
	}
	
	/**
	 * computes new Position for the camera in 3rd person view
	 * @return		the PersectiveCamera on the new position
	 */
	public PerspectiveCamera updateCamera() {
		
		float[] values = player.getPlane().getInstance().transform.getValues();
		Vector3 newDir = new Vector3(values[8], values[9], values[10]).nor();
		camera.direction.set(newDir).nor();
		Vector3 newUp = new Vector3(values[4], values[5], values[6]).nor();
		camera.up.set(newUp);
		
		Vector3 dir = new Vector3(camera.direction.x, camera.direction.y, camera.direction.z);
		
		camera.position.set(player.getPlane().getPosition().cpy().sub(dir.scl(2.f /* * cameraOffset*/)));
		camera.position.add(camera.up.cpy().scl(0.3f + cameraOffset));

		camera.update();
		
		return camera;
	}

	/**
	 * Sets up the camera for the initial view.
	 */
	private final void setUpCamera() {
		camera = new PerspectiveCamera(67, screenWidth, screenHeight);

		camera.position.set(player.getLevel().start.position);
		camera.lookAt(player.getLevel().start.viewDirection);
		camera.near = 0.1f;
		camera.far = player.getLevel().radius * 2;
		camera.update();
	}
}
