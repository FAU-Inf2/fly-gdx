package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

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

	public static void init(Fly game, Stage stage, int index) {
		debugOverlay = new DebugOverlay(game, stage, index);
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