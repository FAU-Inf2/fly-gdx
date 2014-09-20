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
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Overlay to display the remaining time.
 * 
 * @author Tobias Zangl
 */
public class TimeLeftOverlay implements IntegerTimeListener {
    
    private final Label timeCounter;
    
    public TimeLeftOverlay(final Stage stage, final float timeLeft) {
        
        final Skin skin = SkinManager.getInstance().getSkin();
        // offers the visible background, contains icon and time
        final Table innerTable = new Table();
        // necessary for adjusting the innerTable on the screen
        final Table outerTable = new Table();
        
        final NinePatchDrawable backgroundOfInnerTable = new NinePatchDrawable(skin.get("button-up", NinePatch.class));
        innerTable.setBackground(backgroundOfInnerTable);
        
        final TextureRegion textureRegionOfWatchIcon = new TextureRegion(skin.getRegion("watch"));
        final Image watchIcon = new Image(textureRegionOfWatchIcon);
        
        // float value has to be rounded, because size of float converted to
        // string is bigger than size of integer converted to string
        timeCounter = new Label(String.valueOf(Math.round(timeLeft)), skin);
        
        // add icon with some padding to the text right of it
        innerTable.add(watchIcon).pad(0, 0, 0, 40);
        innerTable.add(timeCounter);
        
        outerTable.setFillParent(true);
        outerTable.add(innerTable).pad(UI.Window.BORDER_SPACE).height(UI.Buttons.MAIN_BUTTON_HEIGHT).expand().top().left();
        stage.addActor(outerTable);
    }
    
    @Override
    public boolean integerTimeChanged(int timeLeft, int timeSinceStart) {
        timeCounter.setText(String.valueOf(timeLeft));
        return false;
    }
    
}