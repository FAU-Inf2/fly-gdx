package de.fau.cs.mad.fly.ui.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.ui.BackProcessor;
import de.fau.cs.mad.fly.ui.GenericBackProcessor;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

public abstract class BasicScreenWithBackButton extends BasicScreen {
    
    private Button backButton;
    protected BackProcessor backProcessor;
    protected Table contentTable;
    
    public BasicScreenWithBackButton(BasicScreen screenToReturn) {
        super();
        backProcessor = new GenericBackProcessor(screenToReturn);
        inputProcessor = new InputMultiplexer(stage, backProcessor);
    }
    
    /**
     * Overwrites the back processor.
     * 
     * @param backProcessor
     */
    public void setBackProcessor(BackProcessor backProcessor) {
        for(InputProcessor processor : inputProcessor.getProcessors()) {
            if (processor instanceof BackProcessor) {
                inputProcessor.removeProcessor(processor);
            }
        }
        inputProcessor.addProcessor(backProcessor);
        this.backProcessor = backProcessor;
    }
    
    /**
     * Generates a back button in the lower left corner of the screen that leads
     * back to the previous screen.
     */
    protected void generateBackButton() {
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        stage.addActor(outerTable);
        contentTable = new Table();
        contentTable.setFillParent(true);
        stage.addActor(contentTable);
        outerTable.row();
        
        Skin skin = SkinManager.getInstance().getSkin();
        backButton = new ImageButton(skin, "backArrow");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // trigger the routine to go back to previous screen
                backProcessor.keyDown(Keys.ESCAPE);
            }
        });
        outerTable.add(backButton).pad(UI.Window.BORDER_SPACE).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).bottom().left().expand();
    }
    
}
