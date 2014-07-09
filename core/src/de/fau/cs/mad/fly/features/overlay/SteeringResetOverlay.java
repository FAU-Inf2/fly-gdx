package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.FlightController;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.ui.UI;

public class SteeringResetOverlay implements IFeatureInit, IFeatureRender, IFeatureDispose {

	private final FlightController flightController;
	
	private final Stage stage;
	private final Table table;
	
	private Button resetButton;

	public SteeringResetOverlay(final Skin skin, final FlightController flightController, final Stage stage) {
		this.flightController = flightController;
		this.stage = stage;
		table = new Table();
		table.setFillParent(true);
		resetButton = new TextButton(I18n.t("reset.Steering"), skin, UI.Buttons.STYLE);
		
		table.row().expand();
		table.add(resetButton).right().bottom().pad(UI.Window.BORDER_SPACE);
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
	public void init(final GameController gameController) {
		resetButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				flightController.resetSteering();
			}
		});
		
		stage.addActor(table);
	}

}
