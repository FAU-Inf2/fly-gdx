package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;

/**
 * Filter for the TextField that is used to enter the user name.
 * <p>
 * Makes sure that a user name only consists of digits and alpabetic characters.
 * 
 * @author Lukas Hahmann
 * 
 */
public class UserNameTextFieldFilter implements TextFieldFilter {
    
    @Override
    public boolean acceptChar(TextField textField, char c) {
        if(textField.getText().length() >= 9) {
            return false;
        }
        else if (Character.isAlphabetic(c) || Character.isDigit(c)) {
            return true;
        } else {
            return false;
        }
    }
    
}
