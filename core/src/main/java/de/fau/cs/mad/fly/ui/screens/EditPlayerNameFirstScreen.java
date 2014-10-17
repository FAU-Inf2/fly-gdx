package de.fau.cs.mad.fly.ui.screens;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

public class EditPlayerNameFirstScreen extends InputScreen {

    private BasicScreen screenToGoWhenCancel; 
    
    public EditPlayerNameFirstScreen(BasicScreen screenToGoWhenOk, BasicScreen screenToGoWhenCancel) {
        super(screenToGoWhenOk);
        label.setText(I18n.t("editUserNameFirst"));
        this.screenToGoWhenCancel = screenToGoWhenCancel;
    }
    
    /**
     * Save the new user name and go back to the previous screen.
     */
    protected void validInputAndGoBack() {
        PlayerProfileManager.getInstance().editCurrentPlayerName(textField.getText());
        textField.getOnscreenKeyboard().show(false);
        screenToGoBack.set();
    }
    
    @Override
    protected void goBackToPreviousScreen() {
        textField.getOnscreenKeyboard().show(false);
        stage.setKeyboardFocus(null);
        textField.setText("");
        screenToGoWhenCancel.set();
    }
    
}
