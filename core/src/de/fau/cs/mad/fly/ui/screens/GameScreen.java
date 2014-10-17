package de.fau.cs.mad.fly.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameController.GameState;

/**
 * Provides a screen for the game itself.
 * <p>
 * 
 * Manages the operations when leaving the screen or when the screen is hidden.
 * 
 * @author Tobias Zangl
 */
public class GameScreen implements Screen {
    
    private GameController gameController;
    
    public GameScreen() {
    }
    
    @Override
    public void render(float delta) {
        gameController.renderGame(delta);
    }
    
    @Override
    public void resize(int width, int height) {
        gameController.getStage().getViewport().update(width, height, true);
    }
    
    @Override
    public void show() {
        this.gameController = ((Fly)Gdx.app.getApplicationListener()).getGameController();
    }
    
    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "hide");
        gameController.disposeGame();
    }
    
    @Override
    public void pause() {
        Gdx.app.log("GameScreen", "pause");
        if (gameController.getGameState() == GameState.RUNNING) {
            gameController.setGameState(GameState.PAUSED);
        }
    }
    
    @Override
    public void resume() {
        Gdx.app.log("GameScreen", "resume");
        if (gameController.getGameState() == GameState.PAUSED) {
            gameController.setGameState(GameState.RUNNING);
        }
    }
    
    @Override
    public void dispose() {
        // nothing to dispose
    }
}
