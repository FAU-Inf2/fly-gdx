package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Stores one setting and displays its description and its value
 * 
 * @author Tobias Zangl
 */
public class Setting extends ChangeListener {
	private final SettingManager manager;
	private String id;
	private SettingType type;
	
	private String textValue;
	private int selectionValue;
	private boolean checkBoxValue;
	private float sliderValue;
	
	private Label label;
	private Actor actor;
	
	private TextField textField;
	private SelectBox<String> selectBox;
	private CheckBox checkBox;
	private Slider slider;
	
	public enum SettingType {
		TEXT, SELECTION, CHECKBOX, SLIDER
	}
	

	/**
	 * Creates a new Setting with a TextField.
	 * @param manager
	 *            the parent SettingManager
	 * @param id
	 *            the id of the Setting
	 * @param description
	 *            the description of the Setting
	 * @param value
	 *            the default value of the Setting
	 * @param skin
	 *            the Skin of the UI        
	 */
	Setting(final SettingManager manager, String id, String description, String value, final Skin skin) {
		this.type = SettingType.TEXT;
		this.manager = manager;
		this.id = id;
		this.textValue = value;
		
		label = new Label(description, skin);
		textField = new TextField(value, skin);
		actor = textField;
		
		textField.addListener(this);
	}
	
	/**
	 * Creates a new Setting with a SelectionBox
	 * @param manager
	 *            the parent SettingManager
	 * @param id
	 *            the id of the Setting
	 * @param description
	 *            the description of the Setting
	 * @param value
	 *            the default value of the Setting
	 * @param selectionList
	 *            the possible selections of the SelectionBox
	 * @param skin
	 *            the Skin of the UI        
	 */
	public Setting(final SettingManager manager, String id, String description, int value, String[] selectionList, final Skin skin) {
		this.type = SettingType.SELECTION;
		this.manager = manager;
		this.id = id;
		this.selectionValue = value;

		label = new Label(description, skin);
		selectBox = new SelectBox<String>(skin);
		selectBox.setItems((String[]) selectionList);
		selectBox.setSelectedIndex(value);
		actor = selectBox;
		
		selectBox.addListener(this);
	}
	
	/**
	 * Creates a new Setting with a CheckBox
	 * @param manager
	 *            the parent SettingManager
	 * @param id
	 *            the id of the Setting
	 * @param description
	 *            the description of the Setting
	 * @param value
	 *            the default value of the Setting
	 * @param skin
	 *            the Skin of the UI        
	 */
	public Setting(final SettingManager manager, String id, String description, boolean value, final Skin skin) {
		this.type = SettingType.CHECKBOX;
		this.manager = manager;
		this.id = id;
		this.checkBoxValue = value;
		
		label = new Label(description, skin);
		checkBox = new CheckBox("", skin);
		checkBox.setChecked(value);
		checkBox.getCells().get(0).size(0.05f * Gdx.graphics.getWidth(), 0.05f * Gdx.graphics.getHeight());
		actor = checkBox;
		
		checkBox.addListener(this);
	}
	
	/**
	 * Creates a new Setting with a Slider
	 * @param manager
	 *            the parent SettingManager
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
	 * @param skin
	 *            the Skin of the UI        
	 */
	public Setting(final SettingManager manager, String id, String description, float value, float min, float max, float stepSize, final Skin skin) {
		this.type = SettingType.SLIDER;
		this.manager = manager;
		this.id = id;
		this.sliderValue = value;
		
		label = new Label(description, skin);
		slider = new Slider(min, max, stepSize, false, skin);

		actor = slider;
		
		slider.addListener(this);
	}
	
	/**
	 * Getter for the SettingType.
	 */
	public SettingType getType() {
		return type;
	}
	
	/**
	 * Getter for the Label.
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * Getter for the Actor.
	 */
	public Actor getActor() {
		return actor;
	}
	
	/**
	 * Getter for the saved Text.
	 */
	public String getText() {
		return textValue;
	}
	
	/**
	 * Getter for the saved Selection.
	 */
	public int getSelection() {
		return selectionValue;
	}
	
	/**
	 * Getter for the current CheckBox status.
	 */
	public boolean getCheckBox() {
		return checkBoxValue;
	}
	
	/**
	 * Getter for the current Slider position;
	 */
	public float getSlider() {
		return sliderValue;
	}
	
	/**
	 * Saves the setting in the preference-file.
	 */
	public void saveSetting() {
		if(type == SettingType.TEXT) {
			manager.getPreferences().putString(id, textValue);
		} else if(type == SettingType.SELECTION) {
			manager.getPreferences().putInteger(id, selectionValue);
		} else if(type == SettingType.CHECKBOX) {
			manager.getPreferences().putBoolean(id, checkBoxValue);
		} else if(type == SettingType.SLIDER) {
			manager.getPreferences().putFloat(id, sliderValue);
		}
	}

	/**
	 * Stores the current value of the widget in the variable.
	 */
	@Override
	public void changed(ChangeEvent event, Actor actor) {
		if(type == SettingType.TEXT) {
			textValue = textField.getText();
		} else if(type == SettingType.SELECTION) {
			selectionValue = selectBox.getSelectedIndex();
		} else if(type == SettingType.CHECKBOX) {
			checkBoxValue = checkBox.isChecked();
		} else if(type == SettingType.SLIDER) {
			sliderValue = slider.getValue();
		}
	}
}
