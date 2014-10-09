package de.fau.cs.mad.fly.settings;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.fau.cs.mad.fly.ui.SkinManager;

/**
 * This class contains a Setting with a {@link Slider} to manage a float value.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class FloatSetting extends ChangeListener implements ISetting {
    
    private String id;
    private float value;
    private SettingManager settingManager;
    private Slider slider;
    private String description;
    private String helpingText;
	private boolean hidden;
	private Groups group;
    
    /**
     * Initializes the class with the {@link #id}. Also a {@link Slider} object
     * and a {@link Label} is created.
     * 
     * @param settingManager
     * @param id
     * @param description
     * @param value
     */
    public FloatSetting(SettingManager settingManager, String id, String description, float value, float minValue, float maxValue, float stepSize, String helpingText, boolean hidden, Groups group) {
        this.settingManager = settingManager;
        this.id = id;
        this.helpingText = helpingText;

		this.description = description;
        
        this.value = value;
        slider = new Slider(minValue, maxValue, stepSize, false, SkinManager.getInstance().getSkin());
        slider.setValue(value);
        this.slider.addListener(this);
		this.hidden = hidden;
		this.group = group;
    }
    
    /**
     * Saves the current state in the {@link Preferences} object of
     * {@link #settingManager}. Also calls .flush() method to save the state
     * permanently.
     */
    private void save() {
        settingManager.getPreferences().putFloat(id, value);
        settingManager.getPreferences().flush();
    }
    
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        value = slider.getValue();
        save();
    }

	@Override
	public String getDescription() {
		return description;
	}

	@Override
    public Actor getActor() {
        return slider;
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
		return group;
	}

}
