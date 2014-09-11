package de.fau.cs.mad.fly.game.object;

/**
 * Interface for game object mover which move the game object in every frame
 * with specific parameter.
 * 
 * @author Tobi
 * 
 */
public interface IGameObjectMover {
    /**
     * Called every frame and moves the object.
     * 
     * @param The
     *            delta since the last call.
     */
    public void move(float delta);
}
