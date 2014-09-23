package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.fau.cs.mad.fly.I18n;

public class DialogWithOkAndCancelButton extends DialogWithOkButton {
    
    public static final String CANCEL = "cancel";
    
    public DialogWithOkAndCancelButton(String i18nKey) {
        super(i18nKey);
        TextButton cancelButton = new TextButton(I18n.t("cancel"), SkinManager.getInstance().getSkin());
        super.button(cancelButton, CANCEL).key(Keys.ESCAPE, CANCEL).pad(UI.Dialogs.PADDING);
    }
    
}
