package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

/**
 * Offers a selection of level groups.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LevelGroupScreen extends BasicScreenWithBackButton {
    
    private LevelChooserScreen levelChooserScreen;
    
    public LevelGroupScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
    }
    
    /**
     * Shows a list of all available level groups.
     */
    public void generateDynamicContent() {        
        
        contentTable.clear();
        Skin skin = SkinManager.getInstance().getSkin();
        List<LevelGroup> levelGroups = LevelGroupManager.getInstance().getLevelGroups();
        
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
                        setLevelChooserScreen(group);
                    }
                });
                contentTable.add(button).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE).expand();
            }
            contentTable.row();
        }
    }
    
    public void setLevelChooserScreen(LevelGroup levelGroup) {
        if (levelChooserScreen == null) {
            levelChooserScreen = new LevelChooserScreen(this);
        }
        levelChooserScreen.setGroup(levelGroup);
        levelChooserScreen.set();
    }
    
    @Override
    public void show() {
        super.show();
        generateDynamicContent();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        levelChooserScreen.dispose();
    }
}
