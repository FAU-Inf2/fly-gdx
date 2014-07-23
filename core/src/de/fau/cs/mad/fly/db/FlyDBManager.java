package de.fau.cs.mad.fly.db;

import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

import de.fau.cs.mad.fly.settings.AppSettingsManager;

/**
 * Manages the database of fly 
 *
 * @ Qufang Fan
 */
public class FlyDBManager {

	private static final String DATABASE_NAME = "fau.mad.fly.db";
	private static final int DATABASE_VERSION = 1;

	Database dbHandler;

	public static final String TABLE_PLAYER = "player";
	public static final String PLAYER_COLUMN_ID = "player_id";
	public static final String PLAYER_COLUMN_FLY_ID = "fly_id";
	public static final String PLAYER_COLUMN_NAME = "name";
	public static final String PLAYER_COLUMN_SOCIAL_TYPE = "social_type";
	public static final String PLAYER_COLUMN_SOCIAL_NAME = "social_name";
	public static final String PLAYER_COLUMN_SOCIAL_PASSWORD = "social_password";

	public static final String TABLE_SCORE = "score";
	public static final String SCORE_COLUMN_PLAYERID = "player_id";
	public static final String SCORE_COLUMN_LEVELID = "level_id";
	public static final String SCORE_COLUMN_SCORE = "score";
	public static final String SCORE_COLUMN_COMPARESCORE = "compare_score";
	public static final String SCORE_COLUMN_REACHEDDATE = "reached_date";

	public static final String TABLE_SCORE_DETAIL = "score_detail";
	public static final String SCORE_DETAIL_COLUMN_ID = "scoredetail_id";
	public static final String SCORE_DETAIL_COLUMN_PLAYERID = "player_id";
	public static final String SCORE_DETAIL_COLUMN_LEVELID = "level_id";
	public static final String SCORE_DETAIL_COLUMN_DETAIL = "score_detail";
	public static final String SCORE_DETAIL_COLUMN_VALUE = "_value";

	private String createTablePlayer() {
		final String creatSQL = "create table if not exists " + TABLE_PLAYER
				+ "(" + PLAYER_COLUMN_ID
				+ " integer primary key autoincrement, " + PLAYER_COLUMN_FLY_ID
				+ " text, " + PLAYER_COLUMN_NAME + " text not null, "
				+ PLAYER_COLUMN_SOCIAL_TYPE + " text, "
				+ PLAYER_COLUMN_SOCIAL_NAME + " text, "
				+ PLAYER_COLUMN_SOCIAL_PASSWORD + " text);";
		return creatSQL;
	}

	private String createTableScore() {
		final String creatSQL = "create table if not exists " + TABLE_SCORE
				+ "(" + SCORE_COLUMN_PLAYERID + " integer not null, "
				+ SCORE_COLUMN_LEVELID + " integer not null, "
				+ SCORE_COLUMN_SCORE + " integer not null, "
				+ SCORE_COLUMN_COMPARESCORE + " text, "
				+ SCORE_COLUMN_REACHEDDATE + " date );";
		return creatSQL;
	}

	private String createTableScoreDetail() {
		final String creatSQL = "create table if not exists "
				+ TABLE_SCORE_DETAIL + "(" + SCORE_DETAIL_COLUMN_ID
				+ " integer primary key autoincrement, "
				+ SCORE_DETAIL_COLUMN_PLAYERID + " integer not null, "
				+ SCORE_DETAIL_COLUMN_LEVELID + " integer not null, "
				+ SCORE_DETAIL_COLUMN_DETAIL + " text not null, "
				+ SCORE_DETAIL_COLUMN_VALUE + " text);";
		return creatSQL;
	}

	private FlyDBManager() {
		Gdx.app.log("FlyDBManager", "setupDatabase begin" + new Date().toString());
		dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME,
				DATABASE_VERSION, null, null);
		dbHandler.setupDatabase();
		Gdx.app.log("FlyDBManager", "setupDatabase end" + new Date().toString());
		int dbVersion = AppSettingsManager.Instance.getIntegerSetting(
				AppSettingsManager.DATABASE_VERSION, 0);
		if (dbVersion != DATABASE_VERSION) {
			try {
				Gdx.app.log("FlyDBManager", "openOrCreateDatabase begin" + new Date().toString());
				dbHandler.openOrCreateDatabase();
				Gdx.app.log("FlyDBManager", "openOrCreateDatabase end" + new Date().toString());
				dbHandler.execSQL(createTablePlayer());
				dbHandler.execSQL(createTableScore());
				dbHandler.execSQL(createTableScoreDetail());
				dbHandler.closeDatabase();
				Gdx.app.log("FlyDBManager", "Createtable end" + new Date().toString());
				AppSettingsManager.Instance.setIntegerSetting(
						AppSettingsManager.DATABASE_VERSION, DATABASE_VERSION);
			} catch (SQLiteGdxException e) {
				Gdx.app.error("FlyDBManager", e.toString());
			}
		}
	}

	private static FlyDBManager Instance = new FlyDBManager();

	public static FlyDBManager getInstance() {
		return Instance;
	}

	/*
	 * execute one SQL without return value. It is one atomic database
	 * operation. Don't need to open and close the database.
	 */
	public void execSQLAtomic(String sql) {
		try {
			dbHandler.openOrCreateDatabase();
			Gdx.app.log("execSQL", sql);
			dbHandler.execSQL(sql);
			dbHandler.closeDatabase();
		} catch (SQLiteGdxException e) {
			Gdx.app.error("FlyDBManager", e.toString());
		}
	}

	/*
	 * execute one SQL without return value. please don't forget to call the
	 * open and close database methods.
	 */
	public void execSQL(String sql) throws SQLiteGdxException {
		Gdx.app.log("execSQL", sql);
		dbHandler.execSQL(sql);
	}

	/*
	 * execute one insert SQL without return value. It is one atomic database
	 * operation. Don't need to open and close the database.
	 */
	public void insertDataAtomic(String insertSQL) {
		execSQLAtomic(insertSQL);
	}

	/*
	 * execute one insert SQL without return value. please don't forget to call
	 * the open and close database methods.
	 */
	public void insertData(String insertSQL) throws SQLiteGdxException {
		execSQL(insertSQL);
	}

	public void openDatabase() throws SQLiteGdxException {
		dbHandler.openOrCreateDatabase();
	}

	/*
	 * execute one select SQL. please don't forget to call the open and close
	 * database methods.
	 */
	public DatabaseCursor selectData(String selectSQL)
			throws SQLiteGdxException {
		DatabaseCursor cursor = null;
		Gdx.app.log("selectData", selectSQL);
		cursor = dbHandler.rawQuery(selectSQL);
		return cursor;
	}

	public void closeDatabase() {
		try {
			dbHandler.closeDatabase();
		} catch (SQLiteGdxException e) {
			Gdx.app.error("FlyDBManager.closeDatabase", e.toString());
		}
	}

	public void dispose() {

		dbHandler = null;
		Gdx.app.log("FlyDBManager", "dispose");

	}

}
