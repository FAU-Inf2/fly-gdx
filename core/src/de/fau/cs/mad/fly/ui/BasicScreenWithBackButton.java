package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class BasicScreenWithBackButton extends BasicScreen {
    
    private static ImageButton backButton;
    private GenericBackProcessor genericBackProcessor;
    
    public BasicScreenWithBackButton(BasicScreen screenToReturn) {
        super();
        genericBackProcessor = new GenericBackProcessor(screenToReturn);
        inputProcessor = new InputMultiplexer(stage, genericBackProcessor);
        generateBackButton();
    }
    
    /**
     * Generates a back button in the lower left corner of the screen that leads
     * back to the previous screen.
     */
    protected void generateBackButton() {
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        stage.addActor(outerTable);
        
        Skin skin = SkinManager.getInstance().getSkin();
        backButton = new ImageButton(skin, "backArrow");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                genericBackProcessor.goBack();
            }
        });
        outerTable.add(backButton).pad(UI.Window.BORDER_SPACE).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).bottom().left().expand();
    }
    
}
