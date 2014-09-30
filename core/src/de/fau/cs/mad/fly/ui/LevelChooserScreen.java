package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.UI.Window;

/**
 * Offers the levels of one {@link LevelGroup }to start.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LevelChooserScreen extends BasicScreenWithBackButton {
    
    private LevelGroup levelGroup;
    
    public LevelChooserScreen(BasicScreen screenToReturn) {
        super(screenToReturn);
        generateBackButton();
    }
    
    /**
     * Shows a list of all available levels. This list is always created when
     * the {@link LevelChooserScreen} is shown because either the level group or
     * the progress may have changed since the last time it has been generated.
     */
    public void generateDynamicContent() {
        Skin skin = SkinManager.getInstance().getSkin();
        
        // clear the contentTable so that elements of a previous view are
        // deleted
        contentTable.clear();
        
        Table buttonTable = new Table();
        Table outerButtonTable = new Table();
        outerButtonTable.add(buttonTable).width(viewport.getWorldWidth() - 2 * (Window.BORDER_SPACE - UI.Buttons.SPACE));
        contentTable.add(outerButtonTable);
        
        final PlayerProfile currentProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        
        List<LevelProfile> allLevels = levelGroup.getLevels();
        
        // create a button for each level. The amount of buttons in a row can be
        // adjusted
        int buttonsInARow;
        int buttonWidth;
        if(allLevels.size() > 2) {
            buttonsInARow = 9;
            buttonWidth = UI.Buttons.IMAGE_BUTTON_WIDTH;
        }
        else {
            buttonsInARow = UI.Buttons.BUTTONS_IN_A_ROW;
            buttonWidth = UI.Buttons.TEXT_BUTTON_WIDTH;
        }
        int maxRows = (int) Math.ceil((float) allLevels.size() / (float) buttonsInARow);

        currentProfile.checkPassedLevelForTutorials();
        
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(allLevels.size() - (row * buttonsInARow), buttonsInARow);
            // fill a row with buttons
            for (int column = 0; column < maxColumns; column++) {
                final LevelProfile level = allLevels.get(row * buttonsInARow + column);
                final TextButton button = new TextButton(level.name, skin);
                
                if (!Fly.DEBUG_MODE && (levelGroup.id > currentProfile.getPassedLevelgroupID() || (levelGroup.id == currentProfile.getPassedLevelgroupID() && level.id > currentProfile.getPassedLevelID()))) {
                    button.setDisabled(true);
                }
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentProfile.setCurrentLevelGroup(levelGroup);
                        currentProfile.saveCurrentLevelGroup();
                        currentProfile.setCurrentLevelProfile(level);
                        currentProfile.saveCurrentLevelProfile();
                        Loader.getInstance().loadLevel(level);
                    }
                });
                buttonTable.add(button).width(buttonWidth).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).pad(UI.Buttons.SPACE).expand();
            }
            buttonTable.row();
        }
    }
    
    /**
     * Sets the current group for which the level chooser screen should display
     * the levels.
     * 
     * @param group
     *            The group to display.
     */
    public void setGroup(LevelGroup group) {
        levelGroup = group;
    }
    
    @Override
    public void show() {
        super.show();
        generateDynamicContent();
    }
    
}
