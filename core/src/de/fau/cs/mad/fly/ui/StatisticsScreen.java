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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
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
    private Table userTable;
    private Table levelgroupTable;
    private TextField newUserField;
    
    private int selectedUserindex = 0;
    private SelectBox<String> userList;
    
    /**
     * init the UI controls which are relative to User management operations.
     * all these control are placed in userTable
     */
    private void generateUserTable() {
        userTable.clear();
        userTable.add(new Label(I18n.t("usernameLableText"), skin)).pad(6f);
        // add all user to userList and set the current user to display value
        userList = new SelectBox<String>(skin);
        updateUserTable();
        
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
        newUserField = new TextField("", skin);
        newUserField.setTextFieldListener(new TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if ((key == '\r' || key == '\n')) {
                    addNewUser();
                }
            }
        });
        newUserField.setMessageText(I18n.t("TipsUserName"));
        userTable.add(newUserField).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH);
        userTable.add(addUserButton).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.row().expand();
        // TODO delete user and change user name
        userTable.layout();
    }
    
    /**
     * Method that is called, if the list of user changes.
     */
    public void updateUserTable() {
        String currentUserName = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName();
        Array<String> nameList = new Array<String>();
        java.util.List<PlayerProfile> playerList = PlayerProfileManager.getInstance().getAllPlayerProfiles();
        String name;
        for (int i = 0; i < playerList.size(); i++) {
            name = playerList.get(i).getName();
            nameList.add(name);
            if (currentUserName.equals(name)) {
                selectedUserindex = i;
            }
        }
        userList.setItems(nameList);
        userList.setSelectedIndex(selectedUserindex);
    }
    
    /**
     * generate Content here two Tables are used to show the content: userTable
     * and levelgroupTable
     */
    @Override
    protected void generateContent() {
        addUserButton = new TextButton(I18n.t("addUserButtonText"), skin);
        stage.clear();
        final Table table = new Table();
        table.pad(UI.Window.BORDER_SPACE);
        table.setFillParent(true);
        table.debug();
        stage.addActor(table);
        
        Table infoTable = new Table();
        userTable = new Table();
        generateUserTable();
        
        addUserButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewUser();
            }
        });
        infoTable.add(userTable);
        infoTable.row();
        
        initLevegroups();
        
        infoTable.add(levelgroupTable);
        
        final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
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
                final TextButton button = new TextButton(group.name, skin);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ((Fly) Gdx.app.getApplicationListener()).setLevelsStatisScreen(group);
                    }
                });
                levelgroupTable.add(button).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).expand();
            }
            levelgroupTable.row().expand();
        }
    }
    
    /**
     * Method that is called when the button is pressed to create a new user or
     * the enter button is pressed.
     * <p>
     * Before a new user can be created, two things have to be checked:
     * <p>
     * 1) Does the name contains any characters? - Otherwise display a
     * corresponding {@link Dialog}.
     * <p>
     * 2) Does the player already exists? - If so, display a corresponding
     * {@link Dialog}.
     * <p>
     * Other checks are done in {@link UserNameTextFieldFilter}
     * <p>
     * If a new player is created, display a corresponding {@link Dialog}.
     */
    public void addNewUser() {
        String name = newUserField.getText();
        Dialog dialog = new Dialog("", skin, "dialog");
        boolean userExists = false;
        if (!"".equals(name)) {
            List<PlayerProfile> allPlayerProfiles = PlayerProfileManager.getInstance().getAllPlayerProfiles();
            int numberOfAllPlayerProfiles = allPlayerProfiles.size();
            PlayerProfile playerProfile;
            for (int i = 0; i < numberOfAllPlayerProfiles; i++) {
                playerProfile = allPlayerProfiles.get(i);
                if (playerProfile.getName().equals(name)) {
                    userExists = true;
                    i = numberOfAllPlayerProfiles;
                }
            }
            if (userExists) {
                dialog.text(I18n.t("UserExists"));
            } else {
                // update player profile
                playerProfile = new PlayerProfile();
                playerProfile.setName(name);
                PlayerProfileManager.getInstance().setCurrentPlayer(playerProfile);
                PlayerProfileManager.getInstance().savePlayer(playerProfile);
                updateUserTable();
                // reset user input field
                newUserField.setText("");
                newUserField.setMessageText(I18n.t("TipsUserName"));
                // set dialog text
                dialog.text(I18n.t("UserAdded"));
            }
        } else {
            dialog.text(I18n.t("NullUserName"));
        }
        Gdx.input.setOnscreenKeyboardVisible(false);
        TextButton button = new TextButton(I18n.t("ok"), skin);
        dialog.button(button);
        dialog.show(stage);
    }
    
}
