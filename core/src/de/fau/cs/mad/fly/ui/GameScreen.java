package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameController.GameState;

/**
 * Provides a screen for the game itself.
 * 
 * @author Tobias Zangl
 */
public class GameScreen implements Screen {
    private GameController gameController;
    private final Fly game;
    
    public GameScreen(final Fly game) {
        this.gameController = game.getGameController();
        this.game = game;
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
        this.gameController = game.getGameController();
    }
    
    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "hide");
        gameController.disposeGame();
    }
    
    @Override
    public void pause() {
        Gdx.app.log("GameScreen", "pause");
        if (gameController.isRunning()) {
            gameController.pauseGame();
        }
    }
    
    @Override
    public void resume() {
        Gdx.app.log("GameScreen", "resume");
        GameController.GameState state = gameController.getGameState();
        if (state == GameState.PAUSED) {
            gameController.resumeGame();
        }
    }
    
    @Override
    public void dispose() {
        // nothing to dispose
    }
}
