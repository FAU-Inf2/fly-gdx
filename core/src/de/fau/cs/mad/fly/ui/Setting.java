package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


/**
 * Stores one setting and displays its description and its value
 * 
 * @author Tobias Zangl
 */
public class Setting extends ChangeListener {
	private SettingManager manager;
	private String id;
	private String description;
	private SettingType type;
	
	private String textValue;
	private int selectionValue;
	private boolean checkBoxValue;
	
	private String[] selectionList;
	
	private Label label;
	private Actor actor;
	
	private TextField textField;
	private SelectBox<String> selectBox;
	private CheckBox checkBox;
	
	public enum SettingType {
		TEXT, SELECTION, CHECKBOX
	}
	
	public Setting(SettingManager manager, String id, String description, String value, Skin skin) {
		this.type = SettingType.TEXT;
		this.manager = manager;
		this.id = id;
		this.description = description;
		this.textValue = value;
		
		label = new Label(description, skin);
		textField = new TextField(value, skin);
		actor = textField;
		
		textField.addListener(this);
	}
	
	public Setting(SettingManager manager, String id, String description, int value, String[] selectionList, Skin skin) {
		this.type = SettingType.SELECTION;
		this.manager = manager;
		this.id = id;
		this.description = description;
		this.selectionValue = value;
		this.selectionList = selectionList;

		label = new Label(description, skin);
		selectBox = new SelectBox<String>(skin);
		selectBox.setItems((String[]) selectionList);
		selectBox.setSelectedIndex(value);
		actor = selectBox;
		
		selectBox.addListener(this);
	}
	
	public Setting(SettingManager manager, String id, String description, boolean value, Skin skin) {
		this.type = SettingType.CHECKBOX;
		this.manager = manager;
		this.id = id;
		this.description = description;
		this.checkBoxValue = value;
		
		label = new Label(description, skin);
		checkBox = new CheckBox("", skin);
		checkBox.setChecked(value);
		actor = checkBox;
		
		checkBox.addListener(this);
	}
	
	public SettingType getType() {
		return type;
	}
	
	public Label getLabel() {
		return label;
	}

	public Actor getActor() {
		return actor;
	}
	
	public String getText() {
		return textValue;
	}
	
	public int getSelection() {
		return selectionValue;
	}
	
	public boolean getCheckBox() {
		return checkBoxValue;
	}
	
	public void saveSetting() {
		if(type == SettingType.TEXT) {
			manager.getPreferences().putString(id, textValue);
		} else if(type == SettingType.SELECTION) {
			manager.getPreferences().putInteger(id, selectionValue);
		} else if(type == SettingType.CHECKBOX) {
			manager.getPreferences().putBoolean(id, checkBoxValue);
		}
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {
		if(type == SettingType.TEXT) {
			textValue = textField.getText();
		} else if(type == SettingType.SELECTION) {
			selectionValue = selectBox.getSelectedIndex();
		} else if(type == SettingType.CHECKBOX) {
			checkBoxValue = checkBox.isChecked();
		}
	}

}
