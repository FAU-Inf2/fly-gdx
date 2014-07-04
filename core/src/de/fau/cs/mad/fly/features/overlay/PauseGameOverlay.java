package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.ui.UI;

public class PauseGameOverlay implements IFeatureInit, IFeatureDispose {
	private final Fly game;
	
	private final Skin skin;
	private final Stage stage;
	private final Table table;
	
	private final TextButton pauseButton;

	public PauseGameOverlay(final Fly game, final Stage stage) {
		this.game = game;
		this.stage = stage;
		skin = game.getSkin();
		
		table = new Table();
		table.setFillParent(true);
		pauseButton = new TextButton(I18n.t("pause"), skin, UI.Buttons.STYLE);
		table.row().expand();
		table.add(pauseButton).right().pad(UI.Window.BORDER_SPACE);
		table.row().expand();
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void init(final GameController gameController) {
		pauseButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				if(game.getGameController().isRunning()) {
					game.getGameController().pauseGame();
					pauseButton.setText(I18n.t("run"));
				} else if(game.getGameController().isPaused()) {
					game.getGameController().startGame();
					pauseButton.setText(I18n.t("pause"));
				}
			}
		});
		
		stage.addActor(table);
	}

}
