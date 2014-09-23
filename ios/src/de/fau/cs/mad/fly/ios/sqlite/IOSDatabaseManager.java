package de.fau.cs.mad.fly.ios.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.DatabaseManager;
import com.badlogic.gdx.utils.GdxRuntimeException;

import SQLite.JDBC2z.JDBCConnection;

public class IOSDatabaseManager implements DatabaseManager {


    private class IOSDatabase implements Database {

        private SQLiteDatabaseHelper helper = null;

        private final String dbName;
        private final int dbVersion;
        private final List<String> dbOnCreateQuery;
        private final List<String> dbOnUpgradeQuery;

        private Connection connection = null;
        private Statement stmt = null;

        private IOSDatabase(String dbName, int dbVersion, List<String> dbOnCreateQuery,
                                List<String> dbOnUpgradeQuery) {
            this.dbName = dbName;
            this.dbVersion = dbVersion;
            this.dbOnCreateQuery = dbOnCreateQuery;
            this.dbOnUpgradeQuery = dbOnUpgradeQuery;
        }

        @Override
        public void setupDatabase() {
            try {
                Class.forName("SQLite.JDBCDriver");
            } catch (ClassNotFoundException e) {
                Gdx.app.log(
                        DatabaseFactory.ERROR_TAG,
                        "Unable to load the SQLite JDBC driver. Their might be a problem with your build path or project setup.",
                        e);
                throw new GdxRuntimeException(e);
            }
        }

        @Override
        public void openOrCreateDatabase() {
            if (helper == null)
                helper = new SQLiteDatabaseHelper(dbName, dbVersion, dbOnCreateQuery,
                        dbOnUpgradeQuery);

            try {
                connection =  new JDBCConnection("jdbc:sqlite:/" + System.getenv("HOME") + "/Documents" + dbName, "", "", "", System.getProperty("SQLite.vfs"));
                stmt = connection.createStatement();
                helper.onCreate(stmt);
            } catch (SQLException e) {
                Gdx.app.log("IOSDatabaseManager.openOrCreateDatabase", "threw an SQLException");
                throw new GdxRuntimeException(e);
            }

        }

        @Override
        public void closeDatabase() {

            try {
                stmt.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void execSQL(String sql) {

            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public DatabaseCursor rawQuery(String sql) {
            IOSCursor lCursor = new IOSCursor();
            try {
                ResultSet resultSetRef = stmt.executeQuery(sql);
                lCursor.setNativeCursor(resultSetRef);
                return lCursor;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public DatabaseCursor rawQuery(DatabaseCursor cursor, String sql) {
            IOSCursor lCursor = (IOSCursor) cursor;
            try {
                ResultSet resultSetRef = stmt.executeQuery(sql);
                lCursor.setNativeCursor(resultSetRef);
                return lCursor;
            } catch (SQLException e) {
                throw new RuntimeException(e);// new SQLiteGdxException(e);
            }
        }

    }

    @Override
    public Database getNewDatabase(String dbName, int dbVersion, List<String> dbOnCreateQuery,
                                   List<String> dbOnUpgradeQuery) {
        return new IOSDatabase(dbName, dbVersion, dbOnCreateQuery, dbOnUpgradeQuery);
    }

}