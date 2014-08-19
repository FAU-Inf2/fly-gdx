package de.fau.cs.mad.fly.profile;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.fau.cs.mad.fly.res.PlaneUpgrade;

/**
 * 
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
	
	public PlaneUpgrade getUpgrade(String name) {
		return getUpgradeList().get(name);
	}
}
