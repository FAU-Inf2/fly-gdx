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
 * Frame to describe a help text with an arrow pointing to the Actor.
 * <p>
 * 
 * To create this Frame, you have to pass a skin, which is used for the
 * background of the message. The message itself is fetched from the I18N by the
 * passed string. Furthermore you have to define an actor which is described.
 * The position of the frame is used to find an appropriate position for the
 * message and the arrow. It is searched by the following strategy:
 * <p>
 * - if there is enough space centered and above the actor, place message and arrow centered and above the actor
 * - if actor is visible but message or arrow can not be centered because it would leave the visible screen, it is placed above and moved either to the right or left
 * - if there is enough place centered and below the actor, place message and arrow centered below the actor  
 * - if actor is visible but message or arrow can not be centered because it would leave the visible screen, it is placed below moved either to the right or left
 * 
 * @author Lukas Hahmann
 * 
 */
public class HelpFrameMainMenuPlay extends HelpFrame {
    
    private final TextureRegion arrow;
    private final Label helpingText;
    /** X-Position of the arrow, has to be scaled, because used in render method */
    private float arrowXPos;
    /** Y-Position of the arrow, has to be scaled, because used in render method */
    private float arrowYPos;
    /** Rotation of the arrow in degree. With 0 arrow directs down */
    private float arrowRotation;
    /** Actor that is described by the helping text */
    private final Actor actorToBeDescribed;
    private final Table helpingTextTable;
    private final int PADDING_OF_LABEL = 70;
    
    /**
     * Create the content of the frame, a describing text and an arrow.
     * 
     * @param skin
     */
    public HelpFrameMainMenuPlay(Skin skin, String labelText, Actor actorToBeDescribed) {
        super.setupBatchAndStage();
        this.actorToBeDescribed = actorToBeDescribed;
        
        LabelStyle labelStyle = skin.get("black", LabelStyle.class);
        helpingText = new Label(I18n.t(labelText), labelStyle);
        helpingTextTable = new Table();
        final NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        helpingTextTable.setBackground(background);
        helpingTextTable.row().expand();
        helpingTextTable.add(helpingText);
        helpingTextTable.row().expand();
        this.arrow = skin.getRegion("helpArrowDown");
        
        stage.addActor(helpingTextTable);
    }
    
    @Override
    public void generateContent() {
        float labelXPos;
        float labelYPos;
        
        // check if above is possible
        float minSpaceAbove = helpingText.getHeight() + UI.Window.BORDER_SPACE + arrow.getRegionHeight();
        // constant for above and below
        arrowXPos = 1900 / scalingFactor;
        labelXPos = actorToBeDescribed.getX() + 0.5f * actorToBeDescribed.getWidth() - 0.5f * helpingText.getWidth();
        
        if (viewport.getWorldHeight() - (actorToBeDescribed.getY() + actorToBeDescribed.getHeight() + UI.Window.BORDER_SPACE + arrow.getRegionHeight()) > minSpaceAbove) {
            arrowYPos = (actorToBeDescribed.getY() + actorToBeDescribed.getHeight()) / scalingFactor;
            labelYPos = actorToBeDescribed.getY() + actorToBeDescribed.getHeight() + arrow.getRegionHeight() - PADDING_OF_LABEL;
            arrowRotation = 0;
        }
        // put help text below
        else {
            arrowYPos = (actorToBeDescribed.getY()) / scalingFactor;
            labelYPos = actorToBeDescribed.getY() - arrow.getRegionHeight() - helpingText.getHeight() - 2 * PADDING_OF_LABEL;
            arrowRotation = 180;
        }
        helpingTextTable.setBounds(labelXPos - PADDING_OF_LABEL, labelYPos + PADDING_OF_LABEL, helpingText.getWidth() + PADDING_OF_LABEL, helpingText.getHeight() + PADDING_OF_LABEL);
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
