package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.res.PlaneUpgrade;

/**
 * Manages the different Spaceships
 * @author Sebastian
 *
 */
public class PlaneManager {
	
	private JsonReader reader = new JsonReader();
	//private List<IPlane.Head> planes;
	private Map<Integer, IPlane.Head> planes;
	private IPlane.Head chosenPlane;
	
	private static PlaneManager Instance = new PlaneManager();
	
	public static PlaneManager getInstance() {
		return Instance;
	}

	public Map<Integer, IPlane.Head> getSpaceshipList() {
		if (planes == null) {
			
			//planes = new ArrayList<IPlane.Head>();
			planes = new HashMap<Integer, IPlane.Head>();
			FileHandle dirHandle = Gdx.files.internal("spaceships/json/");
			for (FileHandle file : dirHandle.list()) {
				JsonValue json = reader.parse(file);
				IPlane.Head planeHead = new IPlane.Head();
				
				int id = json.getInt("id");
				planeHead.id = id;
				planeHead.name = json.getString("name");
				planeHead.modelRef = json.getString("modelRef");
				planeHead.speed = json.getFloat("speed");
				planeHead.rollingSpeed = json.getFloat("rollingSpeed");
				planeHead.azimuthSpeed = json.getFloat("azimuthSpeed");
				planeHead.lives = json.getInt("lives");
				JsonValue rotation = json.get("rotation");
				if (rotation != null) {
					Vector3 rotationVector = new Vector3(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2));
					planeHead.rotationSpeed = rotationVector.len();
					planeHead.rotation = rotationVector.nor();
				}
				JsonValue particleOffset = json.get("particleOffset");
				if (particleOffset != null) {
					planeHead.particleOffset = new Vector3(particleOffset.getFloat(0), particleOffset.getFloat(1), particleOffset.getFloat(2));
				}
				
				planeHead.file = file;
				
				int[] upgradeTypes = json.get("upgrades").asIntArray();
				planeHead.upgradeTypes = upgradeTypes;
				
				Collection<PlaneUpgrade> upgrades = PlaneUpgradeManager.getInstance().getUpgradeList().values();
				Map<String, Integer> upgradeMap = new HashMap<String, Integer>();
				
				int size = upgradeTypes.length;
				for(PlaneUpgrade upgrade : upgrades) {
					for(int i = 0; i < size; i++) {
						if(upgrade.type == upgradeTypes[i]) {
							upgradeMap.put(upgrade.name, 0);
						}
					}
				}
				
				planeHead.upgradesBought = upgradeMap;
				
				//planes.add(spaceshipHead);
				planes.put(id, planeHead);
			}
		}
		return planes;
	}

	public IPlane.Head getChosenPlane() {
		if (chosenPlane == null) {
			chosenPlane = getSpaceshipList().get(1);
		}
		return chosenPlane;
	}
	
	public IPlane.Head getNextPlane(int left) {
		if(chosenPlane == null) {
			chosenPlane = getSpaceshipList().get(1);
		}
		
		int chosenPlaneId = chosenPlane.id;
		
		chosenPlaneId -= left;
		if(chosenPlaneId < 0) {
			chosenPlaneId += planes.size();
		} else if(chosenPlaneId >= planes.size()) {
			chosenPlaneId -= planes.size();
		}
		
		chosenPlane = getSpaceshipList().get(chosenPlaneId);
		
		//((Fly) Gdx.app.getApplicationListener()).getGameController().getPlayer().setPlane(new Spaceship(chosenPlane));
		PlayerProfileManager.getInstance().getCurrentPlayerProfile().setPlane(chosenPlane);
		
		return chosenPlane;
	}

	public void setChosenPlane(IPlane.Head plane) {
		chosenPlane = plane;
	}
	
	public IPlane.Head upgradePlane(String upgradeName, int signum) {
		PlaneUpgrade upgrade = PlaneUpgradeManager.getInstance().getUpgrade(upgradeName);
		
		/*int size = upgrade.upgradeValues.length;
		for(int i = 0; i < size; i++) {
			plane.
		}*/
		int[] values = upgrade.upgradeValues;
		
		chosenPlane.speed += values[0] * signum;
		chosenPlane.rollingSpeed += values[1] * signum;
		chosenPlane.azimuthSpeed += values[2] * signum;
		chosenPlane.lives += values[3] * signum;
		
		planes.put(chosenPlane.id, chosenPlane);
		
		return chosenPlane;
	}
	
	public void buyUpgradeForPlane(String upgradeName) {
		int currentUpgradeBought = chosenPlane.upgradesBought.get(upgradeName);
		
		PlaneUpgrade upgrade = PlaneUpgradeManager.getInstance().getUpgrade(upgradeName);
		int maxUpgrade = upgrade.timesAvailable;
		
		if(currentUpgradeBought < maxUpgrade) {
			if(PlayerProfileManager.getInstance().getCurrentPlayerProfile().addMoney(-upgrade.price)) {
				chosenPlane.upgradesBought.put(upgradeName, currentUpgradeBought + 1);
			}
		}
	}
}