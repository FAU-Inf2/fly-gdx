package de.fau.cs.mad.fly.ios.sqlite;

import java.util.List;

import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseManager;

public class IOSDatabaseManager implements DatabaseManager {
	
	private class IOSDatabase implements Database {

		@Override
		public void setupDatabase() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void openOrCreateDatabase() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void closeDatabase() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void execSQL(String sql) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public DatabaseCursor rawQuery(String sql) {
			// TODO Auto-generated method stub
			return new IOSCursor();
		}

		@Override
		public DatabaseCursor rawQuery(DatabaseCursor cursor, String sql) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public Database getNewDatabase(String dbName, int dbVersion,
			List<String> dbOnCreateQuery, List<String> dbOnUpgradeQuery) {
		// TODO Auto-generated method stub
		return new IOSDatabase();
	}

}
