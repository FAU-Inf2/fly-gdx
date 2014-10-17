package de.fau.cs.mad.fly.ui.screens;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * UI for checking scores of user, also for add and change user. Is called from
 * the main menu screen.
 * 
 * 
 * @author Qufang Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class HighscoreScreen extends BasicScreenWithBackButton {
    
    private LevelGroupHighscoreScreen levelHighscoreScreen;
    
    public HighscoreScreen(BasicScreen screenToReturn) {
        super(screenToReturn);
    }
    
    /**
     * generate Content here two Tables are used to show the content: userTable
     * and levelgroupTable
     */
    @Override
    protected void generateContent() {
        super.generateBackButton();
        Skin skin = SkinManager.getInstance().getSkin();
        Label selectLevelGroup = new Label(I18n.t("selectLevelGroup"), skin);
        selectLevelGroup.setAlignment(Align.center);
        contentTable.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, 2 * UI.Window.BORDER_SPACE + UI.Buttons.IMAGE_BUTTON_HEIGHT, UI.Window.BORDER_SPACE);
        contentTable.add(selectLevelGroup).colspan(2).center();
        contentTable.row();
        List<LevelGroup> levelGroups = LevelGroupManager.getInstance().getLevelGroups();
        // create a button for each level group
        int maxRows = (int) Math.ceil((float) levelGroups.size() / (float) UI.Buttons.BUTTONS_IN_A_ROW);
        
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(levelGroups.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
            // fill a row with buttons
            for (int column = 0; column < maxColumns; column++) {
                final LevelGroup group = levelGroups.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
                final TextButton button = new TextButton(group.name, skin);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        setLevelHighscoreScreen(group);
                    }
                });
                contentTable.add(button).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE).expand();
            }
            contentTable.row();
        }
    }
    
    private void setLevelHighscoreScreen(LevelGroup group) {
        if (levelHighscoreScreen == null) {
            levelHighscoreScreen = new LevelGroupHighscoreScreen(this);
        }
        levelHighscoreScreen.setLevelGroup(group);
        levelHighscoreScreen.set();
    }
    
    @Override
    public void dispose() {
        if(levelHighscoreScreen != null) {
            levelHighscoreScreen.dispose();
            levelHighscoreScreen = null;
        }
    }
}
