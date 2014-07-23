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
public class HelpFrameMainMenuSelectLevel extends HelpFrame {
    
    private final TextureRegion arrow;
    
    /**
     * Create the content of the frame, a describing text and an arrow.
     * @param skin
     */
    public HelpFrameMainMenuSelectLevel(Skin skin) {
        super.setupBatchAndStage();
        
        LabelStyle labelStyle = skin.get("black", LabelStyle.class);
        Label helpToPlay = new Label(I18n.t("helpSelectLevel"), labelStyle);
        
        this.arrow = skin.getRegion("helpArrowDown");
        
        stage.addActor(helpToPlay);
        helpToPlay.setPosition(100, 900);
        
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    @Override
    public void render() {
        batch.begin();
        batch.draw(arrow, 1900 / scalingFactor, 1050 / scalingFactor, 0, 0, arrow.getRegionWidth() / scalingFactor, arrow.getRegionHeight() / scalingFactor, 1f, 1f, 0);
        batch.end();
        stage.draw();
    }
    
}
