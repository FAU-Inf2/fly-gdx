package de.fau.cs.mad.fly.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.game.EndlessLevelGenerator;
import de.fau.cs.mad.fly.features.game.EndlessRailLevelGenerator;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.res.GateDisplay;
import de.fau.cs.mad.fly.res.GateGoal;
import de.fau.cs.mad.fly.res.Perspective;

/**
 * 
 * @author Sebastian
 *
 */
public class RailFlightController extends FlightController implements ICollisionListener{
	
	private EndlessRailLevelGenerator generator;
	
	private Vector3 direction;
	private Vector3 endPosition; 
	private Vector3 currentPosition; 
	
	private int listSize = 20;
	
	private List<Vector3> centerRail;
	private float railOffset = 3.f;
	
	private int railX, railY;
	
	private boolean changeRailX, changeRailY = false;
	private float changeTimeX, changeTimeY = 0;
	private float changeX, changeY;

	public RailFlightController(Player player, PlayerProfile playerProfile, EndlessLevelGenerator generator, Perspective perspective) {
		super(player, playerProfile);
		this.generator = (EndlessRailLevelGenerator) generator;
		
		this.direction = perspective.viewDirection;
		this.endPosition = perspective.position;
		this.currentPosition = perspective.position;
		
		centerRail = new ArrayList<Vector3>();
		centerRail.add(currentPosition);
		//this.generator.addRailPosition(currentPosition);
		//this.generator.initRail(currentPosition);
		
		initRail();
		
		this.railX = this.railY = 0;
	}
	
	@Override
	public void update(float delta) {
		if (useSensorData) {
            super.interpretSensorInput();
        }
		
		changeRail(delta);
		
		if(checkRailPointPassed()) {
			Vector3 nextStep = nextStep();
			centerRail.add(nextStep);
			generator.addRailPosition(nextStep);

			// remove objects behind the passed point
			generator.removeComponents(centerRail.get(0));
			
			centerRail.remove(0);
			currentPosition = centerRail.get(0);
			
		}
		float speed = player.getPlane().getSpeed() + 0.0001f;
		player.getPlane().setSpeed(speed);
    }
	
	private void initRail() {
		for(int i = 1; i < listSize; i++) {
			Vector3 nextStep = nextStep();
			centerRail.add(nextStep);
			//generator.addRailPosition(nextStep);
		}
		generator.setRail(centerRail);
		generator.setRailOffset(railOffset);
	}
	
	private Vector3 nextStep() {
		Vector3 nextPos = new Vector3();
		
		nextPos = endPosition.cpy().add(direction);
		
		//change direction?
		
		endPosition = nextPos;
		return nextPos;
	}
	
	private void changeRail(float delta) {
		Vector3 shiftVector = new Vector3();
		if(Math.abs(rollFactor) > 0.5) {
			if(Math.abs(railX + Math.signum(rollFactor)) <= 1.f && !changeRailX) {
				//shiftVector.z += Math.signum(rollFactor);
				railX += Math.signum(rollFactor);
				changeX = Math.signum(rollFactor);
				changeTimeX = 1f;
			}
		} 
		if(Math.abs(azimuthFactor) > 0.5) {
			if(Math.abs(railY - Math.signum(azimuthFactor)) <= 1.f && !changeRailY) {
				//shiftVector.x -= Math.signum(azimuthFactor);
				railY -= Math.signum(azimuthFactor);
				changeY = -Math.signum(azimuthFactor);
				changeTimeY = 1f;
			}
		}
		
		if(changeTimeX == 1f) {
			changeRailX = true;
		}
		
		if(changeTimeY == 1f) {
			changeRailY = true;
		}
		
		if(changeRailX) {
			if(changeTimeX - delta <= 0) {
				shiftVector.z = changeX * changeTimeX * railOffset;
				changeRailX = false;
				changeX = 0;
			} else {
				changeTimeX -= delta;
				shiftVector.z = changeX * delta * railOffset;
			}
			
		}
		
		if(changeRailY) {
			if(changeTimeY - delta <= 0) {
				shiftVector.x = changeY * changeTimeY * railOffset;
				changeRailY = false;
				changeY = 0;
			} else {
				changeTimeY -= delta;
				shiftVector.x = changeY * delta * railOffset;
			}
			
		}
		
		player.getPlane().shift(shiftVector);
	}
	
	private boolean checkRailPointPassed() {		
		if(player.getPlane().getPosition().y > centerRail.get(0).y + 3) {
			return true;
		}
		
		return false;
	}

	@Override
	public void onCollision(GameObject g1, GameObject g2) {
		
		if(g2 instanceof GateDisplay) {
			if(generator.checkAsteroidPosition(currentPosition, railX * railOffset, railY * railOffset)) {
				railX -= changeX;
				railY -= changeY;
			} 
			
			player.getPlane().resetOnRail(railX * railOffset, railY * railOffset, currentPosition.y);
			
			changeX = changeY = 0;
			changeRailX = changeRailY = false;
			
		} else if(!(g2 instanceof GateGoal) && !(g2 instanceof Collectible)) {
			//asteroid
			while(generator.checkAsteroidPosition(currentPosition, railX * railOffset, railY * railOffset)) {
				Vector3 newPos = generateRandomAdjacentPosition();
			}
			
			player.getPlane().resetOnRail(railX * railOffset, railY * railOffset, currentPosition.y);
			
			changeX = changeY = 0;
			changeRailX = changeRailY = false;
		}
	}
	
	private Vector3 generateRandomAdjacentPosition() {
		int newRailX;
		int newRailY;
		
		if(Math.abs(railX) > 0) {
			newRailX = 0;
		} else {
			newRailX = (int) Math.signum(MathUtils.random(-1, 1));
		}
		
		if(Math.abs(railY) > 0) {
			newRailY = 0;
		} else {
			newRailY = (int) Math.signum(MathUtils.random(-1, 1));
		}
		
		railX = newRailX;
		railY = newRailY;
		
		return new Vector3(newRailX, newRailY, 0);
	}

}