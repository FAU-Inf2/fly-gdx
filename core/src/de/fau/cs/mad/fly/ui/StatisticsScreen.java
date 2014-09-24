package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;

/**
 * UI for checking scores of user, also for add and change user. Is called from
 * the main menu screen.
 * 
 * 
 * @author Qufang Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class StatisticsScreen extends BasicScreen {
    
    private Table levelGroupTable;
    
    /**
     * generate Content here two Tables are used to show the content: userTable
     * and levelgroupTable
     */
    @Override
    protected void generateBackButton() {
        stage.clear();
        initLevegroups();
        stage.addActor(levelGroupTable);
        
    }
    
    /**
     * init and display the level groups
     */
    private void initLevegroups() {
        levelGroupTable = new Table();
        levelGroupTable.pad(UI.Window.BORDER_SPACE);
        levelGroupTable.setFillParent(true);
        Skin skin = SkinManager.getInstance().getSkin();
        Label selectLevelGroup = new Label(I18n.t("selectLevelGroup"), skin);
        selectLevelGroup.setAlignment(Align.center);
        levelGroupTable.add(selectLevelGroup).colspan(2).center();
        levelGroupTable.row();
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
                        ((Fly) Gdx.app.getApplicationListener()).setLevelsStatisScreen(group);
                    }
                });
                levelGroupTable.add(button).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT).expand();
            }
            levelGroupTable.row();
        }
    }
    
}
