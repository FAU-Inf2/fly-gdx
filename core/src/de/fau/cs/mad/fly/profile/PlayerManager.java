package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.SQLiteGdxException;

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
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/*
	 * init the default player, create the default player when there is no
	 * player exist.
	 */
	private PlayerManager() {

		int userID = AppSettingsManager.Instance.getIntegerSetting(AppSettingsManager.CHOSEN_USER,
				0);
		Player player = getPlayerfromDB(userID);
		if (player == null) {
			player = new Player();
			player.setName(I18n.t("default.username"));
			savePlayer(player);
		}

		player.createSettings();
		setCurrentPlayer(player);
		AppSettingsManager.Instance.setIntegerSetting(AppSettingsManager.CHOSEN_USER,
				player.getId());
	}

	private static PlayerManager Instance = new PlayerManager();

	public static PlayerManager getInstance() {
		return Instance;
	}

	public Player getPlayerfromDB(int userID) {
		final String selectSQL = "select " + FlyDBManager.PLAYER_COLUMN_ID + ", "
				+ FlyDBManager.PLAYER_COLUMN_FLY_ID + ", " + FlyDBManager.PLAYER_COLUMN_NAME
				+ " from " + FlyDBManager.TABLE_PLAYER + " where " + FlyDBManager.PLAYER_COLUMN_ID
				+ "=" + userID;
		Player player = null;
		try {

			FlyDBManager.getInstance().openDatabase();

			DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.next();
				player = new Player();
				player.setId(cursor.getInt(0));
				player.setFlyID(cursor.getInt(1));
				player.setName(cursor.getString(2));
			}

		} catch (SQLiteGdxException e) {
			Gdx.app.error("PlayerManager.getPlayerfromDB", e.toString());
		} finally {
			FlyDBManager.getInstance().closeDatabase();
		}

		return player;
	}

	//private List<Player> players = null;

	public List<Player> getAllPlayer() {
		List<Player> players = new ArrayList<Player>();
		final String selectSQL = "select " + FlyDBManager.PLAYER_COLUMN_ID + ", "
				+ FlyDBManager.PLAYER_COLUMN_FLY_ID + ", " + FlyDBManager.PLAYER_COLUMN_NAME
				+ " from " + FlyDBManager.TABLE_PLAYER;

		try {
			FlyDBManager.getInstance().openDatabase();
			DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);

			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.next()) {
					Player player = new Player();
					player.setId(cursor.getInt(0));
					player.setFlyID(cursor.getInt(1));
					player.setName(cursor.getString(2));
					players.add(player);
				}
			}
		} catch (SQLiteGdxException e) {
			Gdx.app.error("PlayerManager.getAllPlayer", e.toString());
		} finally {
			FlyDBManager.getInstance().closeDatabase();
		}

		return players;
	}

	public void savePlayer(Player player) {

		try {
			FlyDBManager.getInstance().openDatabase();
			int newID = getMaxPlayerID()+1;
			player.setId(newID);
			final String insertSQL = "insert into " + FlyDBManager.TABLE_PLAYER + " ( "
					+ FlyDBManager.PLAYER_COLUMN_ID + " , " + FlyDBManager.PLAYER_COLUMN_NAME
					+ " ) values (" + player.getId() + " , '" + player.getName() + "')";

			FlyDBManager.getInstance().execSQL(insertSQL);

		} catch (SQLiteGdxException e) {
			Gdx.app.error("PlayerManager.savePlayer", e.toString());
		} finally {
			FlyDBManager.getInstance().closeDatabase();
		}
	}

	private int getMaxPlayerID() throws SQLiteGdxException {
		final String selectSQL = "select max(" + FlyDBManager.PLAYER_COLUMN_ID + ") from "
				+ FlyDBManager.TABLE_PLAYER;

		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			int ret = cursor.getInt(0);
			return ret;
		}
		return 0;
	}

	public void saveFlyID(Player player) {

		final String sql = "update " + FlyDBManager.TABLE_PLAYER + " set "
				+ FlyDBManager.PLAYER_COLUMN_FLY_ID + "=" + player.getFlyID() + " where "
				+ FlyDBManager.PLAYER_COLUMN_ID + "=" + player.getId();

		FlyDBManager.getInstance().execSQLAtomic(sql);
	}

}
