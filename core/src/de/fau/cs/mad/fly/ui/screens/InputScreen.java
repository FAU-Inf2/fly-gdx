package de.fau.cs.mad.fly.ui.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.DialogWithOneButton;
import de.fau.cs.mad.fly.ui.GenericBackProcessor;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UserNameTextFieldFilter;

public abstract class InputScreen extends BasicScreen {
    
    protected static final float PADDING = 40;
    
    protected Label label;
    protected TextField textField;
    protected Button okButton;
    protected Button cancelButton;
    
    private boolean waitForNextKey = false;
    
    protected final BasicScreen screenToGoBack;
    
    public InputScreen(BasicScreen screenToGoBack) {
        this.screenToGoBack = screenToGoBack;
        inputProcessor = new InputMultiplexer(new GenericBackProcessor(screenToGoBack), stage);
    }
    
    public void generateContent() {
        
        Skin skin = SkinManager.getInstance().getSkin();
        
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        
        Table contentTable = new Table();
        contentTable.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
        
        label = new Label(I18n.t("msgInputNewPlayerName"), skin);
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
        
        outerTable.add(contentTable).pad(PADDING).expand().top();
        stage.addActor(outerTable);
        
        textField.setTextFieldListener(new TextFieldListener() {
            @Override
            public void keyTyped(final TextField textField, char key) {
                
                if (key == '\r' || key == '\n') {
                    if (waitForNextKey) {
                        waitForNextKey = false;
                    } else {
                        checkInput();
                    }
                } else {
                    if (waitForNextKey) {
                        waitForNextKey = false;
                    }
                    if (!"".equals(textField.getText())) {
                        if (doesNameExist()) {
                            okButton.setDisabled(true);
                        } else {
                            okButton.setDisabled(false);
                        }
                    } else {
                        okButton.setDisabled(true);
                    }
                }
            }
        });
        
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                checkInput();
            }
        });
    }
    
    /**
     * Checks if a name is entered, otherwise a dialog with a corresponding
     * message is shown. Furthermore it is checked weather the player name
     * already exists. If so a corresponding message is shown.
     */
    private void checkInput() {
        if (okButton.isDisabled()) {
            Dialog dialog;
            if ("".equals(textField.getText())) {
                dialog = new DialogWithOneButton(I18n.t("NullPlayerName"), I18n.t("ok")) {
                    @Override
                    protected void result(Object object) {
                        super.result(object);
                        if (object == DialogWithOneButton.FIRST_BUTTON) {
                            stage.setKeyboardFocus(null);
                            textField.getOnscreenKeyboard().show(true);
                            waitForNextKey = true;
                            stage.setKeyboardFocus(textField);
                        }
                    }
                };
            } else {
                dialog = new DialogWithOneButton(I18n.t("playerExists"), I18n.t("ok")) {
                    @Override
                    protected void result(Object object) {
                        super.result(object);
                        if (object == DialogWithOneButton.FIRST_BUTTON) {
                            stage.setKeyboardFocus(null);
                            textField.getOnscreenKeyboard().show(true);
                            waitForNextKey = true;
                            stage.setKeyboardFocus(textField);
                        }
                    }
                };
            }
            dialog.show(stage);
            textField.getOnscreenKeyboard().show(false);
        } else {
            validInputAndGoBack();
        }
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
        stage.setKeyboardFocus(null);
        textField.setText("");
        ((Fly) Gdx.app.getApplicationListener()).setScreen(screenToGoBack);
    }
    
    @Override
    public void show() {
        super.show();
        waitForNextKey = false;
        textField.getOnscreenKeyboard().show(true);
        okButton.setDisabled(true);
        stage.setKeyboardFocus(textField);
    }
    
    /**
     * This method is called, when a valid user name is in the textfield and
     * either enter or ok is pressed.
     */
    abstract protected void validInputAndGoBack();
    
}