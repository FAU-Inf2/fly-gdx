package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

public class InputScreen extends BasicScreen {
    
    protected static final float PADDING = 40;
    
    protected Label label;
    protected TextField textField;
    protected Button okButton;
    protected Button cancelButton;
    
    private final BasicScreen screenToGoBack;
    
    public InputScreen(BasicScreen screenToGoBack) {
        this.screenToGoBack = screenToGoBack;
    }
    
    public void generateContent() {
        
        Skin skin = SkinManager.getInstance().getSkin();
        
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        
        Table contentTable = new Table();
        contentTable.setBackground(new NinePatchDrawable(skin.get("button-up", NinePatch.class)));
        
        label = new Label("", skin);
        contentTable.add(label);
        
        textField = new TextField("", skin);
        textField.setTextFieldFilter(new UserNameTextFieldFilter());
        contentTable.add(textField).width(PlayerScreen.MAX_NAME_WIDTH).pad(PADDING);
        
        contentTable.row();
        
        okButton = new TextButton(I18n.t("ok"), skin);
        okButton.setDisabled(true);
        contentTable.add(okButton).pad(PADDING);
        
        cancelButton = new TextButton(I18n.t("cancel"), skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goBackToPreviousScreen();
            }
        });
        contentTable.add(cancelButton).pad(PADDING);
        
        textField.getOnscreenKeyboard().show(true);
        stage.setKeyboardFocus(textField);
        outerTable.add(contentTable).pad(PADDING).expand().top();
        stage.addActor(outerTable);
    }
    
    protected boolean doesNameExist() {
        List<PlayerProfile> allPlayerProfiles = PlayerProfileManager.getInstance().getAllPlayerProfiles();
        int numberOfAllPlayerProfiles = allPlayerProfiles.size();
        final String newName = textField.getText();
        boolean nameExists = false;
        if (!nameExists) {
            for (int i = 0; i < numberOfAllPlayerProfiles; i++) {
                if (allPlayerProfiles.get(i).getName().equals(newName)) {
                    nameExists = true;
                    i = numberOfAllPlayerProfiles;
                }
            }
        }
        return nameExists;
    }
    
    protected void goBackToPreviousScreen() {
        textField.getOnscreenKeyboard().show(false);
        ((Fly) Gdx.app.getApplicationListener()).setScreen(screenToGoBack);
    }
    
}