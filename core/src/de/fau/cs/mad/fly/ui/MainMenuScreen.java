package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.help.HelpFrameText;
import de.fau.cs.mad.fly.ui.help.HelpFrameTextWithArrow;
import de.fau.cs.mad.fly.ui.help.HelpOverlay;
import de.fau.cs.mad.fly.ui.help.WithHelpOverlay;

/**
 * Displays the main menu with Start, Options, Help and Exit buttons.
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class MainMenuScreen extends BasicScreen implements WithHelpOverlay {
    
    private HelpOverlay helpOverlay;
    private boolean showHelpScreen;
    
    private Button continueButton;
    private Button chooseLevelButton;
    private Button choosePlaneButton;
    private Button highscoreButton;
    private Button settingsButton;
    private Button helpButton;
    private Button playerButton;
    
    private LevelGroupScreen levelGroupScreen;
    private HighscoreScreen highscoreScreen;
    
    private void setLevelGroupScreen() {
        if (levelGroupScreen == null) {
            levelGroupScreen = new LevelGroupScreen(this);
        }
        levelGroupScreen.set();
    }
    
    private void setHighscoreScreen() {
        if (highscoreScreen == null) {
            highscoreScreen = new HighscoreScreen(this);
        }
        highscoreScreen.set();
    }
    
    /**
     * Adds the main menu to the main menu screen.
     * <p>
     * Includes buttons for Start, Options, Help, Exit.
     */
    protected void generateContent() {
        // Create an instance of the PlayerManager, which needs an access to the
        // database
        super.generateContent();
        createButtonsAndPositionContent();
        createHelp();
        
        chooseLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setLevelGroupScreen();
            }
        });
        
        choosePlaneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setPlaneChoosingScreen();
                
            }
        });
        
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerProfile playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
                LevelProfile currentLevelProfile = playerProfile.getCurrentLevelProfile();
                Loader.getInstance().loadLevel(currentLevelProfile);
            }
        });
        
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setSettingScreen();
            }
        });
        
        playerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setPlayerScreen();
            }
        });
        
        highscoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setHighscoreScreen();
            }
        });
    }
    
    /**
     * Creates all the buttons and the version label and positions them.
     */
    private void createButtonsAndPositionContent() {
        Skin skin = SkinManager.getInstance().getSkin();
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        stage.addActor(outerTable);
        
        Table innerTable = new Table();
        
        continueButton = new TextButton(I18n.t("play"), skin);
        chooseLevelButton = new TextButton(I18n.t("choose.level"), skin);
        choosePlaneButton = new TextButton(I18n.t("choose.plane"), skin);
        highscoreButton = new TextButton(I18n.t("highscores"), skin);
        settingsButton = new ImageButton(skin, UI.Buttons.SETTING_BUTTON_STYLE);
        helpButton = new ImageButton(skin.get(UI.Buttons.HELP_BUTTON_STYLE, ImageButtonStyle.class));
        playerButton = new ImageButton(skin, "player");
        
        Label versionLabel = new Label(createVersion(), skin, "small");
        
        outerTable.add(helpButton).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).pad(UI.Window.BORDER_SPACE).top().left();
        
        outerTable.add(settingsButton).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).pad(UI.Window.BORDER_SPACE).top().right();
        outerTable.row();
        
        innerTable.add(continueButton).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE);
        innerTable.row();
        innerTable.add(chooseLevelButton).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE);
        innerTable.row();
        innerTable.add(choosePlaneButton).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE);
        innerTable.row();
        innerTable.add(highscoreButton).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE);
        
        outerTable.add(innerTable).colspan(2).expand();
        outerTable.row();
        outerTable.add();
        outerTable.add(playerButton).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).pad(UI.Window.BORDER_SPACE).bottom().right();
        
        Table versionTable = new Table();
        versionTable.setFillParent(true);
        stage.addActor(versionTable);
        versionTable.add(versionLabel).pad(UI.Window.BORDER_SPACE).bottom().expand();
    }
    
    /**
     * Creates the help content for each Actor that is useful for the user.
     */
    private void createHelp() {
        Skin skin = SkinManager.getInstance().getSkin();
        
        helpOverlay = new HelpOverlay(this);
        helpOverlay.addHelpFrame(new HelpFrameText(skin, "welcome"));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpPlay", continueButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpSelectLevel", chooseLevelButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpSelectShip", choosePlaneButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpHighscore", highscoreButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpSettings", settingsButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpPlayer", playerButton));
        helpOverlay.addHelpFrame(new HelpFrameText(skin, "helpEnd"));
        
        helpButton.addListener(helpOverlay);
        showHelpScreen = false;
    }
    
    /**
     * Creates the version text.
     * <p>
     * "Version: Major.Minor.Patch"
     * 
     * @return version text
     */
    private String createVersion() {
        return I18n.t("version") + ": " + Fly.VERSION;
    }
    
    @Override
    public void render(float delta) {
        super.render(delta);
        if (showHelpScreen) {
            helpOverlay.render();
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (levelGroupScreen != null) {
            levelGroupScreen.dispose();
        }
        if (highscoreScreen != null) {
            highscoreScreen.dispose();
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
