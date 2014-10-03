package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.res.Assets;

/**
 * The player in the level.
 * <p>
 * Stores all information about the player like the currently used plane and his
 * lives.
 * 
 * @author Lukas Hahmann
 * 
 */
public class Player {
    
    /** The plane the player is currently steering */
    private IPlane plane;
    
    /**
     * The lives the player has at the moment. If lives is lower or equal zero
     * the player is dead.
     */
    private int lives;
    
    /**
     * Creates the player for the game.
     * <p>
     * Should be only created once for every level start.
     */
    public Player() {
        IPlane.Head head = PlaneManager.getInstance().getChosenPlane();
        
        String ref = "models/planes/" + head.modelRef + "/" + head.modelRef;
        Assets.load(new AssetDescriptor<GameModel>(ref, GameModel.class));
        GameModel model = Assets.manager.get(ref, GameModel.class);
        
        this.plane = new Spaceship(model, head);
        
        setLives(plane.getHead().lives);
    }
    
    /**
     * Getter for the lives of the player.
     * 
     * @return lives
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * Decreases the live by 1 if the player has enough lives, otherwise he has
     * 0 lives left.
     * 
     * @return true, if lifes can be decreased (>1) false otherwise.
     */
    public boolean decreaseLives() {
        if (lives > 1) {
            lives--;
            return true;
        } else {
            lives = 0;
            return false;
        }
    }
    
    /**
     * Returns if the player is dead or alive.
     * 
     * @return true if the player is dead because he has 0 lives left, false
     *         otherwise.
     */
    public boolean isDead() {
        return lives <= 0;
    }
    
    /**
     * Setter for the lives.
     * 
     * @param lives
     */
    public void setLives(int lives) {
        this.lives = lives;
    }
    
    /**
     * Getter for the plane of the player.
     * 
     * @return plane
     */
    public IPlane getPlane() {
        return plane;
    }
    
    /**
     * Setter for the plane of the player.
     * 
     * @param plane
     */
    public void setPlane(IPlane plane) {
        this.plane = plane;
    }
}