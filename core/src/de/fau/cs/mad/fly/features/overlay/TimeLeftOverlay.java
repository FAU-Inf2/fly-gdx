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
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display the used time.
 * 
 * @author Tobias Zangl
 */
public class TimeLeftOverlay implements IntegerTimeListener {

	private Label timeCounter;

	public TimeLeftOverlay(final Skin skin, final Stage stage) {	    
	    Table outerTable = new Table();
        outerTable.setFillParent(true);
        
        final Table innerTable = new Table();
        NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        innerTable.setBackground(background);
        
        TextureRegion textureRegion = new TextureRegion(skin.getRegion("watch"));
        Image watchIcon = new Image(textureRegion);
        
        timeCounter = new Label("", skin);
        innerTable.add(watchIcon);
        innerTable.add(timeCounter).pad(0, 50, 0, 0);
        innerTable.row().expand();
        
        outerTable.row().expand();
        outerTable.add(innerTable).pad(UI.Window.BORDER_SPACE).left().top();
        outerTable.row().expand();
        stage.addActor(outerTable);
	}

	@Override
	public void integerTimeChanged(int newTime) {
		timeCounter.setText(String.valueOf(newTime));
	}
}