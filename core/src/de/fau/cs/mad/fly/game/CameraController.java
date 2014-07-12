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
	private final float cameraOffset;
	private final float cameraDistance;
	private Player player;
	
	private float screenHeight = Gdx.graphics.getHeight();
	private float screenWidth = Gdx.graphics.getWidth();
	private float[] values;
	
	public CameraController(Player player) {
		this.player = player;
		
		this.cameraOffset = 0.5f;
		this.cameraDistance = 2.0f;
		
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
		
		values = player.getPlane().getInstance().transform.getValues();
		camera.direction.set(values[8], values[9], values[10]).nor();
		camera.up.set(values[4], values[5], values[6]).nor();
		
		camera.position.set(player.getPlane().getPosition().cpy().sub(camera.direction.cpy().scl(cameraDistance)));
		camera.position.add(camera.up.x*cameraOffset, camera.up.y*cameraOffset, camera.up.z*cameraOffset);

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
