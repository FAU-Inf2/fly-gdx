package de.fau.cs.mad.fly.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.ui.BasicScreen;
import de.fau.cs.mad.fly.ui.BasicScreenWithBackButton;
import de.fau.cs.mad.fly.ui.SkinManager;

/**
 * Stores all the settings in a HashMap
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class SettingManager {
    
    public static final String USE_TOUCH = "useTouch";
    public static final String SHOW_FPS = "showFPS";
    public static final String VIBRATE_WHEN_COLLIDE = "vibrateWhenCollide";
    public static final String INVERT_X = "invertXAxis";
    public static final String INVERT_Y = "invertYAxis";
    public static final String CHOSEN_PLANE_ID = "chosenPlaneID";
    public static final String DISABLE_TUTORIALS = "disableTutorials";
    public static final String DISABLE_SOUND = "disableSound";
	public static final String MOVE_UP = "moveUp";
	public static final String MOVE_LEFT = "moveLeft";
	public static final String MOVE_DOWN = "moveDown";
	public static final String MOVE_RIGHT = "moveRight";
    
    private Preferences prefs;
    private List<ISetting> settings;

    public SettingManager(String preferenceFileName) {
        prefs = Gdx.app.getPreferences(preferenceFileName);
        settings = new ArrayList<ISetting>();
        createSettings();
    }
    
    /**
     * Adds a new setting with a CheckBox and a boolean value.
     * 
     * @param id
     *            the id of the Setting. Also used to find the description in
     *            the I18N files.
     * @param defaultValue
     *            the default value of the Setting
     * @param helpingText
     *            String that identifies the helping text in the I18N
     * @param hidden
     *            defines weather to hide (true) or to show (false) the option
     *            in the ui
     */
    public void addBooleanSetting(String id, boolean defaultValue, String helpingText, boolean hidden, ISetting.Groups group) {
        boolean value = defaultValue;
        if (prefs.contains(id)) {
            value = prefs.getBoolean(id);
        } else {
            prefs.putBoolean(id, defaultValue);
            prefs.flush();
        }
        
        ISetting newBooleanSetting = new BooleanSetting(this, id, I18n.t(id) + ":", value, helpingText, hidden, group);
        settings.add(newBooleanSetting);
    }
    
    /**
     * Adds a new Setting with a Slider and a float value.
     * 
     * @param id
     *            the id of the Setting
     * @param description
     *            the description of the Setting
     * @param value
     *            the default value of the Setting
     * @param min
     *            the minimum value of the Slider
     * @param max
     *            the maximum value of the Slider
     * @param stepSize
     *            the step size of the Slider
     * @param helpingText
     *            String that identifies the helping text in the I18N
     */
    public void addFloatSetting(String id, String description, float value, float min, float max, float stepSize, String helpingText, boolean hidden, ISetting.Groups group) {
        if (!prefs.contains(id)) {
            prefs.putFloat(id, value);
            prefs.flush();
        } else {
            value = prefs.getFloat(id);
        }
        
        ISetting newFloatSetting = new FloatSetting(this, id, description, value, min, max, stepSize, helpingText, hidden, group);
        settings.add(newFloatSetting);
    }

	public void addKeyboardBinding(final String id, int defaultValue, final String helpingText, final boolean hidden) {
		final int value = prefs.getInteger(id, defaultValue);
		if ( !prefs.contains( id ) ) {
			prefs.putInteger(id, value);
			prefs.flush();
		}
		ISetting binding = new ISetting() {

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
							Fly fly = (Fly) Gdx.app.getApplicationListener();
							BasicScreen screen = (BasicScreen) fly.getScreen();
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
							dialog.show(screen.getStage());
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
		settings.add(binding);
	}
    
    /**
     * Getter for the {@link #prefs}.
     */
    public Preferences getPreferences() {
        return prefs;
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
		addBooleanSetting(DISABLE_SOUND, false, "helpDisableSound", false, ISetting.Groups.AUDIO);
        switch (Gdx.app.getType()) {
            case Android:
            case iOS:
                addBooleanSetting(VIBRATE_WHEN_COLLIDE, true, "helpVibrate", false, ISetting.Groups.GENERAL);
                addBooleanSetting(USE_TOUCH, false, "helpTouch", false, ISetting.Groups.CONTROLS);
                break;
			case Desktop:
				addKeyboardBinding(MOVE_UP, Input.Keys.W, "helpMoveUp", false);
				addKeyboardBinding(MOVE_LEFT, Input.Keys.A, "helpMoveLeft", false);
				addKeyboardBinding(MOVE_DOWN, Input.Keys.S, "helpMoveDown", false);
				addKeyboardBinding(MOVE_RIGHT, Input.Keys.D, "helpMoveRight", false);
				break;
        }
    }
}
