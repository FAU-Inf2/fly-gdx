package de.fau.cs.mad.fly.db;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;

/**
 * Manages the database of fly 
 *
 * @ Qufang Fan
 */
public class FlyDBManager {

	private static final String DATABASE_NAME = "faumadfly01.db";
	private static final int DATABASE_VERSION = 1;
	private static final int LASTEST_DATABASE_VERSION = 1;

	Database dbHandler;

	

	
	private FlyDBManager() {
		Gdx.app.log("FlyDBManager", "setupDatabase begin " + System.currentTimeMillis());
		String createTablePlayer = "create table if not exists player(player_id integer primary key autoincrement, fly_id text, name text not null, total_score integer not null default 0, total_geld integer not null default 0, current_levelgroup_id integer not null default 1, current_level_id integer not null default 1,"
				+ "passed_levelgroup_id integer not null default 1, passed_level_id integer not null default 1)";
		String createTableScore = "create table if not exists score(player_id integer not null, level_group_id integer not null, level_id integer not null, score integer not null, compare_score text, reached_date date, is_uploaded integer not null default 0, server_score_id integer)";
		//String createTableScoreDetail = "create table if not exists score_detail(scoredetail_id integer primary key autoincrement, level_group_id integer not null, player_id integer not null, level_id integer not null,score_detail text not null, _value text)";
		String createTavlePlaneEquiped = "create table if not exists fly_plane_Equiped(player_id integer not null, plane_id integer, equiped_name text not null, _count integer not null default 0)";
		String createTavlePlaneUpdates = "create table if not exists fly_plane_upgrade(player_id integer not null, plane_id integer, update_name text not null, _count integer not null default 0)";
		String createTavleVersion = "create table if not exists fly_db_version(_version interger not null)";
		String insertDBVersion = "insert into fly_db_version values(1)";

		List<String> createSQLs = new ArrayList<String>();
		createSQLs.add(createTablePlayer);
		createSQLs.add(createTableScore);
		//createSQLs.add(createTableScoreDetail);
		createSQLs.add(createTavlePlaneEquiped);
		createSQLs.add(createTavlePlaneUpdates);
		createSQLs.add(createTavleVersion);
		createSQLs.add(insertDBVersion);
		
		
		List<String> upgradeSQLs = null;		
		dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME, DATABASE_VERSION, createSQLs,
				upgradeSQLs);
		
		synchronized (dbHandler) {
			dbHandler.setupDatabase();
			Gdx.app.log("FlyDBManager", "setupDatabase end   " + System.currentTimeMillis());
		
			dbHandler.openOrCreateDatabase();
			Gdx.app.log("FlyDBManager", "database opened " + System.currentTimeMillis());
			
			int dbVersion = getDBVersion();
			if(dbVersion < LASTEST_DATABASE_VERSION )
			{				
				//change LASTEST_DATABASE_VERSION amd add db update sql here when new version needs
				updateDBversion(LASTEST_DATABASE_VERSION);
			}
		}
		
	}

	private static FlyDBManager Instance = new FlyDBManager();
	
	private int getDBVersion() {
		DatabaseCursor cursor = dbHandler.rawQuery("select _version from fly_db_version");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			int ret = cursor.getInt(0);
			cursor.close();
			return ret;
		}
		return 0;
	}
	
	private void updateDBversion( int version ){
		 dbHandler.execSQL("update fly_db_version set _version=" + version );		
	}

	public static FlyDBManager getInstance() {
		if( Instance ==null)
			Instance = new FlyDBManager(); 
		return Instance;
	}

	/*
	 * execute one SQL without return value. please don't forget to call the
	 * open and close database methods.
	 */
	public void execSQL(String sql) {
		//Gdx.app.log("FlyDBManager.execSQL", "execSQL begin " + System.currentTimeMillis());
		Gdx.app.log("FlyDBManager.execSQL", sql);
		synchronized (dbHandler) {
			dbHandler.execSQL(sql);
		}
		//Gdx.app.log("FlyDBManager.execSQL", "execSQL end   " + System.currentTimeMillis());
	}
	
	public void openDatabase() {
		//Gdx.app.log("FlyDBManager.openDatabase", "open db begin " + System.currentTimeMillis());
		synchronized (dbHandler) {
			dbHandler.openOrCreateDatabase();
		}
		//Gdx.app.log("FlyDBManager.openDatabase", "open db end   " + System.currentTimeMillis());
	}

	/*
	 * execute one select SQL. please don't forget to call the open and close
	 * database methods.
	 */
	public DatabaseCursor selectData(String selectSQL) {
		//Gdx.app.log("FlyDBManager.selectData", "selectData  begin " + System.currentTimeMillis());
		DatabaseCursor cursor = null;
		//Gdx.app.log("FlyDBManager.selectData", selectSQL);
		synchronized (dbHandler) {
			cursor = dbHandler.rawQuery(selectSQL);
		}
		//Gdx.app.log("FlyDBManager.selectData", "selectData  end   " + System.currentTimeMillis());
		return cursor;
	}

	protected void closeDatabase() {
		try {
			if (dbHandler != null) {
				synchronized (dbHandler) {
					dbHandler.closeDatabase();
				}
			}
		} catch (Exception e) {
			Gdx.app.error("FlyDBManager.closeDatabase", e.toString());
		}
		//Gdx.app.log("FlyDBManager.closeDatabase", "close db at:" + System.currentTimeMillis());
	}

	public void dispose() {
		closeDatabase();
		Instance=null;
		Gdx.app.log("FlyDBManager", "db is closed and disposed");
	}
}
