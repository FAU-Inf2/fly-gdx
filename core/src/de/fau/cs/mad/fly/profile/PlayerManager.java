package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.DatabaseCursor;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.settings.AppSettingsManager;

/*
 * Manage players.
 * 
 * @author Qufang Fan
 */
public class PlayerManager {
	private Player currentPlayer;

	

	public Player getCurrentPlayer() {
		Gdx.app.log("getCurrentPlayer", "" + currentPlayer.getName());
		return currentPlayer;
	}

	public int getMaxPlayerID() {
		final String selectSQL = "select max(" + FlyDBManager.PLAYER_COLUMN_ID 
				+ ") from " + FlyDBManager.TABLE_PLAYER;
		
		FlyDBManager.getInstance().openDatabase();
		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);		

		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			FlyDBManager.getInstance().closeDatabase();
			int ret = cursor.getInt(0);
			return ret;
		}
		
		FlyDBManager.getInstance().closeDatabase();
		return 0;
	}

	public void setCurrentPlayer(Player currentPlayer) {

		this.currentPlayer = currentPlayer;
	}

	/*
	 * init the default player, create the default player when there is no
	 * player exist.
	 */
	private PlayerManager() {

		int userID = AppSettingsManager.Instance.getIntegerSetting(
				AppSettingsManager.CHOSEN_USER, 0);
		Player player = getPlayerfromDB(userID);
		if (player == null) {
			player = new Player();
			player.setName(I18n.t("default.username"));

			player.setId(getMaxPlayerID() + 1);

			savePlayer(player);

		}
		setCurrentPlayer(player);
		AppSettingsManager.Instance.setIntegerSetting(
				AppSettingsManager.CHOSEN_USER, player.getId());
	}

	private static PlayerManager Instance = new PlayerManager();

	public static PlayerManager getInstance() {
		return Instance;
	}

	public Player getPlayerfromDB(int userID) {
		final String selectSQL = "select * from " + FlyDBManager.TABLE_PLAYER + " where "
				+ FlyDBManager.PLAYER_COLUMN_ID + "=" + userID;
		FlyDBManager.getInstance().openDatabase();
		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
		

		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			Player player = new Player();
			player.setId(cursor.getInt(0));
			player.setName(cursor.getString(1));
			FlyDBManager.getInstance().closeDatabase();
			return player;
		}

		FlyDBManager.getInstance().closeDatabase();
		return null;

	}

	public List<Player> getAllPlayer() {
		List<Player> players = new ArrayList<Player>();
		final String selectSQL = "select * from " + FlyDBManager.TABLE_PLAYER;

		FlyDBManager.getInstance().openDatabase();
		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);

		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.next()) {
				Player player = new Player();
				player.setId(cursor.getInt(0));
				player.setName(cursor.getString(1));
				players.add(player);
			}
		}
		FlyDBManager.getInstance().closeDatabase();

		return players;
	}

	public void savePlayer(Player player) {
		
		final String deleteSQL = "delete from " + FlyDBManager.TABLE_PLAYER + " where "
				+ FlyDBManager.PLAYER_COLUMN_ID + "=" + player.getId();

		final String insertSQL = "insert into " + FlyDBManager.TABLE_PLAYER + " ( "
				+ FlyDBManager.PLAYER_COLUMN_ID + " , " + FlyDBManager.PLAYER_COLUMN_NAME + " ) values ("
				+ player.getId() + " , '" + player.getName() + "')";

		FlyDBManager.getInstance().openDatabase();
		FlyDBManager.getInstance().execSQL(deleteSQL);
		FlyDBManager.getInstance().execSQL(insertSQL);
		FlyDBManager.getInstance().closeDatabase();
	}

}
