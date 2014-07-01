package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Level;


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
		table.setFillParent(true);
		table.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
		stage.addActor(table);
		
		TextButtonStyle textButtonStyle = skin.get(UI.Buttons.STYLE, TextButtonStyle.class);
		final Button continueButton = new TextButton(I18n.t("continue"), textButtonStyle);
		final Button chooseLevelButton = new TextButton(I18n.t("choose.level"), textButtonStyle);
		final Button settingsButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
		final Button statsButton = new TextButton("Statistics", textButtonStyle);

		table.row();
		table.add();
		table.add();
		table.add(settingsButton).right();
		table.row().expand();
		table.add();
		table.add(continueButton).fill();
		table.add();
		table.row().expand();
		table.add();
		table.add(chooseLevelButton).fill().pad(40, 0, 40, 0);
		table.add();
		table.row().expand();
		table.add();
		table.add(statsButton).fill().setWidgetHeight(0.2f);;
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
				Level.Head levelHead = PlayerManager.getInstance().getCurrentPlayer().getLastLevel();
				if(levelHead == null) {
					Gdx.app.log("Loader.continueLevel", "No last level set for player. Defaulting to first level..");
					levelHead = LevelManager.getInstance().getLevelList().get(0);
				}
				((Fly) Gdx.app.getApplicationListener()).loadLevel(levelHead);
			}
		});
		
		settingsButton.addListener(new ClickListener() {
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
