package com.badlogic.gdx.sqlite.android;

import java.util.List;

import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseManager;

public class EmptyDatabaseManager implements DatabaseManager {
	
	private class IOSDatabase implements Database {

		@Override
		public void setupDatabase() {			
		}

		@Override
		public void openOrCreateDatabase() {
		}

		@Override
		public void closeDatabase() {
		}

		@Override
		public void execSQL(String sql) {
		}

		@Override
		public DatabaseCursor rawQuery(String sql) {
			return new EmptyCursor();
		}

		@Override
		public DatabaseCursor rawQuery(DatabaseCursor cursor, String sql) {
			return null;
		}
		
	}

	@Override
	public Database getNewDatabase(String dbName, int dbVersion,
			List<String> dbOnCreateQuery, List<String> dbOnUpgradeQuery) {
		return new IOSDatabase();
	}

}
