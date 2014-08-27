package de.fau.cs.mad.fly.db;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;

import de.fau.cs.mad.fly.settings.AppSettingsManager;

/**
 * Manages the database of fly 
 *
 * @ Qufang Fan
 */
public class FlyDBManager {

	private static final String DATABASE_NAME = "faumadfly.db";
	private static final int DATABASE_VERSION = 1;
	private static final int LASTEST_DATABASE_VERSION = 2;

	Database dbHandler;

	

	
	private FlyDBManager() {
		Gdx.app.log("FlyDBManager", "setupDatabase begin " + System.currentTimeMillis());
		String createTablePlayer = "create table if not exists player(player_id integer primary key autoincrement, fly_id text, name text not null, social_type text, social_name text, social_password text)";
		String createTableScore = "create table if not exists score(player_id integer not null, level_id integer not null, score integer not null, compare_score text, reached_date date)";
		String createTableScoreDetail = "create table if not exists score_detail(scoredetail_id integer primary key autoincrement, player_id integer not null, level_id integer not null,score_detail text not null, _value text)";

		List<String> createSQLs = new ArrayList<String>();
		createSQLs.add(createTablePlayer);
		createSQLs.add(createTableScore);
		createSQLs.add(createTableScoreDetail);
		List<String> upgradeSQLs = null;		
		dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME, DATABASE_VERSION, createSQLs,
				upgradeSQLs);
		
		synchronized (dbHandler) {
			dbHandler.setupDatabase();
			Gdx.app.log("FlyDBManager", "setupDatabase end   " + System.currentTimeMillis());
		
			dbHandler.openOrCreateDatabase();
			Gdx.app.log("FlyDBManager", "database opened " + System.currentTimeMillis());
			
			int dbVersion = AppSettingsManager.Instance.getIntegerSetting(AppSettingsManager.DATABASE_VERSION, 0);
			if(dbVersion < 2 )
			{				
				dbHandler.execSQL("alter table score add column level_group_id integer not null default  1");
				dbHandler.execSQL("alter table score_detail add column level_group_id integer not null default 1");
				dbHandler.execSQL("alter table player add column total_score integer not null default 0");
				dbHandler.execSQL("alter table player add column total_geld integer not null default 0");
			}
			AppSettingsManager.Instance.setIntegerSetting(AppSettingsManager.DATABASE_VERSION, LASTEST_DATABASE_VERSION);		
		}
		
	}

	private static FlyDBManager Instance = new FlyDBManager();

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
		//Gdx.app.log("FlyDBManager.execSQL", sql);
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
