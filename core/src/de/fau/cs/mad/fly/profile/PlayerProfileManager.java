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
        AppSettingsManager.Instance.setIntegerSetting(AppSettingsManager.CHOSEN_USER, currentPlayerProfile.getId());
    }
    
    /**
     * Initializes the default player profile, creates the default player
     * profile when there is no player already existing.
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
    }
    
    private static PlayerProfileManager Instance = new PlayerProfileManager();
    
    public static PlayerProfileManager getInstance() {
        return Instance;
    }
    
    private PlayerProfile getPlayerfromList(int userID) {
        for (PlayerProfile player : playerProfiles) {
            if (player.getId() == userID) {
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
    
    public List<PlayerProfile> getAllPlayerProfiles() {
        return playerProfiles;
    }
    
    private void setPlayers() {
        playerProfiles = new ArrayList<PlayerProfile>();
        final String selectSQL = "select player_id,fly_id,name,total_score,total_geld,current_levelgroup_id,current_level_id,passed_levelgroup_id,passed_level_id from player";
        
        // FlyDBManager.getInstance().openDatabase();
        DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
        
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.next()) {
                PlayerProfile playerProfile = new PlayerProfile();
                playerProfile.setId(cursor.getInt(0));
                playerProfile.setFlyID(cursor.getInt(1));
                playerProfile.setName(cursor.getString(2));
                playerProfile.setMoney(cursor.getInt(3));
                playerProfile.setCurrentLevelGroup(LevelGroupManager.getInstance().getLevelGroup(cursor.getInt(5)));
                playerProfile.setCurrentLevelProfile(cursor.getInt(6));
                playerProfile.setPassedLevelgroupID(cursor.getInt(7));
                playerProfile.setPassedLevelID(cursor.getInt(8));
                
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
    
    public void updateIntColumn(PlayerProfile playerProfile, String colname, int newValue) {
        final String sql = "update player set " + colname + "=" + newValue + " where player_id=" + playerProfile.getId();
        FlyDBManager.getInstance().execSQL(sql);
    }
    
	public void deletePlayerProfile() {
		this.deletePlayerProfile(this.getCurrentPlayerProfile());
	}

	public void deletePlayerProfile(PlayerProfile playerProfile) {
		final String sql = "delete from player where player_id=" + playerProfile.getId();
		final String sql1 = "delete from score where player_id=" + playerProfile.getId();
		final String sql2 = "delete from fly_plane_Equiped where player_id=" + playerProfile.getId();
		final String sql3 = "delete from fly_plane_upgrade where player_id=" + playerProfile.getId();
		FlyDBManager.getInstance().execSQL(sql);
		FlyDBManager.getInstance().execSQL(sql1);
		FlyDBManager.getInstance().execSQL(sql2);
		FlyDBManager.getInstance().execSQL(sql3);

		if (playerProfile == this.getCurrentPlayerProfile()) {
			this.getAllPlayerProfiles().remove(playerProfile);
			playerProfile.clearSettingManager();
			this.setCurrentPlayer(this.getAllPlayerProfiles().get(0));
		} else {
			this.getAllPlayerProfiles().remove(playerProfile);
			playerProfile.clearSettingManager();
		}
	}
    
}
