package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.fau.cs.mad.fly.I18n;

public class DialogWithOkButton extends Dialog{

    public static final String OK = "ok";
    
    public DialogWithOkButton(String i18nKey) {
        super("", SkinManager.getInstance().getSkin(), "dialog");      
        super.text(I18n.t(i18nKey)).pad(UI.Dialogs.PADDING);
        TextButton button = new TextButton(I18n.t("ok"), SkinManager.getInstance().getSkin());
        super.button(button, OK).key(Keys.ENTER, OK).key(Keys.ESCAPE, OK).pad(UI.Dialogs.PADDING);
    }
    
    protected void result(Object object) {
        super.hide();
    }
}
