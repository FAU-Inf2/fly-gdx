package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.FlightController;
import de.fau.cs.mad.fly.game.GameController;

public class SteeringResetOverlay implements IFeatureInit, IFeatureRender, IFeatureDispose {

	private FlightController flightController;
	
	private Skin skin;
	private Stage stage;
	private Table table;
	
	private TextButton resetButton;

	public SteeringResetOverlay(final Fly game, final FlightController flightController, Stage stage) {
		this.flightController = flightController;
		this.stage = stage;
		skin = game.getSkin();
		
		table = new Table();
		table.setBounds(Gdx.graphics.getWidth() * 0.3f, -Gdx.graphics.getHeight() * 0.4f, Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.05f);
		table.pad(Gdx.graphics.getWidth() * 0.01f);
		table.setFillParent(true);
		resetButton = new TextButton("Reset Steering", skin, "default");
		
		final Table infoTable = new Table();
		final ScrollPane pane = new ScrollPane(infoTable, skin);
		infoTable.add(resetButton).pad(1f);
		pane.setFadeScrollBars(true);
		
		table.row().expand();
		table.add(pane);
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GameController gameController) {
		// TODO Auto-generated method stub
		resetButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				flightController.resetSteering();
			}
		});
		
		stage.addActor(table);
	}

}
