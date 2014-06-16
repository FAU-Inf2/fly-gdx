package de.fau.cs.mad.fly.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.fau.cs.mad.fly.Fly;

/**
 * This class contains a Setting with a {@link #CheckBox} to manage a string.
 * 
 * @author Lukas Hahmann
 * 
 */
public class BooleanSetting extends ChangeListener implements ISetting {

	private String id;
	private boolean value;
	private SettingManager settingManager;
	private CheckBox checkBox;
	private Label label;
	
	/**
	 * Initializes the class with the {@link #id}. Also a {@link CheckBox} object and a {@link Label} is created. 
	 * @param settingManager
	 * @param id
	 * @param description
	 * @param value
	 */
	public BooleanSetting(SettingManager settingManager, String id, String description, boolean value) {
		this.settingManager = settingManager;
		this.id = id;
		
		Skin skin = ((Fly) Gdx.app.getApplicationListener()).getSkin();
		this.label = new Label(description, skin);
		
		this.value = value;
		this.checkBox = new CheckBox("", skin);
		this.checkBox.setChecked(value);
		//TODO: remove this and use skin instead
		this.checkBox.getCells().get(0).size(0.05f * Gdx.graphics.getWidth(), 0.05f * Gdx.graphics.getHeight());
		this.checkBox.addListener(this);
	}
	
	/**
	 * Saves the current state in the {@link Preferences} object of {@link #settingManager}. 
	 * Also calls .flush() method to save the state permanently.
	 */
	private void save() {
		settingManager.getPreferences().putBoolean(id, value);
		settingManager.getPreferences().flush();
	}


	@Override
	public void changed(ChangeEvent event, Actor actor) {
		value = checkBox.isChecked();
		save();
	}


	@Override
	public Label getLabel() {
		return label;
	}

	@Override
	public Actor getActor() {
		return checkBox;
	}

}
