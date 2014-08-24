package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.sql.DatabaseCursor;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.settings.AppSettingsManager;

/**
 * Manages the player profiles.
 * 
 * @author Qufang Fan
 */
public class PlayerProfileManager {
	private PlayerProfile currentPlayerProfile;
	List<PlayerProfile> playerProfiles;

	public PlayerProfile getCurrentPlayerProfile() {
		return currentPlayerProfile;
	}

	public void setCurrentPlayer(PlayerProfile currentPlayerProfile) {
		this.currentPlayerProfile = currentPlayerProfile;
	}

	/**
	 * Initializes the default player profile, creates the default player profile when there is no
	 * player already existing.
	 */
	private PlayerProfileManager() {
		setPlayers();

		int userID = AppSettingsManager.Instance.getIntegerSetting(AppSettingsManager.CHOSEN_USER, 0);
		PlayerProfile player = getPlayerfromList(userID);
		if (player == null) {
			player = new PlayerProfile();
			player.setName(I18n.t("default.username"));
			savePlayer(player);
		}

		setCurrentPlayer(player);
		AppSettingsManager.Instance.setIntegerSetting(AppSettingsManager.CHOSEN_USER,
				player.getId());		
	}

	private static PlayerProfileManager Instance = new PlayerProfileManager();

	public static PlayerProfileManager getInstance() {
		return Instance;
	}
	
	private PlayerProfile getPlayerfromList(int userID){
		for(PlayerProfile player : playerProfiles){
			if(player.getId() == userID)
			{
				return player;
			}
		}
		return null;
	}

	public PlayerProfile getPlayerfromDB(int userID) {
		final String selectSQL = "select player_id, fly_id,name from player where player_id =" + userID;
		PlayerProfile playerProfile = null;

		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			playerProfile = new PlayerProfile();
			playerProfile.setId(cursor.getInt(0));
			playerProfile.setFlyID(cursor.getInt(1));
			playerProfile.setName(cursor.getString(2));
			cursor.close();
		}

		return playerProfile;
	}

	// private List<Player> players = null;

	public List<PlayerProfile> getAllPlayerProfiles()
	{
		return playerProfiles;
	}
	
	private void setPlayers() {
		playerProfiles = new ArrayList<PlayerProfile>();
		final String selectSQL = "select player_id, fly_id, name from player";

		// FlyDBManager.getInstance().openDatabase();
		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);

		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.next()) {
				PlayerProfile playerProfile = new PlayerProfile();
				playerProfile.setId(cursor.getInt(0));
				playerProfile.setFlyID(cursor.getInt(1));
				playerProfile.setName(cursor.getString(2));
				playerProfiles.add(playerProfile);
			}
			cursor.close();
		}
	}

	public void savePlayer(PlayerProfile playerProfile) {

		int newID = getMaxPlayerID() + 1;
		playerProfile.setId(newID);
		final String insertSQL = "insert into player (player_id, name) values (" + playerProfile.getId() + " , '" + playerProfile.getName() + "')";

		FlyDBManager.getInstance().execSQL(insertSQL);
		
		playerProfiles.add(playerProfile);

	}

	private int getMaxPlayerID() {
		final String selectSQL = "select max(player_id) from player";

		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			int ret = cursor.getInt(0);
			cursor.close();
			return ret;			
		}
		return 0;
	}

	public void saveFlyID(PlayerProfile playerProfile) {

		final String sql = "update player set fly_id=" + playerProfile.getFlyID() + " where player_id=" + playerProfile.getId();

		FlyDBManager.getInstance().execSQL(sql);
	}

}
