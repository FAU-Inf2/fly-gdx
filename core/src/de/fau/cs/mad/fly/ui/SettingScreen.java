package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.ISetting;
import de.fau.cs.mad.fly.settings.SettingManager;
import de.fau.cs.mad.fly.ui.help.HelpFrameTextWithArrow;
import de.fau.cs.mad.fly.ui.help.HelpOverlay;
import de.fau.cs.mad.fly.ui.help.WithHelpOverlay;

/**
 * Displays and changes the options of the game.
 * 
 * @author Tobias Zangl
 */
public class SettingScreen extends BasicScreen implements WithHelpOverlay {
    private SettingManager settingManager;
    private float padding = 50;
    private Table settingTable;
    private String displayPlayer = "";
    private boolean showHelpScreen = false;
    private HelpOverlay helpOverlay;
    private boolean helpScreenCreated = false;
    
    @Override
    protected void generateContent() {
        settingTable = new Table();
        
        Table table = new Table();
        table.setFillParent(true);
        table.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
        table.add(settingTable).center();
        stage.addActor(table);
        
        // setup help overlay
        final Button helpButton = new ImageButton(skin.get(UI.Buttons.HELP_BUTTON_STYLE, ImageButtonStyle.class));
        helpButton.setBounds(UI.Window.BORDER_SPACE, viewport.getWorldHeight() - UI.Window.BORDER_SPACE - UI.Buttons.MAIN_BUTTON_HEIGHT, UI.Buttons.MAIN_BUTTON_HEIGHT, UI.Buttons.MAIN_BUTTON_HEIGHT);
        
        this.helpOverlay = new HelpOverlay(this);
        helpButton.addListener(helpOverlay);
        showHelpScreen = false;
        stage.addActor(helpButton);
    }
    
    private void generateContentDynamic() {
        PlayerProfile playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        if (displayPlayer == null || (!displayPlayer.equals(playerProfile.getName()))) {
            ISetting setting;
            settingTable.clear();
            settingManager = playerProfile.getSettingManager();
            for (String s : settingManager.getSettingList()) {
                settingTable.row().expand();
                setting = settingManager.getSettingMap().get(s);
                if (!helpScreenCreated) {
                    helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, setting.getHelpingText(), setting.getActor()));
                }
                settingTable.add(setting.getLabel()).right().pad(padding);
                settingTable.add(setting.getActor()).pad(padding, 3 * padding, padding, padding);
            }
            displayPlayer = playerProfile.getName();
        }
        helpScreenCreated = true;
    }
    
    @Override
    public void show() {
        super.show();
        generateContentDynamic();
    }
    
    @Override
    public void render(float delta) {
        super.render(delta);
        if (showHelpScreen) {
            helpOverlay.render();
        }
    }
    
    @Override
    public void startHelp() {
        showHelpScreen = true;
        Gdx.input.setInputProcessor(helpOverlay);
        
    }
    
    @Override
    public void endHelp() {
        showHelpScreen = false;
        Gdx.input.setInputProcessor(inputProcessor);
    }
}