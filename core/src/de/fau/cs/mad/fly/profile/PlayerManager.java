package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.sql.DatabaseCursor;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.settings.AppSettingsManager;

/**
 * Manage players.
 * 
 * @author Qufang Fan
 */
public class PlayerManager {
	private Player currentPlayer;
	List<Player> players;

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/**
	 * init the default player, create the default player when there is no
	 * player exist.
	 */
	private PlayerManager() {
		setPlayers();

		int userID = AppSettingsManager.Instance.getIntegerSetting(AppSettingsManager.CHOSEN_USER,
				0);
		Player player = getPlayerfromList(userID);
		if (player == null) {
			player = new Player();
			player.setName(I18n.t("default.username"));
			savePlayer(player);
		}

		setCurrentPlayer(player);
		AppSettingsManager.Instance.setIntegerSetting(AppSettingsManager.CHOSEN_USER,
				player.getId());		
	}

	private static PlayerManager Instance = new PlayerManager();

	public static PlayerManager getInstance() {
		return Instance;
	}
	
	private Player getPlayerfromList(int userID){
		for(Player player : players){
			if(player.getId() == userID)
			{
				return player;
			}
		}
		return null;
	}

	public Player getPlayerfromDB(int userID) {
		final String selectSQL = "select " + FlyDBManager.PLAYER_COLUMN_ID + ", "
				+ FlyDBManager.PLAYER_COLUMN_FLY_ID + ", " + FlyDBManager.PLAYER_COLUMN_NAME
				+ " from " + FlyDBManager.TABLE_PLAYER + " where " + FlyDBManager.PLAYER_COLUMN_ID
				+ "=" + userID;
		Player player = null;

		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			player = new Player();
			player.setId(cursor.getInt(0));
			player.setFlyID(cursor.getInt(1));
			player.setName(cursor.getString(2));
			cursor.close();
		}

		return player;
	}

	// private List<Player> players = null;

	public List<Player> getAllPlayer()
	{
		return players;
	}
	
	private void setPlayers() {
		players = new ArrayList<Player>();
		final String selectSQL = "select " + FlyDBManager.PLAYER_COLUMN_ID + ", "
				+ FlyDBManager.PLAYER_COLUMN_FLY_ID + ", " + FlyDBManager.PLAYER_COLUMN_NAME
				+ " from " + FlyDBManager.TABLE_PLAYER;

		// FlyDBManager.getInstance().openDatabase();
		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);

		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.next()) {
				Player player = new Player();
				player.setId(cursor.getInt(0));
				player.setFlyID(cursor.getInt(1));
				player.setName(cursor.getString(2));
				players.add(player);
			}
			cursor.close();
		}
	}

	public void savePlayer(Player player) {

		int newID = getMaxPlayerID() + 1;
		player.setId(newID);
		final String insertSQL = "insert into " + FlyDBManager.TABLE_PLAYER + " ( "
				+ FlyDBManager.PLAYER_COLUMN_ID + " , " + FlyDBManager.PLAYER_COLUMN_NAME
				+ " ) values (" + player.getId() + " , '" + player.getName() + "')";

		FlyDBManager.getInstance().execSQL(insertSQL);
		
		players.add(player);

	}

	private int getMaxPlayerID() {
		final String selectSQL = "select max(" + FlyDBManager.PLAYER_COLUMN_ID + ") from "
				+ FlyDBManager.TABLE_PLAYER;

		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			int ret = cursor.getInt(0);
			cursor.close();
			return ret;			
		}
		return 0;
	}

	public void saveFlyID(Player player) {

		final String sql = "update " + FlyDBManager.TABLE_PLAYER + " set "
				+ FlyDBManager.PLAYER_COLUMN_FLY_ID + "=" + player.getFlyID() + " where "
				+ FlyDBManager.PLAYER_COLUMN_ID + "=" + player.getId();

		FlyDBManager.getInstance().execSQL(sql);
	}

}
