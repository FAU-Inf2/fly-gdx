package de.fau.cs.mad.fly.ui.screens;

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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.ChangeListener;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.DialogWithOkAndCancelButton;
import de.fau.cs.mad.fly.ui.DialogWithOneButton;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Screen to see your user, to switch the user and to add a new user. Is called
 * from the {@link MainMenuScreen}.
 * 
 * 
 * @author Qufang Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class PlayerScreen extends BasicScreenWithBackButton {
    
    private Button addPlayerButton;
    private Table playerTable;
    
    private Button deletePlayerButton;
    private Button editPlayerNameButton;
    
    private int selectedUserindex = 0;
    private SelectBox<String> userSelectBox;
    
    private final float padding = 50f;
    
    private BasicScreen addNewPlayerScreen;
    private BasicScreen editPlayerNameScreen;
    
    private Label totalScoreValueLabel;
    private Label lastLevelValueLabel;
    private Label totalMoneyValueLabel;
    private Label flyIDLabel;
    
    public final static int MAX_NAME_WIDTH = 1650;
    
    public PlayerScreen(BasicScreen screenToReturn) {
        super(screenToReturn);
    }
    
    /**
     * Method that is called, if the list of user changes. It updates the user
     * table and sets its selection to the given {@link PlayerProfile}.
     */
    public void updateUserTable(PlayerProfile newPlayerProfile) {
        String currentUserName = newPlayerProfile.getName();
        Array<String> nameList = new Array<String>();
        List<PlayerProfile> playerList = PlayerProfileManager.getInstance().getAllPlayerProfiles();
        String name;
        for (int i = 0; i < playerList.size(); i++) {
            name = playerList.get(i).getName();
            nameList.add(name);
            if (currentUserName.equals(name)) {
                selectedUserindex = i;
            }
        }
        userSelectBox.setItems(nameList);
        userSelectBox.setSelectedIndex(selectedUserindex);
    }
    
    /**
     * generate Content here two Tables are used to show the content: userTable
     * and levelgroupTable
     */
    @Override
    protected void generateContent() {
        generateBackButton();
        Skin skin = SkinManager.getInstance().getSkin();
        final PlayerProfileManager playerProfileManager = PlayerProfileManager.getInstance();
        playerTable = new Table();
        
        playerTable.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
        
        playerTable.add(new Label(I18n.t("playerNameLableText") + ":", skin)).pad(padding).right();
        
        // add all users to userList and set the current user to display value
        userSelectBox = new SelectBox<String>(skin);
        updateUserTable(playerProfileManager.getCurrentPlayerProfile());
        userSelectBox.getList().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userSelectBox.getSelectedIndex() >= 0 && userSelectBox.getSelectedIndex() != selectedUserindex) {
                    selectedUserindex = userSelectBox.getSelectedIndex();
                    PlayerProfile newPlayerProfile = playerProfileManager.getAllPlayerProfiles().get(selectedUserindex);
                    playerProfileManager.setCurrentPlayer(newPlayerProfile);
                }
            }
        });
        playerProfileManager.addPlayerChangedListener(new ChangeListener<PlayerProfile>() {
            
            @Override
            public void changed(PlayerProfile newPlayerProfile) {
                updateUserTable(newPlayerProfile);
            }
            
        });
        playerTable.add(userSelectBox).width(MAX_NAME_WIDTH);
        playerTable.row();
        
        playerTable.add();
        Table imageButtonTable = new Table();
        
        // add button to add a new player
        addPlayerButton = new ImageButton(skin, "cross");
        addPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToAddNewPlayerScreen();
            }
        });
        imageButtonTable.add(addPlayerButton).pad(padding, 0, padding, 0);
        
        // add button to edit the current player name
        editPlayerNameButton = new ImageButton(skin, "editButton");
        editPlayerNameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToEditPlayerNameScreen();
            }
        });
        imageButtonTable.add(editPlayerNameButton).pad(padding, 0, padding, 0).expand();
        
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
                            }
                        }
                    };
                }
                dialog.show(stage);
            }
        });
        imageButtonTable.add(deletePlayerButton).pad(padding, 0, padding, 0);
        playerTable.add(imageButtonTable).width(MAX_NAME_WIDTH);
        playerTable.row();
        
        PlayerProfile playerProfile = playerProfileManager.getCurrentPlayerProfile();
        
        //show fly id
       // playerTable.add(new Label(I18n.t("labelFlyId") + ":", skin)).pad(padding).right();
        flyIDLabel = new Label("" + playerProfile.getFlyID(), skin);
        //playerTable.add(flyIDLabel).pad(padding);
        //playerTable.row();
        
        // show total score
        playerTable.add(new Label(I18n.t("labelTotalScore"), skin)).pad(padding).right();
        totalScoreValueLabel = new Label("" + playerProfile.getTotalScoreOfAll(), skin);
        playerTable.add(totalScoreValueLabel).pad(padding);
        playerProfileManager.addPlayerChangedListener(new ChangeListener<PlayerProfile>() {
            
            @Override
            public void changed(PlayerProfile newPlayerProfile) {
            	updateLabels();
            }
            
        });
        playerTable.row();
        
        //show money
        playerTable.add(new Label(I18n.t("gainMoney") + ":", skin)).pad(padding).right();
        totalMoneyValueLabel = new Label("" + playerProfile.getMoney(), skin);
        playerTable.add(totalMoneyValueLabel).pad(padding);
        playerTable.row();
        
        // show passed group and level
        addLastLevel(playerProfile, skin);
        
        contentTable.add(playerTable);
    }
    
    protected void switchToAddNewPlayerScreen() {
        if (addNewPlayerScreen == null) {
            addNewPlayerScreen = new AddNewPlayerScreen(this);
        }
        addNewPlayerScreen.set();
    }
    
    protected void switchToEditPlayerNameScreen() {
        if (editPlayerNameScreen == null) {
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
        playerTable.add(new Label(I18n.t("lastLevel") + ":", skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(padding).right();
        lastLevelValueLabel = new Label("", skin);
        playerTable.add(lastLevelValueLabel);
        updateLastLevelGroup(playerProfile);
        playerTable.row();
    }
    
    private void updateLastLevelGroup(PlayerProfile playerProfile) {
        int group = playerProfile.getPassedLevelgroupID();
        int level = playerProfile.getPassedLevelID();
        if (group >= LevelGroupManager.getInstance().getLastGroup().id && level > LevelGroupManager.getInstance().getLastGroup().getLastLevelProfile().id) {
            
            lastLevelValueLabel.setText(I18n.t("ALLGroupPassed"));
        } else {
            lastLevelValueLabel.setText("" + group + " - " + playerProfile.getCurrentLevelProfile().name);
        }
    }
    
    @Override
    public void hide() {
        userSelectBox.hideList();
    }
    
    @Override
    public void show() {
        super.show();
        updateLabels();
    }
    
    private void updateLabels(){
    
    	 if (totalScoreValueLabel != null) {
             PlayerProfile currentPlayerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
             totalScoreValueLabel.setText(String.valueOf(currentPlayerProfile.getTotalScoreOfAll()));
             totalMoneyValueLabel.setText(currentPlayerProfile.getMoney() + "");
             flyIDLabel.setText(currentPlayerProfile.getFlyID() + "");
             updateLastLevelGroup(currentPlayerProfile);
         }
         updateUserTable(PlayerProfileManager.getInstance().getCurrentPlayerProfile());
    }
}
