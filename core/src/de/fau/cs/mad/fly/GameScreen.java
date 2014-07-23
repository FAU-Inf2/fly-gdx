package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Assets;

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
        // delegate all inputs to the #inputProcessor
        // TODO: put stage in GameScreen and new InputMultiplexer back to the
        // constructor    	
        this.gameController = game.getGameController();
    }
    
    @Override
    public void hide() {
        gameController.disposeGame();
    }
    
    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void dispose() {
        Gdx.app.log("GameScreen.dispose", "dispose game screen");
        Assets.dispose();
        // game.getGameController().disposeGame();
    }
}
