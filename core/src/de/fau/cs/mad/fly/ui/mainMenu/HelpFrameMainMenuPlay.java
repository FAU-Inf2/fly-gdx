package de.fau.cs.mad.fly.ui.mainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.ui.HelpFrame;

/**
 * Frame to describe what to do with the play button.
 * 
 * @author Lukas Hahmann
 * 
 */
public class HelpFrameMainMenuPlay extends HelpFrame {
    
    private final TextureRegion arrow;
    private final Label helpToPlay;
    
    /**
     * Create the content of the frame, a describing text and an arrow.
     * @param skin
     */
    public HelpFrameMainMenuPlay(Skin skin) {
        super.setupBatchAndStage();
        
        LabelStyle labelStyle = skin.get("black", LabelStyle.class);
        helpToPlay = new Label(I18n.t("helpPlay"), labelStyle);
        
        this.arrow = skin.getRegion("helpArrowDown");
        
        stage.addActor(helpToPlay);
        helpToPlay.setPosition(650, viewport.getWorldHeight()-500);
        
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    @Override
    public void render() {
        batch.begin();
        batch.draw(arrow, 1900 / scalingFactor, (helpToPlay.getY()-100) / scalingFactor, 0, 0, arrow.getRegionWidth() / scalingFactor, arrow.getRegionHeight() / scalingFactor, 1f, 1f, 0);
        batch.end();
        stage.draw();
    }
    
}
