package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
    
    private TextButton addPlayerButton;
    private Table contentTable;
    
    private ImageButton deletePlayerButton;
    private TextButton editPlayerNameButton;
    
    private int selectedUserindex = 0;
    private SelectBox<String> userSelectBox;
    
    private float padding = 80f;
    
    final BasicScreen addNewPlayerScreen = new AddNewPlayerScreen(this);
    final BasicScreen editPlayerNameScreen = new EditPlayerNameScreen(this);
    
    public final static int MAX_NAME_WIDTH = 1650;
    
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
        final PlayerProfileManager playerProfileManager = PlayerProfileManager.getInstance();
        
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        contentTable = new Table();
        
        contentTable.setBackground(new NinePatchDrawable(skin.get("button-up", NinePatch.class)));
        
        contentTable.add(new Label(I18n.t("usernameLableText") + ":", skin));
        
        // add all users to userList and set the current user to display value
        userSelectBox = new SelectBox<String>(skin);
        updateUserTable();
        userSelectBox.getList().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userSelectBox.getSelectedIndex() >= 0 && userSelectBox.getSelectedIndex() != selectedUserindex) {
                    selectedUserindex = userSelectBox.getSelectedIndex();
                    PlayerProfile newPlayerProfile = playerProfileManager.getAllPlayerProfiles().get(selectedUserindex);
                    playerProfileManager.setCurrentPlayer(newPlayerProfile);
                    updateUserTable();
                }
            }
        });
        contentTable.add(userSelectBox);
        
        // add button to delete the current player
        deletePlayerButton = new ImageButton(skin, "trash");
        deletePlayerButton.addListener(new ClickListener() {
            Dialog dialog;
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playerProfileManager.getAllPlayerProfiles().size() <= 1) {
                    dialog = new DialogWithOkButton("msgLastUser");
                } else {
                    dialog = new DialogWithOkAndCancelButton("msgDeleteUser") {
                        protected void result(Object object) {
                            if (DialogWithOkButton.OK.equals(object.toString())) {
                                playerProfileManager.deletePlayerProfile();
                                updateUserTable();
                            }
                        }
                    };
                }
                dialog.show(stage);
            }
        });
        contentTable.add(deletePlayerButton).pad(padding);
        contentTable.row().expand();
        
        // add button to add a new player
        addPlayerButton = new TextButton(I18n.t("addUserButtonText"), skin);
        addPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addNewPlayerScreen.set();
            }
        });
        contentTable.add(addPlayerButton);
        
        // add button to edit the current player name
        editPlayerNameButton = new TextButton(I18n.t("button.editUser"), skin);
        editPlayerNameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editPlayerNameScreen.set();
            }
        });
        contentTable.add(editPlayerNameButton);
        contentTable.row();
        
        // show fly id, if no fly id, show a info button
        contentTable.add(new Label(I18n.t("labelFlyId") + ":", skin)).pad(padding);
        if (playerProfileManager.getCurrentPlayerProfile().getFlyID() > 0) {
            contentTable.add(new Label(playerProfileManager.getCurrentPlayerProfile().getFlyID() + "", skin)).pad(padding);
        } else {
            ImageButton infoButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
            infoButton.addListener(new ClickListener() {
                Dialog dialog = new DialogWithOkButton("msgGetFlyId");
                
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    dialog.show(stage);
                }
            });
            contentTable.add(infoButton).pad(padding);
        }
        contentTable.row();
        
        PlayerProfile playerProfile = playerProfileManager.getCurrentPlayerProfile();
        
        // show total score
        contentTable.add(new Label(I18n.t("labelTotalScore"), skin)).pad(padding);
        contentTable.add(new Label("" + playerProfile.getMoney(), skin)).pad(padding);
        // TODO: do not use money for that!
        contentTable.row();
        
        // show passed group and level
        addLastLevel(playerProfile, skin);
        
        outerTable.add(contentTable).pad(UI.Window.BORDER_SPACE).expand();
        stage.addActor(outerTable);
    }
    
    /**
     * Adds a label that describes the last level group and the last level.
     * 
     * @param playerProfile
     * @param skin
     */
    private void addLastLevel(PlayerProfile playerProfile, Skin skin) {
        contentTable.add(new Label(I18n.t("lastLevel") + ":", skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        
        int group = playerProfile.getPassedLevelgroupID();
        int level = playerProfile.getPassedLevelID();
        if (level > LevelGroupManager.getInstance().getLastGroup().getLastLevelProfile().id) {
            contentTable.add(new Label(I18n.t("ALLGroupPassed"), skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        } else {
            contentTable.add(new Label("" + group + " - " + level, skin)).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        }
        contentTable.row();
    }
    
    @Override
    public void hide() {
        userSelectBox.hideList();
    }
    
    @Override
    public void show() {
        super.show();
        updateUserTable();
    }
}
