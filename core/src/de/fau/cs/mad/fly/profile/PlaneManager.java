package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import de.fau.cs.mad.fly.player.IPlane;

/**
 * Manages the different Spaceships
 * @author Sebastian
 *
 */
public class PlaneManager {
	
	private JsonReader reader = new JsonReader();
	private List<IPlane.Head> planes;
	private IPlane.Head chosenPlane;
	
	private static PlaneManager Instance = new PlaneManager();
	
	public static PlaneManager getInstance() {
		return Instance;
	}

	public List<IPlane.Head> getSpaceshipList() {
		if (planes == null) {
			
			planes = new ArrayList<IPlane.Head>();
			FileHandle dirHandle = Gdx.files.internal("spaceships/");
			for (FileHandle file : dirHandle.list()) {
				JsonValue json = reader.parse(file);
				IPlane.Head spaceshipHead = new IPlane.Head();
				
				spaceshipHead.id = json.getInt("id");
				spaceshipHead.name = json.getString("name");
				spaceshipHead.modelRef = json.getString("modelRef");
				spaceshipHead.speed = json.getFloat("speed");
				spaceshipHead.rollingSpeed = json.getFloat("rollingSpeed");
				spaceshipHead.azimuthSpeed = json.getFloat("azimuthSpeed");
				spaceshipHead.lives = json.getInt("lives");
				spaceshipHead.file = file;
				
				planes.add(spaceshipHead);
			}
		}
		return planes;
	}

	public String getSpaceshipName(int levelID) {
		for (IPlane.Head spaceship : getSpaceshipList()) {
			if (spaceship.id == levelID) {
				return spaceship.name;
			}
		}
		return Integer.toString(levelID);
	}

	public IPlane.Head getChosenPlane() {
		if (chosenPlane == null) {
			chosenPlane = getSpaceshipList().get(0);
		}
		return chosenPlane;
	}
	
	public IPlane.Head getNextPlane(int left) {
		if(chosenPlane == null) {
			chosenPlane = getSpaceshipList().get(0);
		}
		
		int chosenPlaneId = chosenPlane.id - 1;
		
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
}