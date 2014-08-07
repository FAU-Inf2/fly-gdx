package de.fau.cs.mad.fly.ui.mainMenu;

import com.badlogic.gdx.Gdx;
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
public class HelpFrameMainMenuWelcome extends HelpFrame {
    
    /**
     * Create the content of the frame, a describing text and an arrow.
     * @param skin
     */
    public HelpFrameMainMenuWelcome(Skin skin, String text) {
        super.setupBatchAndStage();
        
        LabelStyle labelStyle = skin.get("black", LabelStyle.class);
        Label helpToPlay = new Label(I18n.t(text), labelStyle);
        
        stage.addActor(helpToPlay);
        helpToPlay.setPosition(650, viewport.getWorldHeight()-500);
        
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    @Override
    public void render() {
        stage.draw();
    }
    
}
