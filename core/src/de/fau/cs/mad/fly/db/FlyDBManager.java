package de.fau.cs.mad.fly.db;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

/*
 * Manager the database of fly
 * todo all
 *
 * @ Qufang Fan
 */
public class FlyDBManager {

	private static final String DATABASE_NAME = "fau.mad.fly.db";
	private static final int DATABASE_VERSION = 1;

	Database dbHandler;
	
	public static final String TABLE_PLAYER = "player";
	public static final String PLAYER_COLUMN_ID = "player_id";
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
		final String DATABASE_CREATE = "create table if not exists "
				+ TABLE_PLAYER + "(" 
				+ PLAYER_COLUMN_ID + " integer primary key autoincrement, " 
				+ PLAYER_COLUMN_NAME + " text not null, " 
				+ PLAYER_COLUMN_SOCIAL_TYPE + " text, "
				+ PLAYER_COLUMN_SOCIAL_NAME + " text, " 
				+ PLAYER_COLUMN_SOCIAL_PASSWORD	+ " text);";
		return DATABASE_CREATE;
	}
	
	private String createTableScore() {
		final String DATABASE_CREATE = "create table if not exists "
				+ TABLE_SCORE + "(" 
				+ SCORE_COLUMN_PLAYERID + " integer not null, " 
				+ SCORE_COLUMN_LEVELID + " integer not null, " 
				+ SCORE_COLUMN_SCORE + " integer not null, "
				+ SCORE_COLUMN_COMPARESCORE + " text, "
				+ SCORE_COLUMN_REACHEDDATE + " date );";				
		return DATABASE_CREATE;
	}
	
	private String createTableScoreDetail() {
		final String DATABASE_CREATE = "create table if not exists "
				+ TABLE_SCORE_DETAIL + "(" 
				+ SCORE_DETAIL_COLUMN_ID + " integer primary key autoincrement, "
				+ SCORE_DETAIL_COLUMN_PLAYERID + " integer not null, " 
				+ SCORE_DETAIL_COLUMN_LEVELID + " integer not null, " 
				+ SCORE_DETAIL_COLUMN_DETAIL + " text not null, "
				+ SCORE_DETAIL_COLUMN_VALUE + " text);";
		return DATABASE_CREATE;
	}
	
	

	private FlyDBManager() {

		Gdx.app.log("FlyDBManager.()", "creation started");
		dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME,
				DATABASE_VERSION, null, null);
		dbHandler.setupDatabase();
		
		 try {
	            dbHandler.openOrCreateDatabase();
	            dbHandler.execSQL(createTablePlayer());
	            dbHandler.execSQL(createTableScore());
	            dbHandler.execSQL(createTableScoreDetail());
	            dbHandler.openOrCreateDatabase();
	        } catch (SQLiteGdxException e) {
	            e.printStackTrace();
	        }
	}

	private static FlyDBManager Instance = new FlyDBManager();
	
	public static FlyDBManager getInstance() {
		return Instance;
	}

	/* execute one SQL without return value.
	 * It is one atomic database operation. Don't need to open and close the database.
	 */
	public void execSQLAtomic(String SQL) {
		try {
			dbHandler.openOrCreateDatabase();
			Gdx.app.log("execSQL", SQL);
			dbHandler.execSQL(SQL);
			dbHandler.closeDatabase();
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
	}
	
	/* execute one SQL without return value.
	 * please don't forget to call the open and close database methods.
	 */
	public void execSQL(String SQL) {
		try {
			Gdx.app.log("execSQL", SQL);
			dbHandler.execSQL(SQL);
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
	}

	/* execute one insert SQL without return value.
	 * It is one atomic database operation. Don't need to open and close the database. 
	 */
	public void insertDataAtomic(String insertSQL) {
		execSQLAtomic(insertSQL);
	}
	
	/* execute one insert SQL without return value.
	 * please don't forget to call the open and close database methods.
	 *  
	 */
	public void insertData(String insertSQL) {
		execSQL(insertSQL);
	}

	public void openDatabase() {
		try {
			dbHandler.openOrCreateDatabase();
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
	}

	/* execute one select SQL. 
	 * please don't forget to call the open and close database methods.
	 */
	public DatabaseCursor selectData(String selectSQL) {
		DatabaseCursor cursor = null;
		try {

			Gdx.app.log("selectData", selectSQL);
			cursor = dbHandler.rawQuery(selectSQL);

		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
		return cursor;
	}

	public void closeDatabase() {
		try {
			dbHandler.closeDatabase();
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		// todo
		dbHandler = null;
		Gdx.app.log("FlyDBManager", "dispose");

	}

}
