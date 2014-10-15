package de.fau.cs.mad.fly.ui.screens;

import de.fau.cs.mad.fly.profile.PlayerProfileManager;

public class AddNewPlayerScreen extends InputScreen {

    public AddNewPlayerScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
    }
    
    /**
     * Save the new user name and go back to the previous screen.
     */
    protected void validInputAndGoBack() {
        PlayerProfileManager.getInstance().addNewPlayerProfile(textField.getText());
        textField.setText("");
        goBackToPreviousScreen();
    }
    
}
