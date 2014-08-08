package de.fau.cs.mad.fly.ui.help;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
    
    /**
     * Create the content of the frame, a describing text and an arrow.
     * 
     * @param skin
     */
    public HelpFrameText(Skin skin, String text) {
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
        stage.draw();
    }
    
}
