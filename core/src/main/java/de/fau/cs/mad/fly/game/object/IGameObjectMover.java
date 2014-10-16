package de.fau.cs.mad.fly.game.object;

import de.fau.cs.mad.fly.game.GameObject;

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
     * @param The delta since the last call.
     */
    public void move(float delta);
    
    /**
     * Returns a copy of the mover with the game object to move.
     * 
     * @param the game object for the new mover.
     * 
     * @return copy of mover.
     */
    public IGameObjectMover getCopy(GameObject gameObject);
    
    /**
     * Changes the activation status of the mover.
     * 
     * @param active		True, if the mover should be active, false otherwise.
     */
    public void setActive(boolean active);
}