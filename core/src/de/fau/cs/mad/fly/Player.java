package de.fau.cs.mad.fly;

import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.res.ResourceManager;

/**
 * Stores all player-specific information.
 * @author Lukas Hahmann
 *
 */
public class Player {
	
	/** The plane the player is currently steering */
	private IPlane plane;
	
	private Level lastLevel;
	//TODO: Player name
	//TODO: Settings
	
	public Player() {
		this.lastLevel = ResourceManager.getLevelList().get(0);
	}

	public Level getLastLevel() {
		return lastLevel;
	}

	public void setLastLevel(Level lastLevel) {
		this.lastLevel = lastLevel;
	}

	public IPlane getPlane() {
		return plane;
	}

	public void setPlane(IPlane plane) {
		this.plane = plane;
	}
}
