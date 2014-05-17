package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

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
	
	public Preferences getPreferences() {
		return prefs;
	}
	
	public String getTextValue(String id) {
		return settingMap.get(id).getText();
	}
	
	public int getSelectionValue(String id) {
		return settingMap.get(id).getSelection();
	}
	
	public boolean getCheckBoxValue(String id) {
		return settingMap.get(id).getCheckBox();
	}
	
	public void saveSettings() {
		for(Map.Entry<String, Setting> entry : settingMap.entrySet()) {
			entry.getValue().saveSetting();
		}
		prefs.flush();
	}

}
