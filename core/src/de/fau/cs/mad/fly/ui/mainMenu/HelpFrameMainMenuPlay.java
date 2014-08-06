package de.fau.cs.mad.fly.ui.mainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.ui.HelpFrame;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Frame to describe what to do with the play button.
 * 
 * @author Lukas Hahmann
 * 
 */
public class HelpFrameMainMenuPlay extends HelpFrame {
    
    private final TextureRegion arrow;
    private final Label helpToPlay;
    private float arrowXPos;
    private float arrowYPos;
    private float arrowRotation;
    private final Actor actorToBeDescribed;
    private final int DISTANCE_BETWEEN_ARROW_AND_LABEL = 20;
    private final Table table;
    private final int PADDING_OF_LABEL = 70;
    
    /**
     * Create the content of the frame, a describing text and an arrow.
     * 
     * @param skin
     */
    public HelpFrameMainMenuPlay(Skin skin, Actor actorToBeDescribed) {
        super.setupBatchAndStage();
        this.actorToBeDescribed = actorToBeDescribed;
        
        LabelStyle labelStyle = skin.get("black", LabelStyle.class);
        helpToPlay = new Label(I18n.t("helpPlay"), labelStyle);
        table = new Table();
        final NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        table.setBackground(background);
        table.row().expand();
        table.add(helpToPlay);
        table.row().expand();
        this.arrow = skin.getRegion("helpArrowDown");
        
        stage.addActor(table);
    }
    
    @Override
    public void generateContent() {
        float labelXPos;
        float labelYPos;
        
        // check if above is possible
        float minSpaceAbove = helpToPlay.getHeight() + UI.Window.BORDER_SPACE + arrow.getRegionHeight();
        // constant for above and below
        arrowXPos = 1900 / scalingFactor;
        labelXPos = actorToBeDescribed.getX() + 0.5f * actorToBeDescribed.getWidth() - 0.5f * helpToPlay.getWidth();
        
        if (viewport.getWorldHeight() - (actorToBeDescribed.getY() + actorToBeDescribed.getHeight()) > minSpaceAbove) {
            labelYPos = viewport.getWorldHeight() - actorToBeDescribed.getY();
            arrowYPos = (helpToPlay.getCenterY() - helpToPlay.getHeight()) / scalingFactor;
            arrowRotation = 0;
        }
        // put help text below
        else {
            arrowYPos = viewport.getWorldHeight() - actorToBeDescribed.getY() - arrow.getRegionHeight();
            labelYPos = arrowYPos;
            arrowRotation = 180;
        }
        table.setBounds(labelXPos - PADDING_OF_LABEL, labelYPos + PADDING_OF_LABEL, helpToPlay.getWidth() + PADDING_OF_LABEL, helpToPlay.getHeight() + PADDING_OF_LABEL);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    @Override
    public void render() {
        stage.draw();
        batch.begin();
        batch.draw(arrow, arrowXPos, arrowYPos, 0, 0, arrow.getRegionWidth() / scalingFactor, arrow.getRegionHeight() / scalingFactor, 1f, 1f, arrowRotation);
        batch.end();
    }
    
}
