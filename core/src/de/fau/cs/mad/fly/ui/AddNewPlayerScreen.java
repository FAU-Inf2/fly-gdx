package de.fau.cs.mad.fly.ui;

import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

public class AddNewPlayerScreen extends InputScreen {

    public AddNewPlayerScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
    }
    
    /**
     * Save the new user name and go back to the previous screen.
     */
    protected void validInputAndGoBack() {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setName(textField.getText());
        PlayerProfileManager profileManager = PlayerProfileManager.getInstance();
        profileManager.setCurrentPlayer(playerProfile);
        profileManager.savePlayer(playerProfile);
        textField.setText("");
        goBackToPreviousScreen();
    }
    
}
