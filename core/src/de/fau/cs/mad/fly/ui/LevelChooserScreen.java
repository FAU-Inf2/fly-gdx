package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
        
        // clear the contentTable so that elements of a previous view are deleted
        contentTable.clear();
        
        Table buttonTable = new Table();
        ScrollPane levelScrollPane = new ScrollPane(buttonTable, skin);
        levelScrollPane.setScrollingDisabled(true, false);
        levelScrollPane.setFadeScrollBars(false);
        contentTable.add(levelScrollPane);
        
        int rowToScrollTo = -1;
        final PlayerProfile currentProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        
        List<LevelProfile> allLevels = levelGroup.getLevels();
        
        // create a button for each level. The amount of buttons in a row can be
        // adjusted
        int maxRows = (int) Math.ceil((double) allLevels.size() / (double) UI.Buttons.BUTTONS_IN_A_ROW);
        
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(allLevels.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
            // fill a row with buttons
            for (int column = 0; column < maxColumns; column++) {
                final LevelProfile level = allLevels.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
                final TextButton button = new TextButton(level.name, skin);
                
                if (!Fly.DEBUG_MODE && (levelGroup.id > currentProfile.getPassedLevelgroupID() || (levelGroup.id == currentProfile.getPassedLevelgroupID() && level.id > currentProfile.getPassedLevelID()))) {
                    button.setDisabled(true);
                    if (rowToScrollTo < 0) {
                        rowToScrollTo = row;
                    }
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
                buttonTable.add(button).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE).expand();
            }
            buttonTable.row().expand();
        }
        
        // let the stage act once, otherwise scrolling programatically is not
        // possible
        stage.act();
        stage.draw();
        if (rowToScrollTo < 0) {
            rowToScrollTo = maxRows;
        }
        levelScrollPane.setScrollY(rowToScrollTo * (UI.Buttons.TEXT_BUTTON_HEIGHT + UI.Buttons.SPACE_HEIGHT) - Gdx.graphics.getHeight());
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
