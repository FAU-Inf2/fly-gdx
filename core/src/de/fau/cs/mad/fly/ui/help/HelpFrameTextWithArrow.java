package de.fau.cs.mad.fly.ui.help;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.I18n;
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
 * - if there is enough space centered and above the actor, place message and
 * arrow centered and above the actor
 * <p>
 * - if actor is visible but message or arrow can not be centered because it
 * would leave the visible screen, it is placed above and moved either to the
 * right or left
 * <p>
 * - if there is enough place centered and below the actor, place message and
 * arrow centered below the actor
 * <p>
 * - if actor is visible but message or arrow can not be centered because it
 * would leave the visible screen, it is placed below moved either to the right
 * or left (same strategy as for above)
 * <p>
 * - if actor is right or left outside the visible screen, its vertical position
 * is used to place label and actor in a row, so tat the arrow points in the
 * direction of the actor.
 * 
 * 
 * @author Lukas Hahmann
 * 
 */
public class HelpFrameTextWithArrow extends OverlayFrame {
    
    protected final int PADDING_OF_LABEL = 70;
    
    /** Space between arrow and actor / message box */
    private final static float PADDING = 10f;
    
    /**
     * arrow that points to the actor that is described by this
     * {@link OverlayFrame}
     */
    private TextureRegion arrow;
    
    /**
     * X-Position of the arrow. It has to be scaled, because it is used in
     * {@link #render()}
     */
    private float arrowXPos;
    
    /**
     * Y-Position of the arrow. It has to be scaled, because it is used in
     * {@link #render()}
     */
    private float arrowYPos;
    
    /**
     * Rotation of the arrow in degree. 0° directs down, increases counter
     * clockwise.
     */
    private float arrowRotation;
    
    /**
     * Width of the arrow. It has to be scaled, because it is used in
     * {@link #render()}
     */
    private float arrowWidth;
    
    /**
     * Height of the arrow. It has to be scaled, because it is used in
     * {@link #render()}
     */
    private float arrowHeight;
    
    /**
     * X-Offset of the arrow that is used for rotation. Hence it is the midpoint
     * of the arrow. It has to be scaled, because it is used in
     * {@link #render()}
     */
    private float arrowOffsetX;
    
    /**
     * Y-Offset of the arrow that is used for rotation. Hence it is the midpoint
     * of the arrow. It has to be scaled, because it is used in
     * {@link #render()}
     */
    private float arrowOffsetY;
    
    /**
     * Vector2 that contains the position of the actor. To be exact the bottom
     * left edge of the actor.
     */
    private Vector2 actorPos;
    
    /** Actor that is described by the helping text */
    private final Actor actor;
    
    /**
     * Table that contains the {@link #label}. It is used because to put a
     * background behind the label.
     */
    private Table helpingTextTable;
    
    /** Label that describes the actor. */
    private Label label;
    
    /** Skin that is used for the arrow, the text and the background */
    private final Skin skin;
    
    /** Local copy of the label text */
    private final String labelText;
    
    /**
     * Initialize only the necessary members.
     * <p>
     * The actual initialization is done in {@link #initialize()} to reduce the
     * loading time when creating a
     * {@link #HelpFrameTextWithArrow(Skin, String, Actor)}.
     * 
     * @param skin
     *            is used to get the label style and the background of the label
     * @param labelText
     *            String identifier to get the describing Text out of the I18N
     * @param actorToBeDescribed
     *            actor that is described by this help frame
     */
    public HelpFrameTextWithArrow(Skin skin, String labelText, Actor actorToBeDescribed) {
        this.skin = skin;
        this.labelText = labelText;
        this.actor = actorToBeDescribed;
    }
    
    /**
     * Setup batch and stage, create label and table with background, update the
     * viewport.
     */
    private void initialize() {
        super.setupBatchAndStage();
        
        LabelStyle labelStyle = skin.get("black", LabelStyle.class);
        label = new Label(I18n.t(labelText), labelStyle);
        label.setAlignment(Align.center);
        helpingTextTable = new Table();
        final NinePatchDrawable background = new NinePatchDrawable(skin.get("dialog-background", NinePatch.class));
        helpingTextTable.setBackground(background);
        helpingTextTable.row().expand();
        helpingTextTable.add(label);
        helpingTextTable.row().expand();
        this.arrow = skin.getRegion("helpArrowDown");
        actorPos = new Vector2();
        
        stage.addActor(helpingTextTable);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    @Override
    /**
     * Finds the appropriate place for the Label and the arrow and places them correctly. <p>
     * For the strategy to find the correct position look in the description of this class. <p>
     * The coordinate system starts on bottom left corner of the display with 0x0. The label position can be defined unscaled because the stage scales it afterwards. In contrast to that the position of the arrow has to be scaled because the coordinates calculated here are used in the render method.
     */
    public void generateContent() {
        initialize();
        float labelXPos;
        float labelYPos;
        
        arrowWidth = arrow.getRegionWidth() / scalingFactor;
        arrowHeight = arrow.getRegionHeight() / scalingFactor;
        arrowOffsetX = arrowWidth / 2;
        arrowOffsetY = arrowHeight / 2;
        
        // make sure that the position of the actor is only read once, because
        // currently we only have static positions and read the position out a
        // second time leads to different coordinates. It is not yet clear why
        if (actorPos.x == 0 && actorPos.y == 0) {
            if (actor.getParent() == null) {
                // if actor has no parent, the localToStageCoordinates-function
                // returns 0x0 in every case
                actorPos.x = actor.getX();
                actorPos.y = actor.getY();
            } else {
                actor.localToStageCoordinates(actorPos);
            }
        }
        
        // Check if actor is horizontally visible.
        // Otherwise place message and arrow horizontally on the same line as
        // the actor
        if (actorPos.x > 0 && actorPos.x + actor.getWidth() < viewport.getWorldWidth()) {
            
            // calculate horizontal position for arrow
            arrowXPos = actorPos.x + 0.5f * actor.getWidth() - 0.5f * arrow.getRegionWidth();
            float leftBorder = UI.Window.BORDER_SPACE + PADDING_OF_LABEL;
            float rightBorder = viewport.getWorldWidth() - leftBorder;
            if (arrowXPos < leftBorder) {
                arrowXPos = leftBorder;
            } else if (arrowXPos + arrow.getRegionWidth() > rightBorder) {
                arrowXPos = rightBorder - arrow.getRegionWidth();
            }
            arrowXPos /= scalingFactor;
            
            // calculate horizontal position for label
            labelXPos = actorPos.x + 0.5f * actor.getWidth() - 0.5f * label.getWidth();
            if (labelXPos < leftBorder) {
                labelXPos = leftBorder;
            } else if (labelXPos + label.getWidth() > rightBorder) {
                labelXPos = rightBorder - label.getWidth();
            }
            
            // check if above is possible
            float minSpaceAbove = label.getHeight() + UI.Window.BORDER_SPACE + arrow.getRegionHeight() + 2 * PADDING;
            float topEdgeOfActor = actorPos.y + actor.getHeight();
            if (viewport.getWorldHeight() - (topEdgeOfActor + UI.Window.BORDER_SPACE + arrow.getRegionHeight()) > minSpaceAbove) {
                // put label and arrow above
                arrowYPos = (topEdgeOfActor + PADDING) / scalingFactor;
                labelYPos = topEdgeOfActor + arrow.getRegionHeight() + PADDING_OF_LABEL + 2 * PADDING;
                // pointing downwards corresponds 0°
                arrowRotation = 0;
            } else {
                // put label and arrow below
                float buttomEdgeOfArrow = actorPos.y - arrow.getRegionHeight();
                arrowYPos = (buttomEdgeOfArrow - PADDING) / scalingFactor;
                labelYPos = buttomEdgeOfArrow - label.getHeight() - PADDING_OF_LABEL - 2 * PADDING;
                // pointing upwards corresponds 180°
                arrowRotation = 180;
            }
        } else {
            // Actor is either left of the visible screen or right of the
            // visible screen.
            // Calculate vertical position of the arrow
            float horizontalMidOfActor = actorPos.y + 0.5f * actor.getHeight();
            arrowYPos = horizontalMidOfActor - 0.5f * arrow.getRegionHeight();
            float bottomBorder = UI.Window.BORDER_SPACE + PADDING_OF_LABEL;
            float topBorder = viewport.getWorldHeight() - bottomBorder;
            if (arrowYPos < bottomBorder) {
                arrowXPos = bottomBorder;
            } else if (arrowYPos + arrow.getRegionHeight() > topBorder) {
                arrowYPos = topBorder - arrow.getRegionHeight();
            }
            arrowYPos /= scalingFactor;
            
            // calculate vertical position for label
            labelYPos = horizontalMidOfActor - 0.5f * label.getHeight();
            if (labelYPos < bottomBorder) {
                labelYPos = bottomBorder;
            } else if (labelYPos + label.getHeight() > topBorder) {
                labelYPos = topBorder - label.getHeight();
            }
            
            // check the side of the actor
            if (actorPos.x < 0) {
                // actor is on the left side
                float leftBorder = UI.Window.BORDER_SPACE;
                arrowXPos = leftBorder / scalingFactor;
                labelXPos = leftBorder + arrow.getRegionWidth();
                // pointing left corresponds 270°
                arrowRotation = 270;
            } else {
                // actor is on the right side
                float rightBorder = viewport.getWorldWidth() - UI.Window.BORDER_SPACE;
                arrowXPos = (rightBorder - arrow.getRegionWidth()) / scalingFactor;
                labelXPos = rightBorder - arrow.getRegionWidth() - label.getWidth() - 2 * PADDING_OF_LABEL;
                // pointing right corresponds 90°
                arrowRotation = 90;
            }
        }
        
        helpingTextTable.setBounds(labelXPos - PADDING_OF_LABEL, labelYPos - PADDING_OF_LABEL, label.getWidth() + 2 * PADDING_OF_LABEL, label.getHeight() + 2 * PADDING_OF_LABEL);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    @Override
    /**
     * Draw the stage with the {@link #label} and its background and the arrow.
     */
    public void render() {
        stage.draw();
        batch.begin();
        batch.draw(arrow, arrowXPos, arrowYPos, arrowOffsetX, arrowOffsetY, arrowWidth, arrowHeight, 1f, 1f, arrowRotation);
        batch.end();
    }
    
}
