package de.fau.cs.mad.fly.ui.screens;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Offers a selection of level groups.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LevelGroupScreen extends BasicScreenWithBackButton {
    
    private LevelChooserScreen levelChooserScreen;
    
    public LevelGroupScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
        generateBackButton();
        contentTable.clear();
        contentTable.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, 2 * UI.Window.BORDER_SPACE + UI.Buttons.IMAGE_BUTTON_HEIGHT, UI.Window.BORDER_SPACE);
        Skin skin = SkinManager.getInstance().getSkin();
        List<LevelGroup> levelGroups = LevelGroupManager.getInstance().getLevelGroups();
        
        // create a button for each level group
        int maxRows = (int) Math.ceil((float) levelGroups.size() / (float) UI.Buttons.BUTTONS_IN_A_ROW);
        
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(levelGroups.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
            // fill a row with buttons
            for (int column = 0; column < maxColumns; column++) {
                final LevelGroup group = levelGroups.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
                final TextButton button = new TextButton(group.name, skin);
                if (!Fly.DEBUG_MODE && group.id > PlayerProfileManager.getInstance().getCurrentPlayerProfile().getPassedLevelgroupID()) {
                    button.setDisabled(true);
                } else {
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            setLevelChooserScreen(group);
                        }
                    });
                }
                contentTable.add(button).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE).expand();
            }
            contentTable.row();
        }
    }
    
    /**
     * Shows a list of all available level groups.
     */
    public void generateDynamicContent() {
        
        
    }
    
    /**
     * Lazy loading of {@link LevelChooserScreen} showing the specified
     * {@link LevelGroup}.
     * 
     * @param levelGroup
     */
    public void setLevelChooserScreen(LevelGroup levelGroup) {
        if (levelChooserScreen == null) {
            levelChooserScreen = new LevelChooserScreen(this);
        }
        levelChooserScreen.setGroup(levelGroup);
        levelChooserScreen.set();
    }
    
//    @Override
//    public void show() {
//        super.show();
//        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(UI.Transitions.FADE_IN_TIME)));
//    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (levelChooserScreen != null) {
            levelChooserScreen.dispose();
            levelChooserScreen = null;
        }
    }
}
