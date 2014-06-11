package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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

	private Table table;

	
	/**
	 * Adds the main menu to the main menu screen.
	 * <p>
	 * Includes buttons for Start, Options, Help, Exit.
	 */
	protected void generateContent() {
		table = new Table();
		//table.debug();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);

		final TextButton continueButton = new TextButton("Continue", skin, "default");
		final TextButton chooseLevelButton = new TextButton("Choose Level", skin, "default");
		final TextButton optionButton = new TextButton("Settings", skin, "default");

		table.row().expand();
		table.add(continueButton).fill().pad(10f).colspan(2);
		table.row().expand();
		table.add(chooseLevelButton).fill().pad(10f).uniform();
		table.add(optionButton).fill().pad(10f).uniform();
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
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
		//Table.drawDebug(stage);
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
