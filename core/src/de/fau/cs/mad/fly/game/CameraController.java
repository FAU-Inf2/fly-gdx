package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.PlayerProfile;

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
	private PlayerProfile playerProfile;
	
	private float screenHeight = Gdx.graphics.getHeight();
	private float screenWidth = Gdx.graphics.getWidth();
	private float[] values;
	
	public CameraController(Player player, PlayerProfile playerProfile) {
		this.player = player;
		this.playerProfile = playerProfile;
		
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
		
		values = player.getPlane().getTransform().getValues();
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

		camera.position.set(Loader.getInstance().getCurrentLevel().start.position);
		camera.lookAt(Loader.getInstance().getCurrentLevel().start.viewDirection);
		camera.near = 0.1f;
		camera.far = Loader.getInstance().getCurrentLevel().radius * 2;
		camera.update();
	}
}
