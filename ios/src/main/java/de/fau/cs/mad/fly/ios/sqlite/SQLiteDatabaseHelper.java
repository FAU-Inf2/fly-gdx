
package de.fau.cs.mad.fly.ios.sqlite;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/** @author M Rafay Aleem */
public class SQLiteDatabaseHelper {

	private final List<String> dbOnCreateQuery;
	private final List<String> dbOnUpgradeQuery;

	public SQLiteDatabaseHelper(String dbName, int dbVersion, List<String> dbOnCreateQuery, List<String> dbOnUpgradeQuery) {
		this.dbOnCreateQuery = dbOnCreateQuery;
		this.dbOnUpgradeQuery = dbOnUpgradeQuery;
	}

	public void onCreate (Statement stmt) throws SQLException {
		if (dbOnCreateQuery != null){
			for(String sql : dbOnCreateQuery){
				stmt.executeUpdate(sql);
			}
		}
	}

	public void onUpgrade (Statement stmt, int oldVersion, int newVersion) throws SQLException {
		if (dbOnUpgradeQuery != null) {
			for(String sql : dbOnUpgradeQuery){
				stmt.executeUpdate(sql);
			}
			onCreate(stmt);
		}
	}

}
