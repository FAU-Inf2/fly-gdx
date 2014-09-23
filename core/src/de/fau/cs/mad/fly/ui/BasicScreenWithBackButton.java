package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class BasicScreenWithBackButton extends BasicScreen {
    
    public BasicScreenWithBackButton(BasicScreen screenToReturn) {
        super();
        inputProcessor = new InputMultiplexer(stage, new GenericBackProcessor(screenToReturn));
    }
    
    /** You have to overwrite this method to create your custom content */
    protected void generateContent() {
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        stage.addActor(outerTable);
        
        Skin skin = SkinManager.getInstance().getSkin();
        
        ImageButton backButton = new ImageButton(skin, "settings");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                
            }
        });
        outerTable.add(backButton).pad(UI.Window.BORDER_SPACE).bottom().left().expand();
    };
    
}
