package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
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
    
    private TextButton deleteUserButton;
    private TextButton editUserButton;
    
    private int selectedUserindex = 0;
    private SelectBox<String> userList;
    
    /**
     * init the UI controls which are relative to User management operations.
     * all these control are placed in userTable
     */
    private void generateUserTable() {
    	stage.clear();
        Table userTable = new Table();
        userTable.pad(UI.Window.BORDER_SPACE,UI.Window.BORDER_SPACE,UI.Window.REFERENCE_HEIGHT/2,UI.Window.BORDER_SPACE);
        userTable.setFillParent(true);
        userTable.add(new Label(I18n.t("usernameLableText"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
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
                    generateUserTable();
                }
                return false;
            }
        });
        userTable.add(userList).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT).expand();
        userTable.row().expand();
        
        //show fly id, if no fly id, show a info button
        userTable.add(new Label(I18n.t("labelFlyId"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT).expand();
        if(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID()>0) {
        	  userTable.add(new Label(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() + "", skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT).expand();
        } else {
        	ImageButton infoButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
        	infoButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                	 Dialog dialog = new Dialog("", skin, "dialog");
                	 dialog.text(I18n.t("msgGetFlyId"));
                	 TextButton button = new TextButton(I18n.t("ok"), skin);
                     dialog.button(button);
                     dialog.show(stage);
                }
            });        	
        	userTable.add(infoButton).width(UI.Buttons.MAIN_BUTTON_HEIGHT).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        }
        userTable.row().expand();
        
        //show total score
        userTable.add(new Label(I18n.t("labelTotalScore"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        userTable.add(new Label("" + PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney(), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        userTable.row().expand();
        
        //show passed group
        userTable.add(new Label(I18n.t("LabelReachedGroup"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        int group = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getPassedLevelgroupID();
        if(group>LevelGroupManager.getInstance().getLastGroupID()){
        	userTable.add(new Label(I18n.t("ALLGroupPassed") , skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        } else {
        	
        	String groupname = LevelGroupManager.getInstance().getLevelGroup(group).name;
        	userTable.add(new Label(groupname, skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        }      	
        userTable.row().expand();
        
        //show delete user and edit user name button        
        userTable.add(editUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        userTable.add(deleteUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT);
        userTable.row().expand();;
        
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
        deleteUserButton = new TextButton(I18n.t("button.deleteUser"), skin);
        editUserButton = new TextButton(I18n.t("button.editUser"), skin);
        
        addUserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewUser();
            }
        });      
        
		deleteUserButton.addListener(new ChangeListener() {
			final String YES = "true";
			final String CANCEL = "cancel";
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (PlayerProfileManager.getInstance().getAllPlayerProfiles().size() <= 1) {
					Dialog dialog = new Dialog("", skin, "dialog");
					dialog.text(I18n.t("msgLastUser"));
					TextButton button = new TextButton(I18n.t("ok"), skin);
					dialog.button(button);
					dialog.show(stage);
				} else {
					TextButton button = new TextButton(I18n.t("ok"), skin);
					TextButton concelButton = new TextButton(I18n.t("buttenText.cancel"), skin);

					new Dialog("", skin, "dialog") {
						protected void result(Object object) {
							Gdx.app.log("fan", object.toString());
							if (object.toString().equals(YES)) {								
								PlayerProfileManager.getInstance().deletePlayerProfile();
								generateUserTable();
							}
						}
					}.text(I18n.t("msgDeleteUser")).button(button, YES).button(concelButton, CANCEL).key(Keys.ENTER, YES).key(Keys.ESCAPE, CANCEL).show(stage);
				}
			}
		});

		editUserButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TextButton button = new TextButton(I18n.t("ok"), skin);
				TextButton concelButton = new TextButton(I18n.t("buttenText.cancel"), skin);

				new Dialog("", skin, "dialog") {
					protected void result(Object object) {
						if (Boolean.getBoolean(object.toString()) == true) {
							
						}
					}
				}.text(I18n.t("msgInputNewUsername")).button(button, true).button(concelButton, false).key(Keys.ENTER, true).key(Keys.ESCAPE, false).show(stage);
			}
		});
        
        generateUserTable();
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
