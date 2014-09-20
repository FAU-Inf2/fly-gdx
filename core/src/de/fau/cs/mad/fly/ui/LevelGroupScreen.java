package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

/**
 * Offers a selection of level groups.
 * 
 * @author Lukas Hahmann
 */
public class LevelGroupScreen extends BasicScreen {
    
    private static LevelGroupScreen instance;
    
    /**
     * This class is a singleton. When called the instance is created (lazy
     * loading)
     * 
     */
    public static LevelGroupScreen getInstance() {
        if(instance == null) {
            instance = new LevelGroupScreen();
        }
        return instance;
    }
    
    /**
     * Shows a list of all available level groups.
     */
    public void generateDynamicContent() {
        // calculate width and height of buttons and the space in between
        List<LevelGroup> levelGroups = LevelGroupManager.getInstance().getLevelGroups();
        
        // table that contains all buttons
        Skin skin = SkinManager.getInstance().getSkin();
        Table scrollableTable = new Table(skin);
        ScrollPane levelScrollPane = new ScrollPane(scrollableTable, skin);
        levelScrollPane.setScrollingDisabled(true, false);
        levelScrollPane.setFadeScrollBars(false);
        levelScrollPane.setFillParent(true);
        
        // create a button for each level group
        int maxRows = (int) Math.ceil((double) levelGroups.size() / (double) UI.Buttons.BUTTONS_IN_A_ROW);
        
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(levelGroups.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
            // fill a row with buttons
            for (int column = 0; column < maxColumns; column++) {
                final LevelGroup group = levelGroups.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
                final TextButton button = new TextButton(group.name, skin);
                if (!Fly.DEBUG_MODE && group.id > PlayerProfileManager.getInstance().getCurrentPlayerProfile().getPassedLevelgroupID()) {
                    button.setDisabled(true);
                }
                
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        LevelChooserScreen levelChooserScreen = LevelChooserScreen.getInstance();
                        levelChooserScreen.setGroup(group);
                        levelChooserScreen.set();
                    }
                });
                scrollableTable.add(button).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).expand();
            }
            scrollableTable.row().expand();
        }
        stage.addActor(levelScrollPane);
    }
    
    @Override
    public void show() {
        super.show();
        generateDynamicContent();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        instance = null;
    }
}
