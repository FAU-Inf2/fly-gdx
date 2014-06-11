package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.res.Assets;


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
		table.pad(UI.window.spaceHeight, UI.window.spaceWidth, UI.window.spaceHeight, UI.window.spaceWidth).setFillParent(true);
		stage.addActor(table);

		final TextButton continueButton = new TextButton("Continue", skin, "default");
		final TextButton chooseLevelButton = new TextButton("Choose Level", skin, "default");
		final TextButton optionButton = new TextButton("Settings", skin, "default");

		table.row().expand();
		table.add(continueButton).fill().pad(UI.smallButtons.spaceHeight, UI.smallButtons.spaceWidth, UI.smallButtons.spaceHeight, UI.smallButtons.spaceWidth).colspan(2);
		table.row().expand();
		table.add(chooseLevelButton).fill().pad(UI.smallButtons.spaceHeight, UI.smallButtons.spaceWidth, UI.smallButtons.spaceHeight, UI.smallButtons.spaceWidth).uniform();
		table.add(optionButton).fill().pad(UI.smallButtons.spaceHeight, UI.smallButtons.spaceWidth, UI.smallButtons.spaceHeight, UI.smallButtons.spaceWidth).uniform();
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
				((Fly) Gdx.app.getApplicationListener()).loadLevel();
			}
		});
		
		optionButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setSettingScreen();
			}
		});
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		Assets.dispose();
		((Fly) Gdx.app.getApplicationListener()).dispose();
		Gdx.app.exit();
	}
}
