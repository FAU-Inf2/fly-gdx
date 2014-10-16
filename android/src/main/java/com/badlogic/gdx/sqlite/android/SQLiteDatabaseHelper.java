package com.badlogic.gdx.sqlite.android;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/** @author M Rafay Aleem */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
	private final List<String> dbOnCreateQuery;
	private final List<String> dbOnUpgradeQuery;

	public SQLiteDatabaseHelper(Context context, String name, CursorFactory factory, int version,
			List<String> dbOnCreateQuery, List<String> dbOnUpgradeQuery) {
		super(context, name, factory, version);
		
		this.dbOnCreateQuery = dbOnCreateQuery;
		this.dbOnUpgradeQuery = dbOnUpgradeQuery;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		if (dbOnCreateQuery != null)
			for (String sql : dbOnCreateQuery) {
				database.execSQL(sql);
			}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (dbOnUpgradeQuery != null) {
			for (String sql : dbOnUpgradeQuery) {
				database.execSQL(sql);
			}
			onCreate(database);
		}
	}

}
