package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Optional Feature to display a start and a finish message to the player.
 * 
 * @author Tobias Zangl
 */
public class GameFinishedOverlay implements IFeatureInit, IFeatureRender, IFeatureFinish {
	private final Fly game;
	private GameController gameController;

	private final Skin skin;
	private final Stage stage;
	private final Table table;
	
	private TextButton continueButton;
	
	public GameFinishedOverlay(final Fly game, final Stage stage) {
		this.game = game;
		this.stage = stage;
		skin = game.getSkin();
		table = new Table();
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(final GameController gameController) {		
		this.gameController = gameController;
		
	}

	@Override
	public void finish() {
		
		
		table.pad(Gdx.graphics.getWidth() * 0.2f);
		table.setFillParent(true);
		String infoString  = "Game over!";
		if( game.getGameController().getLevel().isReachedLastGate()){
			infoString = "Congratulations! :)";
		}
		else if( game.getGameController().getLevel().getLeftTime()<=0)
		{
			infoString = "Time is up! :)";
		}
		else if( game.getGameController().getLevel().getLeftCollisionTime()<=0 )
		{
			infoString = "Ops, your plane is totally broken! :)";
		}
		final Label infoLabel = new Label(infoString, skin);
		continueButton = new TextButton("Back to Menu!", skin, "default");
		
		final Table infoTable = new Table();
		final ScrollPane pane = new ScrollPane(infoTable, skin);
		infoTable.add(infoLabel).pad(10f);
		infoTable.row();
		infoTable.add(continueButton).pad(10f);
		pane.setFadeScrollBars(true);
		
		table.row().expand();
		table.add(pane);
		continueButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setMainMenuScreen();
			}
		});
		stage.addActor(table);
	}
}
