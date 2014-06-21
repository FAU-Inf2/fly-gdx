package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;


/**
 * Displays the main menu with Start, Options, Help and Exit buttons.
 * 
 * @author Tobias Zangl
 */
public class MainMenuScreen extends BasicScreen {

	/**
	 * Adds the main menu to the main menu screen.
	 * <p>
	 * Includes buttons for Start, Options, Help, Exit.
	 */
	protected void generateContent() {
		Table table = new Table();
		table.pad(UI.Window.SINGLE_SPACE_HEIGHT, UI.Window.SINGLE_SPACE_WIDTH, UI.Window.SINGLE_SPACE_HEIGHT, UI.Window.SINGLE_SPACE_WIDTH).setFillParent(true);
		stage.addActor(table);
		
		TextButtonStyle textButtonStyle = skin.get(UI.Buttons.STYLE, TextButtonStyle.class);
		final TextButton continueButton = new TextButton(I18n.t("continue"), textButtonStyle);
		final TextButton chooseLevelButton = new TextButton(I18n.t("choose.level"), textButtonStyle);
		final TextButton optionButton = new TextButton(I18n.t("settings"), textButtonStyle);
		final TextButton statsButton = new TextButton("Statistics", textButtonStyle);

		
		table.row().expand();
		table.add();
		table.add(optionButton).fill().uniform();
		table.row().expand();
		table.add(continueButton).fill().pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).colspan(3);
		table.row().expand();
		table.add(chooseLevelButton).fill().pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).uniform();
		
		table.add(statsButton).fill().pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).uniform();
		table.row().expand();
		
		
		chooseLevelButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setLevelChoosingScreen();
			}
		});
		
		continueButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).getLoader().continueLevel();
			}
		});
		
		optionButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setSettingScreen();
			}
		});
		
		statsButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setStatisticsScreen();
			}
		});
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
