package com.badlogic.gdx.sqlite.android;

import com.badlogic.gdx.sql.DatabaseCursor;

public class EmptyCursor implements DatabaseCursor {

	@Override
	public byte[] getBlob(int columnIndex) {
		return null;
	}

	@Override
	public double getDouble(int columnIndex) {
		return 0;
	}

	@Override
	public float getFloat(int columnIndex) {
		return 0;
	}

	@Override
	public int getInt(int columnIndex) {
		return 0;
	}

	@Override
	public long getLong(int columnIndex) {
		return 0;
	}

	@Override
	public short getShort(int columnIndex) {
		return 0;
	}

	@Override
	public String getString(int columnIndex) {
		return null;
	}

	@Override
	public boolean next() {
		return false;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public void close() {
	}

}
