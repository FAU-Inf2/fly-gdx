package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

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
    
    private TextButton deleteUserButton;
    private TextButton editUserButton;
    
    private int selectedUserindex = 0;
    private SelectBox<String> userSelectBox;
    
    LabelStyle dialogLabelStyle;
    
    private float padding = 20f;
    
    public static final String OK = "true";
    public static final String CANCEL = "cancel";
    
    public final static int MAX_NAME_WIDTH = 1650;
    
    /**
     * init the UI controls which are relative to User management operations.
     * all these control are placed in userTable
     */
    private void generateUserTable() {
        stage.clear();
        Skin skin = SkinManager.getInstance().getSkin();
        dialogLabelStyle = skin.get("black", LabelStyle.class);
        Table userTable = new Table();
        userTable.pad(UI.Window.BORDER_SPACE);
        userTable.setFillParent(true);
        Table contentTable = new Table();
        userTable.add(contentTable);
        contentTable.setBackground(new NinePatchDrawable(skin.get("button-up", NinePatch.class)));
        contentTable.add(new Label(I18n.t("usernameLableText") + ":", skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        // add all user to userList and set the current user to display value
        userSelectBox = new SelectBox<String>(skin);
        updateUserTable();
        
        // handle event when another user was selected
        userSelectBox.getList().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userSelectBox.getSelectedIndex() >= 0 && userSelectBox.getSelectedIndex() != selectedUserindex) {
                    selectedUserindex = userSelectBox.getSelectedIndex();
                    PlayerProfile temPlayerProfile = PlayerProfileManager.getInstance().getAllPlayerProfiles().get(selectedUserindex);
                    PlayerProfileManager.getInstance().setCurrentPlayer(temPlayerProfile);
                    updateUserTable();
                }
            }
        });
        contentTable.add(userSelectBox).width(MAX_NAME_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        contentTable.row().pad(padding);
        
        // show fly id, if no fly id, show a info button
        contentTable.add(new Label(I18n.t("labelFlyId") + ":", skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() > 0) {
            contentTable.add(new Label(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() + "", skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        } else {
            ImageButton infoButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
            infoButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Skin skin = SkinManager.getInstance().getSkin();
                    Dialog dialog = new Dialog("", skin, "dialog");
                    dialog.text(I18n.t("msgGetFlyId"), dialogLabelStyle);
                    TextButton button = new TextButton(I18n.t("ok"), skin);
                    dialog.button(button);
                    dialog.show(stage);
                }
            });
            contentTable.add(infoButton).width(UI.Buttons.MAIN_BUTTON_HEIGHT).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        }
        contentTable.row().pad(padding);
        
        PlayerProfile playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        
        // show total score
        contentTable.add(new Label(I18n.t("labelTotalScore"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        contentTable.add(new Label("" + playerProfile.getMoney(), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        // TODO: do not use money for that!
        contentTable.row().pad(padding);
        
        // show passed group and level
        contentTable.add(new Label(I18n.t("lastLevel") + ":", skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        
        int group = playerProfile.getPassedLevelgroupID();
        int level = playerProfile.getPassedLevelID();
        if (level > LevelGroupManager.getInstance().getLastGroup().getLastLevelProfile().id) {
            contentTable.add(new Label(I18n.t("ALLGroupPassed"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        } else {
            contentTable.add(new Label("" + group + " - " + level, skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        }
        contentTable.row().pad(padding);
        
        // show delete user and edit user name button
        contentTable.add(editUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        contentTable.add(deleteUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        contentTable.row().pad(padding);
        
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
        contentTable.add(newUserField).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        contentTable.add(addUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        stage.addActor(userTable);
    }
    
    /**
     * Method that is called, if the list of user changes.
     */
    public void updateUserTable() {
        PlayerProfileManager playerProfileManager = PlayerProfileManager.getInstance();
        String currentUserName = playerProfileManager.getCurrentPlayerProfile().getName();
        Array<String> nameList = new Array<String>();
        List<PlayerProfile> playerList = playerProfileManager.getAllPlayerProfiles();
        String name;
        for (int i = 0; i < playerList.size(); i++) {
            name = playerList.get(i).getName();
            nameList.add(name);
            if (currentUserName.equals(name)) {
                selectedUserindex = i;
            }
        }
        playerProfileManager.setCurrentPlayer(playerList.get(selectedUserindex));
        userSelectBox.setItems(nameList);
        userSelectBox.setSelectedIndex(selectedUserindex);
    }
    
    /**
     * generate Content here two Tables are used to show the content: userTable
     * and levelgroupTable
     */
    @Override
    protected void generateContent() {
        Skin skin = SkinManager.getInstance().getSkin();
        addUserButton = new TextButton(I18n.t("addUserButtonText"), skin);
        deleteUserButton = new TextButton(I18n.t("button.deleteUser"), skin);
        editUserButton = new TextButton(I18n.t("button.editUser"), skin);
        
        addUserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewUser();
            }
        });
        
        deleteUserButton.addListener(new ChangeListener() {
            
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Dialog dialog;
                Skin skin = SkinManager.getInstance().getSkin();
                if (PlayerProfileManager.getInstance().getAllPlayerProfiles().size() <= 1) {
                    dialog = new Dialog("", skin, "dialog");
                    dialog.text(I18n.t("msgLastUser"), dialogLabelStyle);
                    TextButton button = new TextButton(I18n.t("ok"), skin);
                    dialog.button(button);
                    dialog.show(stage);
                } else {
                    TextButton button = new TextButton(I18n.t("ok"), skin);
                    TextButton concelButton = new TextButton(I18n.t("cancel"), skin);
                    
                    dialog = new Dialog("", skin, "dialog") {
                        protected void result(Object object) {
                            if (object.toString().equals(OK)) {
                                PlayerProfileManager.getInstance().deletePlayerProfile();
                                updateUserTable();
                            }
                        }
                    };
                    dialog.text(I18n.t("msgDeleteUser"), dialogLabelStyle);
                    dialog.button(button, OK).add();
                    dialog.button(concelButton, CANCEL);
                    dialog.key(Keys.ENTER, OK).key(Keys.ESCAPE, CANCEL);
                    dialog.show(stage);
                }
            }
        });
        
        editUserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final PlayerProfile playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
                
                Screen dialog = new EditPlayerNameScreen(PlayerScreen.this, playerProfile); 
                ((Fly) Gdx.app.getApplicationListener()).setScreen(dialog);
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
        Skin skin = SkinManager.getInstance().getSkin();
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
                dialog.text(I18n.t("UserExists"), dialogLabelStyle);
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
                dialog.text(I18n.t("UserAdded"), dialogLabelStyle);
            }
        } else {
            dialog.text(I18n.t("NullUserName"), dialogLabelStyle);
        }
        Gdx.input.setOnscreenKeyboardVisible(false);
        TextButton button = new TextButton(I18n.t("ok"), skin);
        dialog.button(button, OK).key(Keys.ENTER, OK);
        dialog.show(stage);
    }
    
    @Override
    public void hide() {
        userSelectBox.hideList();
    }
    
    @Override
    public void show() {
        super.show();
        generateUserTable();
    }
}
