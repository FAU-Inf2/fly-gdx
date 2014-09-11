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
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display the current score.
 * 
 * @author Tobias Zangl
 */
public class ScoreOverlay implements ScoreChangeListener {
    
    private final Label scoreCounter;
    
    public ScoreOverlay(final Skin skin, final Stage stage) {
        // offers the visible background, contains icon and points
        final Table innerTable = new Table();
        // necessary for adjusting the innerTable on the screen
        final Table outerTable = new Table();
        
        final NinePatchDrawable backgroundOfInnerTable = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        innerTable.setBackground(backgroundOfInnerTable);
        
        final TextureRegion textureRegionOfScoreIcon = new TextureRegion(skin.getRegion("star"));
        final Image scoreIcon = new Image(textureRegionOfScoreIcon);
        
        scoreCounter = new Label(String.valueOf(0), skin);
        
        // add icon with some padding to the text right of it
        innerTable.add(scoreIcon).pad(0, 0, 0, 40);
        innerTable.add(scoreCounter);
        
        outerTable.setFillParent(true);
        outerTable.add(innerTable).pad(UI.Window.BORDER_SPACE).height(UI.Buttons.MAIN_BUTTON_HEIGHT).expand().top().right();
        stage.addActor(outerTable);
    }
    
    @Override
    public void scoreChanged(int newScore) {
        scoreCounter.setText(String.valueOf(newScore));
    }
    
}