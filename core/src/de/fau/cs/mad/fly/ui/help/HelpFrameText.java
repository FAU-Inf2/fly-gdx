package de.fau.cs.mad.fly.ui.help;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.ui.UI;

/**
 * {@link HelpFrame} that shows a simple text centered in the top mid.
 * 
 * @author Lukas Hahmann
 * 
 */
public class HelpFrameText extends HelpFrame {
    
    /** skin that is used for the label and the background */
    private Skin skin;
    /** local copy of the label text */
    private String text;
    /** flat that indicates whether this object is initialized */
    private boolean initialized = false;
    
    /**
     * Init the members.
     * <p>
     * The actual initialization is done in {@link #initialize}. This is used to
     * reduce the initialization time of an object {@link HelpFrameText}.
     * 
     * @param skin
     */
    public HelpFrameText(Skin skin, String text) {
        this.skin = skin;
        this.text = text;
    }
    
    /**
     * Setup batch and stage, create label, table with background and the arrow.
     * This is done to reduce the initialization time of an object
     * {@link HelpFrameText}.
     */
    private void initialize() {
        super.setupBatchAndStage();
        
        LabelStyle labelStyle = skin.get("black", LabelStyle.class);
        Label label = new Label(I18n.t(text), labelStyle);
        
        // top aligned, if something other is needed, an appropriate parameter
        // is necessary
        float tableX = 0.5f * (viewport.getWorldWidth() - label.getWidth());
        float tableY = viewport.getWorldHeight() - UI.Window.BORDER_SPACE - label.getHeight() - 2 * PADDING_OF_LABEL;
        float tableWidth = label.getWidth() + 2 * PADDING_OF_LABEL;
        float tableHeight = label.getHeight() + 2 * PADDING_OF_LABEL;
        Table helpingTextTable = new Table();
        final NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        helpingTextTable.setBackground(background);
        helpingTextTable.row().expand();
        helpingTextTable.add(label);
        helpingTextTable.row().expand();
        helpingTextTable.setBounds(tableX, tableY, tableWidth, tableHeight);
        stage.addActor(helpingTextTable);
        
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    @Override
    public void render() {
        if (!initialized) {
            initialize();
            initialized = true;
        }
        stage.draw();
    }
    
}
