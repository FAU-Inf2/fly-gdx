package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.game.IntegerTimeListener;

/**
 * Optional Feature to display the used time.
 * 
 * @author Tobias Zangl
 */
public class TimeLeftOverlay implements IntegerTimeListener {

	private final Label timeCounter;

	public TimeLeftOverlay(final Skin skin, final Stage stage) {	    
        
        final Table innerTable = new Table();
        final NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        innerTable.setBackground(background);
        
        final TextureRegion textureRegion = new TextureRegion(skin.getRegion("watch"));
        final Image watchIcon = new Image(textureRegion);
        
        timeCounter = new Label("", skin);
        
        innerTable.row().expand();
        innerTable.add(watchIcon);
        innerTable.add(timeCounter).pad(0, 50, 0, 0);
        innerTable.row().expand();
        stage.addActor(innerTable);
        innerTable.setBounds(50, 2050, 600, 300);
	}

	@Override
	public boolean integerTimeChanged(int newTime) {
		timeCounter.setText(String.valueOf(newTime));
		return false;
	}
}