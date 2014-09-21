package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
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
import com.badlogic.gdx.utils.Array;

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
    private SelectBox<String> userList;
    
    private LabelStyle dialogLabelStyle;
    
    final String YES = "true";
    final String CANCEL = "cancel";
    
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
        userTable.add(new Label(I18n.t("usernameLableText"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
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
        userTable.add(userList).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.row().expand();
        
        // show fly id, if no fly id, show a info button
        userTable.add(new Label(I18n.t("labelFlyId"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() > 0) {
            userTable.add(new Label(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() + "", skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        } else {
            ImageButton infoButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
            infoButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Skin skin = SkinManager.getInstance().getSkin();
                    Dialog dialog = new Dialog("", skin, "dialog");
                    dialog.text(I18n.t("msgGetFlyId"),dialogLabelStyle);
                    TextButton button = new TextButton(I18n.t("ok"), skin);
                    dialog.button(button);
                    dialog.show(stage);
                }
            });
            userTable.add(infoButton).width(UI.Buttons.MAIN_BUTTON_HEIGHT).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        }
        userTable.row().expand();
        
        // show total score
        userTable.add(new Label(I18n.t("labelTotalScore"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.add(new Label("" + PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney(), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.row().expand();
        
        // show passed group and level
        userTable.add(new Label(I18n.t("LabelReachedGroup"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        int group = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getPassedLevelgroupID();
        int level = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getPassedLevelID();
        if (level > LevelGroupManager.getInstance().getLastGroup().getLastLevelProfile().id) {
            userTable.add(new Label(I18n.t("ALLGroupPassed"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        } else {
            String groupname = LevelGroupManager.getInstance().getLevelGroup(group).name;
            userTable.add(new Label(groupname, skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
            userTable.row().expand();
            
            //show passed level 
            userTable.add(new Label(I18n.t("LabelReachedLevel"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
            String levelname = LevelGroupManager.getInstance().getLevelGroup(group).getLevelName(level);
            userTable.add(new Label(levelname, skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        }
        userTable.row().expand();
        
        
        // show delete user and edit user name button
        userTable.add(editUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.add(deleteUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
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
        userTable.add(newUserField).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        userTable.add(addUserButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
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
                    TextButton concelButton = new TextButton(I18n.t("buttenText.cancel"), skin);
                    
                    dialog = new Dialog("", skin, "dialog") {
                        protected void result(Object object) {
                            if (object.toString().equals(YES)) {
                                PlayerProfileManager.getInstance().deletePlayerProfile();
                                generateUserTable();
                            }
                        }
                    };
                    dialog.text(I18n.t("msgDeleteUser"), dialogLabelStyle);
                    dialog.button(button, YES).add();
                    dialog.button(concelButton, CANCEL);
                    dialog.key(Keys.ENTER, YES).key(Keys.ESCAPE, CANCEL);
                    dialog.show(stage);
                }
            }
        });
        
		editUserButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				final Skin skin = SkinManager.getInstance().getSkin();
				final PlayerProfile player = PlayerProfileManager.getInstance().getCurrentPlayerProfile();

				EditUserDialog dialog = new EditUserDialog("", skin, "dialog", player.getName()) {
					@Override
					protected void result(Object object) {
						super.result(object);
						if (object.toString().equals(YES)) {
							if (!newUserName.equals(player.getName())) {
								Dialog retdialog = new Dialog("", skin, "dialog");
								boolean userExists = false;
								if (!"".equals(newUserName)) {
									List<PlayerProfile> allPlayerProfiles = PlayerProfileManager.getInstance().getAllPlayerProfiles();
									int numberOfAllPlayerProfiles = allPlayerProfiles.size();
									PlayerProfile playerProfile;
									for (int i = 0; i < numberOfAllPlayerProfiles; i++) {
										playerProfile = allPlayerProfiles.get(i);
										if (playerProfile.getName().equals(newUserName)) {
											userExists = true;
											i = numberOfAllPlayerProfiles;
										}
									}
									if (userExists) {
										retdialog.text(I18n.t("UserExists"), dialogLabelStyle);
									} else {
										// update player profile
										player.setName(newUserName);
										PlayerProfileManager.getInstance().updateIntColumn(player, "name", newUserName);
										updateUserTable();
										// set dialog text
										retdialog.text(I18n.t("userChanged"), dialogLabelStyle);
									}
								} else {
									retdialog.text(I18n.t("NullUserName"), dialogLabelStyle);
								}

								TextButton button = new TextButton(I18n.t("ok"), skin);
								retdialog.button(button);
								retdialog.show(stage);
							}
						}
					}
				};
				dialog.show(stage);
			}
		});
	}

	public class EditUserDialog extends Dialog {

		private TextField newUserNameField;
		private Skin skin;

		public String newUserName = "";
		public String oldName;

		public EditUserDialog(String title, Skin skin1, String windowStyleName, String oldName) {
			super(title, skin1, windowStyleName);
			this.skin = skin1;
			this.oldName = oldName;

			this.text(I18n.t("msgInputNewUsername"), dialogLabelStyle);
			this.row().expand();
			newUserNameField = new TextField("", skin);
			newUserNameField.setTextFieldFilter(new UserNameTextFieldFilter());
			newUserNameField.setTextFieldListener(new TextFieldListener() {
				@Override
				public void keyTyped(TextField textField, char key) {
					if ((key == '\r' || key == '\n')) {
						newUserName = newUserNameField.getText();
					}
				}
			});
			newUserNameField.setMessageText(oldName);
			this.add(newUserNameField).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).colspan(3);
			this.row().expand();

			TextButton okbutton = new TextButton(I18n.t("ok"), skin);
			TextButton concelButton = new TextButton(I18n.t("buttenText.cancel"), skin);
			button(okbutton, true).add();
			button(concelButton, false);
			key(Keys.ENTER, true).key(Keys.ESCAPE, false);
		}

		@Override
		protected void result(Object object) {
			super.result(object);
			if (object.toString().equals(YES)) {
				newUserName = newUserNameField.getText();
			}
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
        dialog.button(button);
        dialog.show(stage);
    }
    
    @Override
    public void hide() {
        super.hide();
        userList.hideList();
    }
    
    @Override
    public void show() {
        super.show();
        generateUserTable();
    }
}
