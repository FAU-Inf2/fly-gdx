package de.fau.cs.mad.fly.settings;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.fau.cs.mad.fly.ui.SkinManager;

/**
 * This class contains a Setting with a {@link #CheckBox} to manage a string.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class BooleanSetting extends ChangeListener implements ISetting {
    
    private String id;
    private boolean value;
    private SettingManager settingManager;
    private CheckBox checkBox;
    private String description;
    private String helpingText;
	private boolean hidden;
	private Groups group;
    
    /**
     * Initializes the class with the {@link #id}. Also a {@link CheckBox}
     * object and a {@link Label} is created.
     * 
     * @param settingManager
     * @param id
     * @param description
     * @param value
     */
    public BooleanSetting(SettingManager settingManager, String id, String description, boolean value, String helpingText, boolean hidden, Groups group) {
        this.settingManager = settingManager;
        this.id = id;
        
        this.description = description;
        
        this.value = value;
        this.helpingText = helpingText;
		this.hidden = hidden;
		this.group = group;
    }
    
    /**
     * Saves the current state in the {@link Preferences} object of
     * {@link #settingManager}. Also calls .flush() method to save the state
     * permanently.
     */
    private void save() {
        settingManager.getPreferences().putBoolean(id, value);
        settingManager.getPreferences().flush();
    }
    
    /**
     * Returns the string that identifies the helping text for this Setting in
     * the I18N
     */
    public String getHelpingText() {
        return helpingText;
    }

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public Groups group() {
		return group;
	}

	@Override
    public void changed(ChangeEvent event, Actor actor) {
        value = checkBox.isChecked();
        save();
    }
    
    @Override
    public String getDescription() {
        return description;
    }

	@Override
    public Actor getActor() {
        if (checkBox == null) {
            this.checkBox = new CheckBox("", SkinManager.getInstance().getSkin());
        }
        this.checkBox.setChecked(value);
        this.checkBox.addListener(this);
        return checkBox;
    }
    
}
