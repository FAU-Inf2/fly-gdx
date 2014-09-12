package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.AppSettingsManager;

/**
 * Screen to see your user, to switch the user and to add a new user. Is called
 * from the {@link MainMenuScreen}.
 * 
 * 
 * @author Qufang Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class PlayerScreen extends BasicScreen {
    
    private TextButton addUserButton;
    private TextField newUserField;
    
    private int selectedUserindex = 0;
    private SelectBox<String> userList;
    
    /**
     * init the UI controls which are relative to User management operations.
     * all these control are placed in userTable
     */
    private void generateUserTable() {
        Table userTable = new Table();
        userTable.pad(UI.Window.BORDER_SPACE,UI.Window.BORDER_SPACE,UI.Window.REFERENCE_HEIGHT/2,UI.Window.BORDER_SPACE);
        userTable.setFillParent(true);
        userTable.add(new Label(I18n.t("usernameLableText"), skin));
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
        userTable.add(userList).width(UI.Buttons.MAIN_BUTTON_WIDTH);
        userTable.row().expand();
        
        // add user field and button
        newUserField = new TextField("", skin);
        newUserField.setTextFieldFilter(new UserNameTextFieldFilter());
        newUserField.setTextFieldListener(new TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if ((key == '\r' || key == '\n')) {
                    addNewUser();
                }
            }
        });
        newUserField.setMessageText(I18n.t("TipsUserName"));
        userTable.add(newUserField).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT).top().expand();
        userTable.add(addUserButton).pad(UI.Buttons.SPACE_HEIGHT).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).top().expand();
        stage.addActor(userTable);
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
        generateUserTable();
        
        addUserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewUser();
            }
        });
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
        PlayerProfileManager profileManager = PlayerProfileManager.getInstance();
        boolean userExists = false;
        if (!"".equals(name)) {
            List<PlayerProfile> allPlayerProfiles = profileManager.getAllPlayerProfiles();
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
                profileManager.setCurrentPlayer(playerProfile);
                profileManager.savePlayer(playerProfile);
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
    
    @Override
    public void hide() {
        super.hide();
        userList.hideList();
    }
    
}
