package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

import de.fau.cs.mad.fly.features.upgrades.ChangePointsUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.ChangeTimeUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.types.ChangePointsUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.ChangeTimeUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.InstantSpeedUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.LinearSpeedUpgrade;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.GateDisplay;
import de.fau.cs.mad.fly.res.GateGoal;
import de.fau.cs.mad.fly.res.Level;

/**
 * 
 * @author Sebastian
 *
 */
public class EndlessRailLevelGenerator extends EndlessLevelGenerator {
	
	private List<Vector3> centerRail;
	private float railOffset = 1.f;
	
	private GateGoal lastGate;
	
	private int asteroidCount = 0;
	
    protected ChangePointsUpgradeHandler changePointsHandler;
    
    private int lastUpgrade;
	private int stepsSinceLastGate = 0;
	private int stepsSinceLastUpgrade = 10;
	private int stepsSinceLastPointUpgrade = 10;
	private int stepsSinceLastTimeUpgrade = 10;
	private int stepsSinceLastSpeedUpgrade = 10;
	private int stepsSinceLastAsteroid = 10;
    private Vector3 lastUpgradePos = new Vector3(0,0,0);
    private Vector3 lastPointUpgradePos = new Vector3(0,0,0);
    private Vector3 lastGatePos = new Vector3(0,0,0);
    private Vector3 lastAsteroidPos = new Vector3(2,0,0);
    
    private int currentRailEndPoint = 0;
    private int currentRailStartPoint = 0;
    private Map<Integer,GameObject> objects;
    private Map<Integer,Collectible> collectibles;
    private Map<Integer,GateGoal> gates;

	public EndlessRailLevelGenerator(Level level, GameControllerBuilder builder) {
		super(level, builder);
		
		//centerRail = new ArrayList<Vector3>();
		
		manager.load(new AssetDescriptor<GameModel>("models/asteroid/asteroid", GameModel.class));
		manager.load(new AssetDescriptor<GameModel>("models/pointsUpgrade/pointsUpgrade", GameModel.class));
		
		objects = new HashMap<Integer,GameObject>();
		collectibles = new HashMap<Integer,Collectible>();
		gates = new HashMap<Integer,GateGoal>();
		
		this.changePointsHandler = new ChangePointsUpgradeHandler();
		builder.addFeatureToLists(changePointsHandler);
	}
	
	/**
	 * Adds random Components to the newest RailSegment
	 */
	public void addRandomComponents() {
		
		int random = MathUtils.random(15);
		if((random - stepsSinceLastAsteroid) < 0/* && stepsSinceLastAsteroid > 3*/) {
			addRandomAsteroid();
		}
		
		if(stepsSinceLastGate >= 10) {
			addRandomGate();
		}
		
		addRandomUpgrade();
		
		stepsSinceLastGate++;
		stepsSinceLastUpgrade++;
		stepsSinceLastPointUpgrade++;
		stepsSinceLastTimeUpgrade++;
		stepsSinceLastSpeedUpgrade++;
		stepsSinceLastAsteroid++;
	}
	
	/**
	 * Removes all Objects on of the railSegment 
	 * @param railPosition    indicates the position of the railsSegment which has to be removed
	 */
	public void removeComponents(Vector3 railPosition) {
		GameObject o = objects.get(currentRailStartPoint);
		if(o !=  null) {
			level.removeComponent(o);
			objects.remove(currentRailStartPoint);
		}
		
		Collectible c = collectibles.get(currentRailStartPoint);
		if(c !=  null) {
			level.getCollectibleManager().removeCollectible(c);
			collectibles.remove(currentRailStartPoint);
		}
		
		GateGoal g = gates.get(currentRailStartPoint);
		if(g !=  null) {
			level.getGateCircuit().removeGate(g);
			//gate is not passed but the player flew past it
			level.getGateCircuit().setVirtualGate(g);
			gates.remove(currentRailStartPoint);
		}
		
		currentRailStartPoint++;
	}
	
	
	private GateGoal addRandomGate() {
		
        CollisionDetector collisionDetector = CollisionDetector.getInstance();
        
        GateDisplay newDisplay = new GateDisplay(level.getDependency("torus"));
		GateGoal newGoal = new GateGoal(currGate, level.getDependency("hole"), defaultGateScore, newDisplay);
		newDisplay.setGoal(newGoal);
		 
		Vector3 railOffset = addGateRailOffset();
    	
    	if(stepsSinceLastAsteroid <= 3) {
    		if(railOffset.equals(lastAsteroidPos)) {
    			return null;
    		}
    	}
        /*if(railOffset.equals(stepsSinceLastAsteroid)) {
        	return null;
        }*/
        
		Vector3 gatePositon = centerRail.get(centerRail.size()-1).cpy().add(railOffset);
		newDisplay.transform.setToTranslation(gatePositon);
		newGoal.transform = newDisplay.transform.cpy();
		 
		newGoal.createShapeAndRigidBody(collisionDetector);
		newDisplay.createShapeAndRigidBody(collisionDetector);
		
		
        level.getGateCircuit().addGate(newGoal);
        gates.put(currentRailEndPoint, newGoal);
		
        if(lastGate != null) {
        	int[] successors = {currGate};
        	lastGate.setSuccessors(successors);
        } 
		
		Gdx.app.log("rails", "addGate: " + currGate + " an Position: " + gatePositon);
		 
		currGate++;
		
		lastGatePos = railOffset;
		lastGate = newGoal;
		stepsSinceLastGate = 0;
		return newGoal;
	}
	
	/**
     * Adds a random Upgrade to the Level
     * 
     * @param matrix
     *            - The average of the transformation matrices of the
     *            predecessor Gates
     * @param distance
     *            - The distance from the latest generated Gate to its
     *            predecessors
     */
    private void addRandomUpgrade() {
        int random = MathUtils.random(7);
        
        Collectible c = null;
        
        float speedFactor = MathUtils.random(0.1f, 0.5f);
        
        if(stepsSinceLastGate == 0) {
        	return;
        }
        
        switch (random) {
        case 0:
        	if(stepsSinceLastTimeUpgrade <= 20 || stepsSinceLastSpeedUpgrade <= 20) {
        		return;
        	}
            c = new ChangeTimeUpgrade(manager.get("models/timeUpgrade/timeUpgrade", GameModel.class), 5);
            changeTimeHandler.addObject(c);
            break;
        case 1:
        	if(stepsSinceLastTimeUpgrade <= 20 || stepsSinceLastSpeedUpgrade <= 20) {
        		return;
        	}
            c = new InstantSpeedUpgrade(manager.get("models/speedUpgrade/speedUpgrade", GameModel.class), speedFactor + 1.f, 10.f);
            instantSpeedHandler.addObject(c);
            break;
        case 2:
        case 3:
        case 4:
        	c = new ChangePointsUpgrade(manager.get("models/pointsUpgrade/pointsUpgrade", GameModel.class), 50);
        	changePointsHandler.addObject(c);
        	random = 2;
            break;
        default:
            break;
        }
        
        if (c != null) {
        	Vector3 railOffset;
        	/*if(lastUpgrade == 2 && random == 2 && stepsSinceLastPointUpgrade <= 5) {
        		// setting pointsUpgrades right behind each other
        		railOffset = lastUpgradePos;
        	} else {*/
        		//railOffset = addUpgradeRailOffset();
        	//}
        	
        	if(random == 2) {
        		railOffset = addPointUpgradeRailOffset();
        	} else {
        		railOffset = addRailOffset();
        	}
        	
        	if(stepsSinceLastAsteroid <= 3) {
        		if(railOffset.equals(lastAsteroidPos)) {
        			return;
        		}
        	}
        	
        	Vector3 upgradePositon = centerRail.get(centerRail.size()-1).cpy().add(railOffset);
    		c.transform.setToTranslation(upgradePositon);
            
            CollisionDetector collisionDetector = CollisionDetector.getInstance();
            c.createShapeAndRigidBody(collisionDetector, c.getType());
            
            level.getCollectibleManager().addCollectible(c);
            collectibles.put(currentRailEndPoint, c);

            switch (random) {
            case 0:
            	stepsSinceLastTimeUpgrade = 0;
                break;
            case 1:
            	stepsSinceLastSpeedUpgrade = 0;
                break;
            case 2:
            	stepsSinceLastPointUpgrade = 0;
                break;
            default:
                break;
            }
            stepsSinceLastUpgrade = 0;
            lastUpgrade = random;
            //lastUpgradePos = railOffset;
        }
    }
    
    private void addRandomAsteroid() {
    	GameObject o;
    	
        String ref = "asteroid";
    	
    	o = new GameObject(manager.get("models/asteroid/asteroid", GameModel.class), ref);
        o.setId("" + asteroidCount);

        Vector3 railOffset = addRailOffset();
        if(stepsSinceLastUpgrade <= 5 || stepsSinceLastGate <= 5) {
        	while(railOffset.equals(lastPointUpgradePos) ||railOffset.equals(lastUpgradePos) || railOffset.equals(lastGatePos) || railOffset.equals(lastAsteroidPos)) {
        		railOffset = addRailOffset();
        	}
        }
        
    	Vector3 asteroidPositon = centerRail.get(centerRail.size()-1).cpy().add(railOffset);
    	o.transform.setToTranslation(asteroidPositon);
    	
    	CollisionDetector collisionDetector = CollisionDetector.getInstance();
        btCollisionShape displayShape;
        displayShape = collisionDetector.getShapeManager().createConvexShape(o.getModelId(), o);
        o.createRigidBody(o.getModelId(), displayShape, 0.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
        collisionDetector.addRigidBody(o);
    	
    	level.addComponent(o);
    	objects.put(currentRailEndPoint, o);
    	
    	lastAsteroidPos = railOffset;
    	asteroidCount++;
    	stepsSinceLastAsteroid = 0;
    }
	
    /**
     * Adds an additional segment to the rails
     * @param newRailPosition
     */
	public void addRailPosition(Vector3 newRailPosition) {
		addRandomComponents();
		currentRailEndPoint++;
	}
	
	/**
	 * Setter for the rail
	 * @param rail
	 */
	public void setRail(List<Vector3> rail) {
		centerRail = rail;
		currentRailEndPoint = rail.size() - 1;
	}
	
	/**
	 * Setter of the railOffset
	 * @param railOffset
	 */
	public void setRailOffset(float railOffset) {
		this.railOffset = railOffset;
	}
	
	private Vector3 addRailOffset() {
		Vector3 offset = new Vector3();
		
		offset.z = MathUtils.random(-1, 1) * railOffset;
		offset.x = MathUtils.random(-1, 1) * railOffset;
		
		return offset;
	}
	
	private int upgradeLine = 3;
	
	private Vector3 addPointUpgradeRailOffset() {
		Vector3 offset = new Vector3();
		
		int randomZ = MathUtils.random(-1, 1);
		int randomX = MathUtils.random(-1, 1);
		
		if(upgradeLine <= 0) {
			offset.z = Math.signum(Math.signum(lastPointUpgradePos.z) + randomZ) * railOffset;
			offset.x = Math.signum(Math.signum(lastPointUpgradePos.x) + randomX) * railOffset;
			
			if((offset.z != lastPointUpgradePos.z) || (offset.x != lastPointUpgradePos.x)) {
				Vector3 tempOffset = offset.cpy();
				offset.z = (offset.z + lastPointUpgradePos.z)/2;
				offset.x = (offset.x + lastPointUpgradePos.x)/2;
				lastPointUpgradePos = tempOffset;
			} else {
				lastPointUpgradePos = offset;
			}
			
			upgradeLine = 3;
		} else {
			offset.z = Math.signum(lastPointUpgradePos.z) * railOffset;
			offset.x = Math.signum(lastPointUpgradePos.x) * railOffset;
		}
		
		upgradeLine--;
		
		return offset;
	}
	
	private Vector3 addGateRailOffset() {
		Vector3 offset = new Vector3();
		
		offset.z = Math.signum(lastPointUpgradePos.z) * railOffset;
		offset.x = Math.signum(lastPointUpgradePos.x) * railOffset;
		
		return offset;
	}
	
	/**
	 * 
	 * @param railPosition
	 * @param railX
	 * @param railY
	 * @return false if no asteroid in front of the position on rail (railX,railY)
	 */
	public boolean checkAsteroidPosition(Vector3 railPosition, float railX, float railY) {
		
		for(int i = 0; i < 3; i++) {
			GameObject o = objects.get(currentRailStartPoint + i);
			if(o !=  null) {
				Vector3 asteroidPosition = o.getPosition();
				if((asteroidPosition.x == railY) && (asteroidPosition.z == railX)) {
					return true;
				}
			}
		}
		return false;
	}

}
