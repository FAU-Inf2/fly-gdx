package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.scenes.scene2d.Stage;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureFinishLevel;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Optional Feature to display a start and a finish message to the player.
 * 
 * @author Tobias Zangl
 */
public class LevelInfoOverlay implements IFeatureInit, IFeatureRender, IFeatureFinishLevel {
	private final Fly game;

	private Stage stage;
	
	public LevelInfoOverlay(final Fly game, Stage stage) {
		this.game = game;
		this.stage = stage;
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GameController game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
