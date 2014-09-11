package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.res.Level;

/**
 * Displays the loading screen with a progress bar.
 * <p>
 * If the value of the progress bar reaches 100f the game screen is loaded.
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LevelLoadingScreen extends LoadingScreen<Level> {
    
    public LevelLoadingScreen(Loader loader) {
        super(loader);
    }
    
    private boolean add = true;
    
    public void addButton() {
        if (add) {
            progressBar.setVisible(false);
            
            final TextButton button = new TextButton(I18n.t("button.start"), skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Fly app = ((Fly) Gdx.app.getApplicationListener());
                    app.setGameScreen();
                    app.getGameController().initGame();
                    app.getGameController().getFlightController().resetSteering();
                    dispose();
                }
            });
            table.add(button).top().width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).expand();
            if(Gdx.app.getType().equals(Application.ApplicationType.iOS)) {
                final TextButton button2 = new TextButton("Compass available: " + Boolean.toString(Gdx.input.isPeripheralAvailable(Input.Peripheral.Compass)), skin);
                table.add(button2).bottom().expand();
            }
        }
        add = false;
    }
}
