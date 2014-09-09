package de.fau.cs.mad.fly.profile;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.fau.cs.mad.fly.res.PlaneUpgrade;

/**
 * Manages the Upgrades for the Planes
 * @author Sebastian
 *
 */
public class PlaneUpgradeManager {
	
	private JsonReader reader = new JsonReader();
	
	private Map<String, PlaneUpgrade> upgrades;
	
	private static PlaneUpgradeManager Instance = new PlaneUpgradeManager();
	
	public static PlaneUpgradeManager getInstance() {
		return Instance;
	}
	
	/**
	 * Getter for the UpgradeList
	 * @return A Map containing all Upgrades for the Planes
	 */
	public Map<String, PlaneUpgrade> getUpgradeList() {
		if (upgrades == null) {
			
			upgrades = new HashMap<String, PlaneUpgrade>();
			FileHandle dirHandle = Gdx.files.internal("spaceships/upgrades/");
			for (FileHandle file : dirHandle.list()) {
				JsonValue json = reader.parse(file);
				
				JsonValue upgradeJson;
				int size = json.size;
				for(int i = 0; i < size; i++) {
					upgradeJson = json.get(i);
					PlaneUpgrade upgrade = new PlaneUpgrade();
					
					upgrade.name = upgradeJson.getString("name");
					upgrade.timesAvailable = upgradeJson.getInt("times");
					upgrade.type = upgradeJson.getInt("type");
					upgrade.price = upgradeJson.getInt("price");
					upgrade.upgradeValues = upgradeJson.get("values").asIntArray();
					
					upgrades.put(upgrade.name, upgrade);
				}
			}
		}
		return upgrades;
	}
	
	/**
	 * Returns the Upgrade specified by the name
	 * @param name - The name of the Upgrade
	 * @return The Upgrade with the given name
	 */
	public PlaneUpgrade getUpgrade(String name) {
		return getUpgradeList().get(name);
	}
}
