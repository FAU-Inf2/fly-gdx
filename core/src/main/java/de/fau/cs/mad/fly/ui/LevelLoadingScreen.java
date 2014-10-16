package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Displays the loading screen with a progress bar.
 * <p>
 * If the value of the progress bar reaches 100f a button to start the screen is shown.
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LevelLoadingScreen extends LoadingScreen<Level> {
    
    private boolean addButton = true;
    
    public LevelLoadingScreen(Loader loader, final BasicScreen screenToReturn) {
        super(loader, screenToReturn);
        setBackProcessor(new GenericBackProcessor(screenToReturn) {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
                    screenToReturn.set();
                    Fly game = (Fly) Gdx.app.getApplicationListener();
                    game.getGameController().disposeGame();
                    return true;
                } else if ( keycode == Keys.ENTER || keycode == Keys.SPACE ) {
                    Fly app = ((Fly) Gdx.app.getApplicationListener());
                    app.setGameScreen();
                    app.getGameController().initGame();
                    if(!PlayerProfileManager.getInstance().getCurrentPlayerProfile().getSettingManager().getBoolean(SettingManager.USE_TOUCH))
                        app.getGameController().getFlightController().init();
                }
                return false;
            }
        });
    }
    
    public void showButton() {
        if (addButton) {
            progressBar.setVisible(false);
            button.setVisible(true);
            generateBackButton();
            inputProcessor.addProcessor(stage);
            Gdx.input.setInputProcessor(inputProcessor);
        }
        
        addButton = false;
    }
}
