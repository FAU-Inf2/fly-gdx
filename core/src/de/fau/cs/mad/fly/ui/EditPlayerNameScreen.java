package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

/**
 * Dialog that is displayed, when pressing the button to edit the player name.
 * 
 * @author Qufang Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class EditPlayerNameScreen extends BasicScreen {
    
    private TextField newUserNameField;
    
    private Button cancelButton;
    private PlayerProfile playerProfile;
    private PlayerScreen playerScreen;
    private final float padding = 40;
    
    public EditPlayerNameScreen(final PlayerScreen playerScreen, final PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
        this.playerScreen = playerScreen;
    }
    
    public void generateContent() {
        
        Skin skin = SkinManager.getInstance().getSkin();
        
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        //outerTable.pad(padding);
        
        Table contentTable = new Table();
        contentTable.setBackground(new NinePatchDrawable(skin.get("button-up", NinePatch.class)));
        
        final Button okButton = new TextButton(I18n.t("ok"), skin);
        
        Label label = new Label(I18n.t("msgInputNewUsername") + ":", skin);
        label.setAlignment(Align.center);
        contentTable.add(label);
        
        newUserNameField = new TextField("", skin);
        newUserNameField.setTextFieldFilter(new UserNameTextFieldFilter());
        contentTable.add(newUserNameField).width(PlayerScreen.MAX_NAME_WIDTH).pad(padding);
        newUserNameField.setTextFieldListener(new TextFieldListener() {
            
            List<PlayerProfile> allPlayerProfiles = PlayerProfileManager.getInstance().getAllPlayerProfiles();
            int numberOfAllPlayerProfiles = allPlayerProfiles.size();
            
            @Override
            public void keyTyped(TextField textField, char key) {
                String newUserName = textField.getText();
                
                if (!"".equals(newUserName)) {
                    // check if user already exists
                    final String oldName = playerProfile.getName();
                    boolean nameExists = oldName.equals(newUserName);
                    if (!nameExists) {
                        for (int i = 0; i < numberOfAllPlayerProfiles; i++) {
                            if (allPlayerProfiles.get(i).getName().equals(newUserName)) {
                                nameExists = true;
                                i = numberOfAllPlayerProfiles;
                            }
                        }
                    }
                    okButton.setDisabled(nameExists);
                    
                    if (!nameExists && (key == '\r' || key == '\n')) {
                        updateUserNameAndCloseDialog();
                    }
                } else {
                    okButton.setDisabled(true);
                }
            }
        });
        
        contentTable.row();
        okButton.setDisabled(true);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!okButton.isDisabled()) {
                    updateUserNameAndCloseDialog();
                }
            }
        });
        cancelButton = new TextButton(I18n.t("cancel"), skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goBackToPreviousScreen();
            }
        });
        contentTable.add(okButton).pad(padding).expand();
        contentTable.add(cancelButton).pad(padding).expand();
        
        newUserNameField.getOnscreenKeyboard().show(true);
        stage.setKeyboardFocus(newUserNameField);
        outerTable.add(contentTable).pad(padding).expand().top();
        stage.addActor(outerTable);
    }
    
    /**
     * Save the new user name and go back to the previous screen.
     */
    protected void updateUserNameAndCloseDialog() {
        String newUserName = newUserNameField.getText();
        playerProfile.setName(newUserName);
        PlayerProfileManager.getInstance().updateIntColumn(playerProfile, "name", newUserName);
        playerScreen.updateUserTable();
        goBackToPreviousScreen();
    }
    
    /**
     * Go back to the previous screen.
     */
    private void goBackToPreviousScreen() {
        newUserNameField.getOnscreenKeyboard().show(false);
        ((Fly) Gdx.app.getApplicationListener()).setScreen(playerScreen);
    }
    
}