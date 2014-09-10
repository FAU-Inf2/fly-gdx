package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

/**
 * Offers a selection of levels to start.
 * 
 * @author Lukas Hahmann
 */
public class LevelChooserScreen extends BasicScreen {
    private LevelGroup levelGroup;
    private ScrollPane levelScrollPane;
    
    /**
     * Shows a list of all available levels.
     */
    public void generateDynamicContent() {
        // calculate width and height of buttons and the space in between
        List<LevelProfile> allLevels = levelGroup.getLevels();
        
        stage.clear();
        
        // table that contains all buttons
        Table scrollableTable = new Table(skin);
        levelScrollPane = new ScrollPane(scrollableTable, skin);
        levelScrollPane.setScrollingDisabled(true, false);
        levelScrollPane.setFadeScrollBars(false);
        levelScrollPane.setFillParent(true);
        
        int rowToScrollTo = -1;
        PlayerProfile currentProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        
        // create a button for each level
        int maxRows = (int) Math.ceil((double) allLevels.size() / (double) UI.Buttons.BUTTONS_IN_A_ROW);
        
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(allLevels.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
            // fill a row with buttons
            for (int column = 0; column < maxColumns; column++) {
                final LevelProfile level = allLevels.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
                final TextButton button = new TextButton(level.name, skin);
                
                if (!Fly.DEBUG_MODE && levelGroup.id > currentProfile.getPassedLevelgroupID() || (levelGroup.id == currentProfile.getPassedLevelgroupID() && level.id > currentProfile.getPassedLevelID())) {
                    button.setDisabled(true);
                    if(rowToScrollTo < 0) {
                        rowToScrollTo = row;
                    }
                }
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        PlayerProfile currentProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
                        currentProfile.setCurrentLevelGroup(levelGroup);
                        currentProfile.saveCurrentLevelGroup();
                        currentProfile.setCurrentLevelProfile(level);
                        currentProfile.saveCurrentLevelProfile();
                        Loader.getInstance().loadLevel(level);
                    }
                });
                scrollableTable.add(button).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).expand();
            }
            scrollableTable.row().expand();
        }
        stage.addActor(levelScrollPane);
        super.stage.act();
        super.stage.draw();
        if(rowToScrollTo < 0) {
            rowToScrollTo = maxRows;
        }
        levelScrollPane.setScrollY(rowToScrollTo * (UI.Buttons.MAIN_BUTTON_HEIGHT + UI.Buttons.SPACE_HEIGHT)-Gdx.graphics.getHeight());
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
    
    @Override
    protected void generateContent() {
        //is done in .generateDynamicContent
    }
}
