package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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
    
    private Button addPlayerButton;
    private Table contentTable;
    
    private Button deletePlayerButton;
    private Button editPlayerNameButton;
    
    private int selectedUserindex = 0;
    private SelectBox<String> userSelectBox;
    
    private final float padding = 50f;
    
    private BasicScreen addNewPlayerScreen;
    private BasicScreen editPlayerNameScreen;
    
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

        contentTable.add(new Label(I18n.t("playerNameLableText") + ":", skin)).pad(padding);
        
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
        contentTable.add(userSelectBox).width(MAX_NAME_WIDTH);
        
        // add button to delete the current player
        deletePlayerButton = new ImageButton(skin, "trash");
        deletePlayerButton.addListener(new ClickListener() {
            Dialog dialog;
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playerProfileManager.getAllPlayerProfiles().size() <= 1) {
                    dialog = new DialogWithOneButton(I18n.t("msgLastPlayer"), I18n.t("ok"));
                } else {
                    StringBuilder text = new StringBuilder();
                    text.append(I18n.t("msgDeletePlayer1")).append(" ");
                    text.append(playerProfileManager.getCurrentPlayerProfile().getName());
                    text.append(I18n.t("msgDeletePlayer2")).append("?");
                    dialog = new DialogWithOkAndCancelButton(text.toString(), I18n.t("yes"), I18n.t("no")) {
                        protected void result(Object object) {
                            if (DialogWithOneButton.FIRST_BUTTON.equals(object.toString())) {
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
        addPlayerButton = new TextButton(I18n.t("addPlayerButtonText"), skin);
        addPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToAddNewPlayerScreen();
            }
        });
        contentTable.add(addPlayerButton).pad(padding);
        
        // add button to edit the current player name
        editPlayerNameButton = new TextButton(I18n.t("button.editPlayer"), skin);
        editPlayerNameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToEditPlayerNameScreen();
            }
        });
        contentTable.add(editPlayerNameButton).pad(padding);
        contentTable.row();
        
        PlayerProfile playerProfile = playerProfileManager.getCurrentPlayerProfile();
        
        // show total score
        contentTable.add(new Label(I18n.t("labelTotalScore"), skin)).pad(padding);
        contentTable.add(new Label("" + playerProfile.getMoney(), skin)).pad(padding);
        // TODO: do not use money for that!
        contentTable.row();
        
        // show passed group and level
        addLastLevel(playerProfile, skin);
        
        outerTable.add(contentTable).pad(UI.Window.BORDER_SPACE);
        stage.addActor(outerTable);
    }
    
    
    protected void switchToAddNewPlayerScreen() {
        if(addNewPlayerScreen == null) {
            addNewPlayerScreen = new AddNewPlayerScreen(this);
        }
        addNewPlayerScreen.set();
    }
    
    protected void switchToEditPlayerNameScreen() {
        if(editPlayerNameScreen == null) {
            editPlayerNameScreen = new EditPlayerNameScreen(this);
        }
        editPlayerNameScreen.set();
    }
    
    /**
     * Adds a label that describes the last level group and the last level.
     * 
     * @param playerProfile
     * @param skin
     */
    private void addLastLevel(PlayerProfile playerProfile, Skin skin) {
        contentTable.add(new Label(I18n.t("lastLevel") + ":", skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
        
        int group = playerProfile.getPassedLevelgroupID();
        int level = playerProfile.getPassedLevelID();
        if (level > LevelGroupManager.getInstance().getLastGroup().getLastLevelProfile().id) {
            contentTable.add(new Label(I18n.t("ALLGroupPassed"), skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
        } else {
            contentTable.add(new Label("" + group + " - " + level, skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
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
