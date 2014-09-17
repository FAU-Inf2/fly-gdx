package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
 * @author Tobias Zangl
 */
public class MainMenuScreen extends BasicScreen implements WithHelpOverlay {
    
    private HelpOverlay helpOverlay;
    private boolean showHelpScreen = false;
    
    /**
     * Adds the main menu to the main menu screen.
     * <p>
     * Includes buttons for Start, Options, Help, Exit.
     */
    protected void generateContent() {
        // Create an instance of the PlayerManager, which needs an access to the
        // database
        
        Table table = new Table();
        table.defaults().width(viewport.getWorldWidth() / 3);
        table.setFillParent(true);
        table.pad(UI.Window.BORDER_SPACE);
        stage.addActor(table);
        
        FloatAction test = new FloatAction();
        
        Button continueButton = new TextButton(I18n.t("play"), skin);
        continueButton.addAction(test);
        Button chooseLevelButton = new TextButton(I18n.t("choose.level"), skin);
        Button choosePlaneButton = new TextButton(I18n.t("choose.plane"), skin);
        Button statsButton = new TextButton(I18n.t("highscores"), skin);
        ImageButton settingsButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
        TextureRegion gear = skin.getRegion("gear");
        gear.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        TextureRegionDrawable drawableGear = new TextureRegionDrawable(gear);
        settingsButton.getImage().setDrawable(drawableGear);
        Button helpButton = new ImageButton(skin.get(UI.Buttons.HELP_BUTTON_STYLE, ImageButtonStyle.class));
        Button playerButton = new ImageButton(skin, "player");
        
        Label versionLabel = new Label(createVersion(), skin, "small");
        
        table.add(helpButton).width(UI.Buttons.MAIN_BUTTON_HEIGHT).height(UI.Buttons.MAIN_BUTTON_HEIGHT).left();
        table.add();
        table.add(settingsButton).width(UI.Buttons.MAIN_BUTTON_HEIGHT).height(UI.Buttons.MAIN_BUTTON_HEIGHT).right();
        table.row().expand();
        table.add();
        table.add(continueButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        table.add();
        table.row().expand();
        table.add();
        table.add(chooseLevelButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        table.add();
        table.row().expand();
        table.add();
        table.add(choosePlaneButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        table.add();
        table.row().expand();
        table.add();
        table.add(statsButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        table.add(playerButton).width(UI.Buttons.MAIN_BUTTON_HEIGHT).height(UI.Buttons.MAIN_BUTTON_HEIGHT).right();
        table.row();
        table.add();
        table.add();
        table.add(versionLabel).width(UI.Labels.MAIN_LABEL_VERSION_WIDTH);
        
        chooseLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setLevelGroupScreen();
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
        
        this.helpOverlay = new HelpOverlay(this);
        helpOverlay.addHelpFrame(new HelpFrameText(skin, "welcome"));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpPlay", continueButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpSelectLevel", chooseLevelButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpSelectShip", choosePlaneButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpHighscore", statsButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpSettings", settingsButton));
        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, "helpPlayer", playerButton));
        
        helpOverlay.addHelpFrame(new HelpFrameText(skin, "helpEnd"));
        
        helpButton.addListener(helpOverlay);
        showHelpScreen = false;
        
        statsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setStatisticsScreen();
            }
        });
    }
    
    /**
     * Creates the version text.
     * <p>
     * "Version: Major.Minor.Patch"
     * 
     * @return version text
     */
    private String createVersion() {
        // return I18n.t("version") + ": " + Fly.VERSION_MAJOR + "." +
        // Fly.VERSION_MINOR + "." + Fly.VERSION_PATCH;
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
        stage.dispose();
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
