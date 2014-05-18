package de.fau.cs.mad.fly.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Stores all the settings in a HashMap
 * 
 * @author Tobias Zangl
 */
public class SettingManager {
	private Skin skin;
	
	private Preferences prefs;
	private HashMap<String, Setting> settingMap;
	
	public SettingManager(String file, Skin skin) {
		this.skin = skin;
		prefs = Gdx.app.getPreferences(file);
		
		settingMap = new HashMap<String, Setting>();
	}
	
	/**
	 * Creates the table with the widgets and adds it to the Stage.
	 * @param stage
	 *            the parent Stage
	 * @param skin
	 *            the used Skin
	 */
	public void display(Stage stage, Skin skin) {
		Table table = new Table();
		//table.debug();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);
		
		for(Map.Entry<String, Setting> entry : settingMap.entrySet()) {
			table.row().expand();			
			table.add(entry.getValue().getLabel()).pad(2f);
			table.add(entry.getValue().getActor()).pad(2f);
		}
	}
	
	/**
	 * Adds a new Setting with a TextField and a String value.
	 * @param id
	 *            the id of the Setting
	 * @param description
	 *            the description of the Setting
	 * @param value
	 *            the default value of the Setting
	 */
	public void addTextSetting(String id, String description, String value) {
		if(!prefs.contains(id)) {
			prefs.putString(id, value);
			prefs.flush();
		} else {
			value = prefs.getString(id);
		}
		
		Setting setting = new Setting(this, id, description, value, skin);
		settingMap.put(id, setting);
	}
	
	/**
	 * Adds a new Setting with a SelectionBox and an int value.
	 * @param id
	 *            the id of the Setting
	 * @param description
	 *            the description of the Setting
	 * @param value
	 *            the default value of the Setting
	 * @param selectionList
	 *            the possible selections of the SelectionBox
	 */
	public void addSelectionSetting(String id, String description, int value, String[] selectionList) {
		if(!prefs.contains(id)) {
			prefs.putInteger(id, value);
			prefs.flush();
		} else {
			value = prefs.getInteger(id);
		}
		
		Setting setting = new Setting(this, id, description, value, selectionList, skin);
		settingMap.put(id, setting);
	}
	
	/**
	 * Adds a new Setting with a CheckBox and a boolean value.
	 * @param id
	 *            the id of the Setting
	 * @param description
	 *            the description of the Setting
	 * @param value
	 *            the default value of the Setting
	 */
	public void addCheckBoxSetting(String id, String description, boolean value) {
		if(!prefs.contains(id)) {
			prefs.putBoolean(id, value);
			prefs.flush();
		} else {
			value = prefs.getBoolean(id);
		}
		
		Setting setting = new Setting(this, id, description, value, skin);
		settingMap.put(id, setting);
	}
	
	/**
	 * Getter for the Preferences.
	 */
	public Preferences getPreferences() {
		return prefs;
	}
	
	/**
	 * Getter for the Text value.
	 */
	public String getTextValue(String id) {
		return settingMap.get(id).getText();
	}
	
	/**
	 * Getter for the Selection value.
	 */
	public int getSelectionValue(String id) {
		return settingMap.get(id).getSelection();
	}
	
	/**
	 * Getter for the CheckBox value.
	 */
	public boolean getCheckBoxValue(String id) {
		return settingMap.get(id).getCheckBox();
	}
	
	/**
	 * Saves all settings in the preference-file.
	 */
	public void saveSettings() {
		for(Map.Entry<String, Setting> entry : settingMap.entrySet()) {
			entry.getValue().saveSetting();
		}
		prefs.flush();
	}

}
