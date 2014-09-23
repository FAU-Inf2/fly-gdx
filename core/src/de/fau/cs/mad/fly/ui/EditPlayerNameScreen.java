package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

/**
 * Screen that is displayed, when pressing the button to edit the player name.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class EditPlayerNameScreen extends InputScreen {
    
    public EditPlayerNameScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
    }

    public void generateContent() {
        
        super.generateContent();
        
        label.setText(I18n.t("msgInputNewPlayerName"));
        
        textField.setTextFieldListener(new TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if (!"".equals(textField.getText())) {
                    if (doesNameExist()) {
                        okButton.setDisabled(true);
                    } else {
                        okButton.setDisabled(false);
                        if (key == '\r' || key == '\n') {
                            updateUserNameAndCloseDialog();
                        }
                    }
                } else {
                    okButton.setDisabled(true);
                }
            }
        });
        
        okButton.addListener(new ClickListener() {
            Dialog dialog;
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (okButton.isDisabled()) {
                    if ("".equals(textField.getText())) {
                        dialog = new DialogWithOkButton("NullPlayerName");
                    } else {
                        dialog = new DialogWithOkButton("playerExists");
                    }
                    dialog.show(stage);
                } else {
                    updateUserNameAndCloseDialog();
                }
            }
        });
        
    }
    
    /**
     * Save the new user name and go back to the previous screen.
     */
    protected void updateUserNameAndCloseDialog() {
        String newUserName = textField.getText();
        
        PlayerProfile playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        playerProfile.setName(newUserName);
        PlayerProfileManager.getInstance().updateIntColumn(playerProfile, "name", newUserName);
        goBackToPreviousScreen();
    }
    
}