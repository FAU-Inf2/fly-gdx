package de.fau.cs.mad.fly.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;

import de.fau.cs.mad.fly.features.overlay.DebugOverlay;

/**
 * Simple debug helper class.
 * <p>
 * Call Debug.init(game, stage, 1-3) in game controller builder if you need
 * an overlay with 1-3 possible values to display data.
 * Use Debug.setOverlay(0-2, value) anywhere in your init/render/... methods
 * to display the value at position 0-2.
 * Use Debug.log() methods to debug on the console.
 * 
 * @author Tobias Zangl
 */
public class Debug {
	public static DebugOverlay debugOverlay;
	
	private static boolean debugMode = false;
	
	private static long lastTimeMS = 0;
	private static long lastTimeNS = 0;

	public static void init(final Skin skin, final Stage stage, int index) {
		debugOverlay = new DebugOverlay(skin, stage, index);
	}
	
	public static boolean isDebug() {
		return debugMode;
	}
	
	public static void setOverlay(int index, int value) {
		if(debugOverlay == null)
			return;
		
		debugOverlay.setValue(index, String.valueOf(value));
	}
	
	public static void setOverlay(int index, float value) {
		if(debugOverlay == null)
			return;
		
		debugOverlay.setValue(index, String.valueOf(value));
	}
	
	public static void setOverlay(int index, String value) {
		if(debugOverlay == null)
			return;
		
		debugOverlay.setValue(index, value);
	}
	
	
	/**
	 * Returns the current time in milli seconds and stores it in the static variable lastTimeMS
	 * @return lastTimeMS
	 */
	public static long getMSTime() {
		lastTimeMS = TimeUtils.millis();
		return lastTimeMS;
	}
	
	/**
	 * Returns the current time in nano seconds and stores it in the static variable lastTimeNS
	 * @return lastTimeNS
	 */
	public static long getNSTime() {
		lastTimeNS = TimeUtils.nanoTime();
		return lastTimeNS;
	}

	/**
	 * Returns the time in milli seconds since the last getMSTime() call and stores the current time in the static variable lastTimeMS
	 * @return timeDiff
	 */
	public static long getMSTimeSinceLast() {
		long timeDiff = TimeUtils.millis() - lastTimeMS;
		lastTimeMS = TimeUtils.millis();
		return timeDiff;
	}
	
	/**
	 * Returns the time in nano seconds since the last getNSTime() call and stores the current time in the static variable lastTimeNS
	 * @return timeDiff
	 */
	public static long getNSTimeSinceLast() {
		long timeDiff = TimeUtils.nanoTime() - lastTimeNS;
		lastTimeNS = TimeUtils.nanoTime();
		return timeDiff;
	}	

	
	public static void setLogLevel(int logLevel) {
		Gdx.app.setLogLevel(logLevel);
	}
	
	public static void log(String message) {
		Gdx.app.log("FLY", message);
	}
	
	public static void log(String tag, String message) {
		Gdx.app.log(tag, message);
	}
	
	public static void error(String tag, String message, Throwable exception) {
		Gdx.app.error(tag, message, exception);
	}
	
	public static void debug(String tag, String message) {
		Gdx.app.debug(tag, message);
	}
}