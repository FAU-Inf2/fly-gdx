package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class DialogWithOkAndCancelButton extends DialogWithOneButton {
    
    /**
     * Object that is passed to {@link #result(Object)} when second button is
     * pressed
     */
    public static final String SECOND_BUTTON = "cancel";
    
    public DialogWithOkAndCancelButton(String text, String button1Text, String button2Text) {
        super(text, button1Text);
        TextButton button2 = new TextButton(button2Text, SkinManager.getInstance().getSkin());
        super.getButtonTable().add(button2).pad(UI.Dialogs.PADDING);
        super.setObject(button2, SECOND_BUTTON);
        super.key(Keys.ESCAPE, SECOND_BUTTON);
    }
    
}
