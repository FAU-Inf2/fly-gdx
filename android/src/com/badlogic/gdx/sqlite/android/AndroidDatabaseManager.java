package com.badlogic.gdx.sqlite.android;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseManager;

/** @author M Rafay Aleem */
public class AndroidDatabaseManager implements DatabaseManager {

	private Context context;

	private class AndroidDatabase implements Database {

		private SQLiteDatabaseHelper helper;
		private SQLiteDatabase database;
		private Context context;

		private final String dbName;
		private final int dbVersion;
		private final List<String> dbOnCreateQuery;
		private final List<String> dbOnUpgradeQuery;

		private AndroidDatabase(Context context, String dbName, int dbVersion,
				List<String> dbOnCreateQuery, List<String> dbOnUpgradeQuery) {
			this.context = context;
			this.dbName = dbName;
			this.dbVersion = dbVersion;
			this.dbOnCreateQuery = dbOnCreateQuery;
			this.dbOnUpgradeQuery = dbOnUpgradeQuery;
		}

		@Override
		public void setupDatabase() {
			helper = new SQLiteDatabaseHelper(this.context, dbName, null, dbVersion,
					dbOnCreateQuery, dbOnUpgradeQuery);
		}

		@Override
		public void openOrCreateDatabase() {
			if (database == null || !database.isOpen()) {
				database = helper.getWritableDatabase();
			}			
		}

		@Override
		public void closeDatabase() {
			helper.close();
		}

		@Override
		public void execSQL(String sql) {
			database.execSQL(sql);
		}

		@Override
		public DatabaseCursor rawQuery(String sql) {
			AndroidCursor aCursor = new AndroidCursor();
			Cursor tmp = database.rawQuery(sql, null);
			aCursor.setNativeCursor(tmp);
			return aCursor;

		}

		@Override
		public DatabaseCursor rawQuery(DatabaseCursor cursor, String sql) {
			AndroidCursor aCursor = (AndroidCursor) cursor;
			Cursor tmp = database.rawQuery(sql, null);
			aCursor.setNativeCursor(tmp);
			return aCursor;
		}

	}

	public AndroidDatabaseManager() {
		AndroidApplication app = (AndroidApplication) Gdx.app;
		context = app.getApplicationContext();
	}

	@Override
	public Database getNewDatabase(String databaseName, int databaseVersion,
			List<String> databaseCreateQuery, List<String> dbOnUpgradeQuery) {
		return new AndroidDatabase(this.context, databaseName, databaseVersion,
				databaseCreateQuery, dbOnUpgradeQuery);
	}

}
