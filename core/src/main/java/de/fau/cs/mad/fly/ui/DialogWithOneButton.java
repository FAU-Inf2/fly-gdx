package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class DialogWithOneButton extends Dialog {
    
    /**
     * this object is passed to {@link #result(Object)} when the button is
     * pressed
     */
    public static final String FIRST_BUTTON = "ok";
    
    public DialogWithOneButton(String text, String buttonText) {
        super("", SkinManager.getInstance().getSkin(), "dialog");
        super.text(text).pad(UI.Dialogs.PADDING);
        TextButton button = new TextButton(buttonText, SkinManager.getInstance().getSkin());
        super.getButtonTable().add(button).pad(UI.Dialogs.PADDING, UI.Dialogs.PADDING, 0, UI.Dialogs.PADDING);
        super.setObject(button, FIRST_BUTTON);
        super.key(Keys.ENTER, FIRST_BUTTON);
    }
    
    protected void result(Object object) {
        super.hide();
    }
}
