package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public abstract class BasicScreenWithBackButton extends BasicScreen {
    
    private static ImageButton backButton;
    
    public BasicScreenWithBackButton(BasicScreen screenToReturn) {
        super();
        inputProcessor = new InputMultiplexer(stage, new GenericBackProcessor(screenToReturn));
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
                
            }
        });
        outerTable.add(backButton).pad(UI.Window.BORDER_SPACE).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).bottom().left().expand();
    };
    
}
