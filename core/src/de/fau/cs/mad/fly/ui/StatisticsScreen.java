package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
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
 * UI for checking scores of user, also for add and change user
 * 
 * 
 * @author Qufang Fan
 */
public class StatisticsScreen extends BasicScreen {
    
    private TextButton addUserButton;
    private TextButtonStyle textButtonStyle;
    private Table infoTable;
    private Table userTable;
    private Table levelgroupTable;
    private TextField newUserField;
    
    private PlayerProfile playerProfile;
    private int selectedUserindex = 0;
    private SelectBox<String> userList;
    
    private void initButtons() {
        textButtonStyle = skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class);
        addUserButton = new TextButton(I18n.t("addUserButtonText"), textButtonStyle);
        
        addUserButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = newUserField.getText().trim();
                if (!"".equals(name))// todo more check
                {
                    for (PlayerProfile playerProfile : PlayerProfileManager.getInstance().getAllPlayerProfiles()) {
                        if (playerProfile.getName().equals(name)) {
                            new Dialog("", skin) {
                                {
                                    text("The user already exists!");
                                    button("OK");
                                }
                            }.show(stage);
                            return;
                        }
                    }
                    PlayerProfile playerProfile = new PlayerProfile();
                    playerProfile.setName(name);
                    
                    PlayerProfileManager.getInstance().savePlayer(playerProfile);
                    updateUserTable();
                    new Dialog("", skin) {
                        {
                            text("User Added.");
                            button("OK");
                        }
                    }.show(stage);
                } else {
                    new Dialog("", skin) {
                        {
                            text("User name should not be null!");
                            button("OK");
                        }
                    }.show(stage);
                }
            }
        });
        
    }
    
    private void genarateUserTable() {
        userTable.clear();
        // add user name and add user buttons
        String userName = playerProfile.getName();
        userTable.add(new Label(I18n.t("usernameLableText"), skin)).pad(6f);
        
        userList = new SelectBox<String>(skin);
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
        userTable.add(userList).width(800).pad(6f).uniform();
        userTable.row().expand();
        
        newUserField = new TextField("", skin);
        newUserField.setMessageText("User name");
        userTable.add(newUserField).width(1200f).height(200f).pad(6f).uniform();
        userTable.add(addUserButton).pad(6f).uniform().width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.row().expand();
        userTable.layout();
    }
    
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
        genarateUserTable();
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
    
    @Override
    public void show() {
        super.show();
    }
    
}
