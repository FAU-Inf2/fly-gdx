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
		table.pad(UI.Window.SINGLE_SPACE_HEIGHT, UI.Window.SINGLE_SPACE_WIDTH, UI.Window.SINGLE_SPACE_HEIGHT, UI.Window.SINGLE_SPACE_WIDTH).setFillParent(true);
		stage.addActor(table);

		final TextButton continueButton = new TextButton("Continue", skin, "default");
		final TextButton chooseLevelButton = new TextButton("Choose Level", skin, "default");
		final TextButton optionButton = new TextButton("Settings", skin, "default");

		table.row().expand();
		table.add(continueButton).fill().pad(UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH, UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH).colspan(2);
		table.row().expand();
		table.add(chooseLevelButton).fill().pad(UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH, UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH).uniform();
		table.add(optionButton).fill().pad(UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH, UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH).uniform();
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
				((Fly) Gdx.app.getApplicationListener()).continueLevel();
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
