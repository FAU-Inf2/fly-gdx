package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.game.ScoreChangeListener;

/**
 * Optional Feature to display the current score.
 * 
 * @author Tobias Zangl
 */
public class ScoreOverlay implements ScoreChangeListener {

	private final Label scoreCounter;

	public ScoreOverlay(final Skin skin, final Stage stage) {	    
        
        final Table innerTable = new Table();
        final NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        innerTable.setBackground(background);
        
        final TextureRegion textureRegion = new TextureRegion(skin.getRegion("star"));
        final Image scoreIcon = new Image(textureRegion);
        
        scoreCounter = new Label("", skin);
        
        innerTable.row().expand();
        innerTable.add(scoreIcon);
        innerTable.add(scoreCounter).pad(0, 50, 0, 0);
        innerTable.row().expand();
        stage.addActor(innerTable);
        innerTable.setBounds(3150, 2050, 800, 300);
	}

	@Override
	public void scoreChanged(int newScore) {
		scoreCounter.setText(String.valueOf(newScore));
	}

}