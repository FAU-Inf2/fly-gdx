package de.fau.cs.mad.fly.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.*;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.ui.SkinManager;

/**
 * Stores all the settings in a HashMap
 *
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class SettingManager {

	public static interface SettingListener {
		public void settingChanged(String id, Object value);
	}

	public static final String USE_TOUCH = "useTouch";
	public static final String SHOW_FPS = "showFPS";
	public static final String VIBRATE_WHEN_COLLIDE = "vibrateWhenCollide";
	public static final String INVERT_X = "invertXAxis";
	public static final String INVERT_Y = "invertYAxis";
	public static final String CHOSEN_PLANE_ID = "chosenPlaneID";
	public static final String DISABLE_TUTORIALS = "disableTutorials";
	public static final String MASTER_VOLUME = "masterVolume";
	public static final String DISABLE_SOUND = "disableSound";
	public static final String MOVE_UP = "moveUp";
	public static final String MOVE_LEFT = "moveLeft";
	public static final String MOVE_DOWN = "moveDown";
	public static final String MOVE_RIGHT = "moveRight";

	private Preferences prefs;
	private List<ISetting> settings = new ArrayList<ISetting>();
	private Map<String, List<SettingListener>> listeners = new HashMap<String, List<SettingListener>>();

	public SettingManager(String preferenceFileName) {
		Gdx.app.log("SettingManager.SettingManager", "parameters[preferenceFileName=" + preferenceFileName + "]");
		prefs = Gdx.app.getPreferences(preferenceFileName);
		createSettings();
	}

	public void addListener(String id, SettingListener listener) {
		lazyListeners(id).add(listener);
	}

	public boolean getBoolean(String id) {
		return getBoolean(id, false, false);
	}

	public boolean getBoolean(String id, boolean defaultValue) {
		return getBoolean(id, defaultValue, true);
	}

	public boolean getBoolean(String id, boolean defaultValue, boolean set) {
		boolean value = prefs.getBoolean(id, defaultValue);
		if ( set && !prefs.contains(id) )
			set(id, value);
		return value;
	}

	public float getFloat(String id) {
		return prefs.getFloat(id);
	}

	public float getFloat(String id, float defaultValue) {
		return getFloat(id, defaultValue, true);
	}

	public float getFloat(String id, float defaultValue, boolean set) {
		float value = prefs.getFloat(id, defaultValue);
		if ( set && !prefs.contains(id) )
			set(id, value);
		return value;
	}

	public int getInteger(String id) {
		return prefs.getInteger(id);
	}

	public int getInteger(String id, int defaultValue) {
		return getInteger(id, defaultValue, true);
	}

	public int getInteger(String id, int defaultValue, boolean set) {
		int value = prefs.getInteger(id, defaultValue);
		if ( set && !prefs.contains(id) )
			set(id, value);
		return value;
	}

	public void set(String id, float f) {
		set(id, f, true);
	}

	public void set(String id, float f, boolean flush) {
		prefs.putFloat(id, f);
		if ( flush )
			prefs.flush();
		fire(id, f);
	}

	public void set(String id, int i) {
		set(id, i, true);
	}

	public void set(String id, int i, boolean flush) {
		prefs.putInteger(id, i);
		if ( flush )
			prefs.flush();
		fire(id, i);
	}

	public void set(String id, boolean b) {
		set(id, b, true);
	}

	public void set(String id, boolean b, boolean flush) {
		prefs.putBoolean(id, b);
		if ( flush )
			prefs.flush();
		fire(id, b);
	}

	public void clear() {
		prefs.clear();
		prefs.flush();
	}

	private List<SettingListener> lazyListeners(String id) {
		List<SettingListener> list = listeners.get(id);
		if ( list == null )
			listeners.put(id, list = new ArrayList<SettingListener>());
		return list;
	}

	private void fire(String id, Object value) {
		for ( SettingListener listener : lazyListeners(id) )
			listener.settingChanged(id, value);
	}

	/**
	 * Adds a new setting with a CheckBox and a boolean value.
	 *
	 * @param id           the id of the Setting. Also used to find the description in
	 *                     the I18N files.
	 * @param defaultValue the default value of the Setting
	 * @param helpingText  String that identifies the helping text in the I18N
	 * @param hidden       defines weather to hide (true) or to show (false) the option
	 *                     in the ui
	 */
	private void addBooleanSetting(String id, boolean defaultValue, String helpingText, boolean hidden, ISetting.Groups group) {
		boolean value = getBoolean(id, defaultValue);

		settings.add(new BooleanSetting(this, id, I18n.t(id), value, helpingText, hidden, group));
	}

	/**
	 * Adds a new Setting with a Slider and a float value.
	 *
	 * @param id          the id of the Setting
	 * @param defaultValue       the default value of the Setting
	 * @param min         the minimum value of the Slider
	 * @param max         the maximum value of the Slider
	 * @param stepSize    the step size of the Slider
	 * @param helpingText String that identifies the helping text in the I18N
	 */
	private void addFloatSetting(String id, float defaultValue, float min, float max, float stepSize, String helpingText, boolean hidden, ISetting.Groups group) {
		float value = getFloat(id, defaultValue);

		settings.add(new FloatSetting(this, id, I18n.t(id), value, min, max, stepSize, helpingText, hidden, group));
	}

	private void addKeyboardBinding(final String id, int defaultValue, final String helpingText, final boolean hidden) {
		listeners.put(id, new ArrayList<SettingListener>());

		final int value = getInteger(id, defaultValue);
		ISetting setting = new ISetting() {

			private TextButton button;

			@Override
			public String getDescription() {
				return I18n.t(id);
			}

			@Override
			public Actor getActor() {
				if ( button == null ) {
					final Skin skin = SkinManager.getInstance().getSkin();
					button = new TextButton(Input.Keys.toString(value), skin);
					button.addListener(new ChangeListener() {

						@Override
						public void changed(ChangeEvent event, Actor actor) {
							final Dialog dialog = new Dialog("Press a key\n", skin);
							dialog.addListener(new InputListener() {

								@Override
								public boolean keyDown(InputEvent e, int keycode) {
									prefs.putInteger(id, keycode);
									prefs.flush();
									button.setText(Input.Keys.toString(keycode));
									dialog.hide();
									return false;
								}
							});
							dialog.show(event.getStage());
						}
					});
				}
				return button;
			}

			@Override
			public String getHelpingText() {
				return helpingText;
			}

			@Override
			public boolean isHidden() {
				return hidden;
			}

			@Override
			public Groups group() {
				return Groups.CONTROLS;
			}
		};
		settings.add(setting);
	}

	/**
	 * Getter for the {@link #settings}
	 */
	public List<ISetting> getSettings() {
		return settings;
	}


	/**
	 * Creates all settings that should be displayed in the
	 * {@link de.fau.cs.mad.fly.ui.SettingScreen}.
	 */
	private void createSettings() {
		// addBooleanSetting(SHOW_PAUSE, false);
		addBooleanSetting(INVERT_X, false, "helpXInversion", false, ISetting.Groups.CONTROLS);
		addBooleanSetting(INVERT_Y, false, "helpYInversion", false, ISetting.Groups.CONTROLS);
		addBooleanSetting(SHOW_FPS, false, "helpShowFPS", false, ISetting.Groups.GENERAL);
		addBooleanSetting(DISABLE_TUTORIALS, false, "helpDisableTutorials", false, ISetting.Groups.GENERAL);
		addFloatSetting(MASTER_VOLUME, 1.0f, 0.0f, 1.0f, 0.01f, "Help", false, ISetting.Groups.AUDIO);
//		addBooleanSetting(DISABLE_SOUND, false, "helpDisableSound", false, ISetting.Groups.AUDIO);
		switch (Gdx.app.getType()) {
			case Android:
			case iOS:
				addBooleanSetting(VIBRATE_WHEN_COLLIDE, true, "helpVibrate", false, ISetting.Groups.GENERAL);
				addBooleanSetting(USE_TOUCH, false, "helpTouch", false, ISetting.Groups.CONTROLS);
				break;
			case Desktop:
//				addScreenResolutionSetting();
				addKeyboardBinding(MOVE_UP, Input.Keys.W, "helpMoveUp", false);
				addKeyboardBinding(MOVE_LEFT, Input.Keys.A, "helpMoveLeft", false);
				addKeyboardBinding(MOVE_DOWN, Input.Keys.S, "helpMoveDown", false);
				addKeyboardBinding(MOVE_RIGHT, Input.Keys.D, "helpMoveRight", false);
				break;
		}
	}

	private void addScreenResolutionSetting() {
		settings.add(new ISetting() {

			@Override
			public String getDescription() {
				return "Screen resolution";
			}

			@Override
			public Actor getActor() {
				final Skin skin = SkinManager.getInstance().getSkin();
				final SelectBox<Graphics.DisplayMode> box = new SelectBox<Graphics.DisplayMode>(skin);
				box.setItems(Gdx.graphics.getDisplayModes());
				box.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						Gdx.graphics.setDisplayMode(box.getSelected().width, box.getSelected().height, false);
					}
				});
				return box;
			}

			@Override
			public String getHelpingText() {
				return "The display mode of your screen";
			}

			@Override
			public boolean isHidden() {
				return false;
			}

			@Override
			public Groups group() {
				return Groups.GENERAL;
			}
		});
	}
}
