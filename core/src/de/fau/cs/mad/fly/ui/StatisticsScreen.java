package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.AppSettingsManager;

/**
 * UI for checking scores of user, also for add and change user. Is called from
 * the main menu screen.
 * 
 * 
 * @author Qufang Fan
 */
public class StatisticsScreen extends BasicScreen {
    
    private TextButton addUserButton;
    private Table infoTable;
    private Table userTable;
    private Table levelgroupTable;
    private TextField newUserField;
    
    private PlayerProfile playerProfile;
    private int selectedUserindex = 0;
    private SelectBox<String> userList;
    
    /**
     * init buttons, which don't need to be created dynamically
     */
    private void initButtons() {      
        // init add user button
        addUserButton = new TextButton(I18n.t("addUserButtonText"), skin, UI.Buttons.DEFAULT_STYLE);
        addUserButton.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String name = newUserField.getText();
                
                if (!"".equals(name)) {
                    for (PlayerProfile playerProfile : PlayerProfileManager.getInstance().getAllPlayerProfiles()) {
                        if (playerProfile.getName().equals(name)) {
                            new Dialog("", skin) {
                                {
                                    text(I18n.t("UserExists"));
                                    button(I18n.t("ok"));
                                }
                            }.show(stage);
                            return;
                        }
                    }
                    PlayerProfile playerProfile = new PlayerProfile();
                    playerProfile.setName(name);
                    
                    PlayerProfileManager.getInstance().savePlayer(playerProfile);
                    generateUserTable();
                    new Dialog("", skin) {
                        {
                            text(I18n.t("UserAdded"));
                            button(I18n.t("ok"));
                        }
                    }.show(stage);
                } else {
                    new Dialog("", skin) {
                        {
                            text(I18n.t("NullUserName"));
                            button("OK");
                        }
                    }.show(stage);
                }
            }
        });
    }
    
    /**
     * init the UI controls which are relative to User management operations.
     * all these control are placed in userTable
     */
    private void generateUserTable() {
        userTable.clear();
        
        // add all user to userList and set the current user to display value
        String userName = playerProfile.getName();
        userTable.add(new Label(I18n.t("usernameLableText"), skin)).pad(6f);
        
        userList = new SelectBox<String>(skin, "rounded");
        Array<String> nameList = new Array<String>();
        java.util.List<PlayerProfile> playerList = PlayerProfileManager.getInstance().getAllPlayerProfiles();
        for (int i = 0; i < playerList.size(); i++) {
            PlayerProfile playerProfile = playerList.get(i);
            nameList.add(playerProfile.getName());
            if (userName.equals(playerProfile.getName())) {
                selectedUserindex = i;
            }
        }
        userList.setItems(nameList);
        userList.setSelectedIndex(selectedUserindex);
        userList.getSelection().setRequired(false);
        userList.getSelection().setToggle(true);
        
        // handle event when another user was selected
        userList.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (userList.getSelectedIndex() >= 0 && userList.getSelectedIndex() != selectedUserindex) {
                    selectedUserindex = userList.getSelectedIndex();
                    PlayerProfile temPlayerProfile = PlayerProfileManager.getInstance().getAllPlayerProfiles().get(selectedUserindex);
                    PlayerProfileManager.getInstance().setCurrentPlayer(temPlayerProfile);
                    AppSettingsManager.Instance.setIntegerSetting(AppSettingsManager.CHOSEN_USER, temPlayerProfile.getId());
                }
                return false;
            }
        });
        userTable.add(userList).width(UI.Buttons.MAIN_BUTTON_WIDTH).pad(6f).uniform();
        userTable.row().expand();
        
        // add user field and button
        newUserField = new TextField("", skin, "rounded");
        newUserField.setTextFieldFilter(new UserNameTextFieldFilter());
        newUserField.setMessageText(I18n.t("TipsUserName"));
        userTable.add(newUserField).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH);
        userTable.add(addUserButton).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.row().expand();
        // TODO delete user and change user name
        userTable.layout();
    }
    
    /**
     * when a new user is added, call this function to update userList
     */
    private void updateUserTable() {
        String userName = playerProfile.getName();
        userList.clear();
        Array<String> nameList = new Array<String>();
        java.util.List<PlayerProfile> playerList = PlayerProfileManager.getInstance().getAllPlayerProfiles();
        for (int i = 0; i < playerList.size(); i++) {
            PlayerProfile playerProfile = playerList.get(i);
            nameList.add(playerProfile.getName());
            if (userName.equals(playerProfile.getName())) {
                selectedUserindex = i;
            }
        }
        userList.setItems(nameList);
        userList.setSelectedIndex(selectedUserindex);
        userList.getSelection().setRequired(false);
        userList.getSelection().setToggle(true);
        userList.layout();
    }
    
    /**
     * generate Content here two Tables are used to show the content: userTable
     * and levelgroupTable
     */
    @Override
    protected void generateContent() {
        playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        initButtons();
        stage.clear();
        final Table table = new Table();
        table.pad(Gdx.graphics.getWidth() * 0.1f);
        table.setFillParent(true);
        table.debug();
        stage.addActor(table);
        
        infoTable = new Table();
        userTable = new Table();
        generateUserTable();
        infoTable.add(userTable);
        infoTable.row();
        
        initLevegroups();
        
        infoTable.add(levelgroupTable);
        
        final ScrollPane statisticsPane = new ScrollPane(infoTable, skin, UI.Window.TRANSPARENT_SCROLL_PANE_STYLE);
        statisticsPane.setFadeScrollBars(false);
        statisticsPane.setScrollingDisabled(true, false);
        table.row().expand();
        table.add(statisticsPane);
    }
    
    /**
     * init and display the level groups
     */
    private void initLevegroups() {
        levelgroupTable = new Table();
        levelgroupTable.pad(200, 0, 0, 0);
        Label selectLevelGroup = new Label(I18n.t("selectLevelGroup"), skin);
        selectLevelGroup.setAlignment(Align.center);
        levelgroupTable.add(selectLevelGroup).pad(6f).colspan(2).center();
        levelgroupTable.row().expand();
        List<LevelGroup> levelGroups = LevelGroupManager.getInstance().getLevelGroups();
        // create a button for each level group
        int maxRows = (int) Math.ceil((float) levelGroups.size() / (float) UI.Buttons.BUTTONS_IN_A_ROW);
        
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(levelGroups.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
            // fill a row with buttons
            for (int column = 0; column < maxColumns; column++) {
                final LevelGroup group = levelGroups.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
                final TextButton button = new TextButton(group.name, skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class));
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        PlayerProfileManager.getInstance().getCurrentPlayerProfile().setChosenLevelGroup(group);
                        ((Fly) Gdx.app.getApplicationListener()).setLevelsStatisScreen(group);
                    }
                });
                levelgroupTable.add(button).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).expand();
            }
            levelgroupTable.row().expand();
        }
    }
    
}
