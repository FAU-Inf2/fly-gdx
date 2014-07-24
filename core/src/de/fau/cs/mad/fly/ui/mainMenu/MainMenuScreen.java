package de.fau.cs.mad.fly.ui.mainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.BasicScreen;
import de.fau.cs.mad.fly.ui.UI;
import de.fau.cs.mad.fly.ui.WithHelpScreen;

/**
 * Displays the main menu with Start, Options, Help and Exit buttons.
 * 
 * @author Tobias Zangl
 */
public class MainMenuScreen extends BasicScreen implements WithHelpScreen {
    
    private HelpOverlayMainMenu helpOverlay;
    private boolean showHelpScreen = false;
    
    /**
     * Adds the main menu to the main menu screen.
     * <p>
     * Includes buttons for Start, Options, Help, Exit.
     */
    protected void generateContent() {
        // Search for all levels and put them in a list
        LevelManager.getInstance().getLevelList();
        // Create an instance of the PlayerManager, which needs an access to the
        // database
        FlyDBManager.getInstance();
        
        Table table = new Table();
        table.defaults().width(viewport.getWorldWidth() / 3);
        table.setFillParent(true);
        table.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
        stage.addActor(table);
        
        TextButtonStyle textButtonStyle = skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class);
        final Button continueButton = new TextButton(I18n.t("play"), textButtonStyle);
        final Button chooseLevelButton = new TextButton(I18n.t("choose.level"), textButtonStyle);
        final Button statsButton = new TextButton(I18n.t("highscores"), textButtonStyle);
        final ImageButton settingsButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
        
        textButtonStyle = skin.get(UI.Buttons.BIG_FONT_SIZE_STYLE, TextButtonStyle.class);
        final Button helpButton = new TextButton("?", textButtonStyle);
        
        
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
        table.add(statsButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        table.row().expand();
        
        chooseLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setLevelChoosingScreen();
            }
        });
        
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Player player = PlayerManager.getInstance().getCurrentPlayer();
                Level.Head levelHead = player.getLastLevel();
                if (levelHead == null) {
                    levelHead = LevelManager.getInstance().getLevelList().get(0);
                    player.setLastLevel(levelHead);
                }
                Loader.loadLevel(levelHead);
            }
        });
        
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setSettingScreen();
            }
        });
        
        this.helpOverlay = new HelpOverlayMainMenu(skin, this);
        helpButton.addListener(helpOverlay);
        showHelpScreen = false;
        
		statsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setStatisticsScreen();
			}
		});
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
