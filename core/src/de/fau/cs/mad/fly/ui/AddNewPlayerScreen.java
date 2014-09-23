package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

public class AddNewPlayerScreen extends InputScreen {

    public AddNewPlayerScreen(BasicScreen screenToGoBack) {
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
                            addUserNameAndCloseDialog();
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
                        dialog = new DialogWithOkButton("NullUserName");
                    } else {
                        dialog = new DialogWithOkButton("UserExists");
                    }
                    dialog.show(stage);
                } else {
                    addUserNameAndCloseDialog();
                }
            }
        });
        
    }
    
    /**
     * Save the new user name and go back to the previous screen.
     */
    protected void addUserNameAndCloseDialog() {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setName(textField.getText());
        PlayerProfileManager profileManager = PlayerProfileManager.getInstance();
        profileManager.setCurrentPlayer(playerProfile);
        profileManager.savePlayer(playerProfile);
        textField.setText("");
        goBackToPreviousScreen();
    }
    
}
