package de.fau.cs.mad.fly.game.object;

/**
 * Game object mover which does not move the game object.
 * 
 * @author Tobi
 *
 */
public class EmptyMover implements IGameObjectMover {
	
	public EmptyMover() {
	}

	@Override
	public void move(float delta) {
		// nothing to do because the EmptyMover does not move the game object at all.
	}

}