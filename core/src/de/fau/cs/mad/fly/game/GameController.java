package de.fau.cs.mad.fly.game;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.fau.cs.mad.fly.Debug;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.BackProcessor;

/**
 * Manages the Player, the Level, the UI, the CameraController and all the
 * optional Features and calls the load(), init(), render(), finish() and
 * dispose() methods of those.
 * <p>
 * Optional Feature Interfaces: load(): - called before the game starts while
 * the loading screen is shown - should be stuff like loading models, creating
 * instances, which takes a while init(): - called the moment the game starts
 * after switching to the game screen - should be stuff like setting values,
 * resetting counter update(): - called every frame while the game is running
 * and not paused - should be stuff like calculating and updating values
 * render(): - called every frame while the game is running or paused - should
 * be stuff like rendering models draw(): - called every frame while the game is
 * running or paused - should be stuff like drawing overlays finish(): - called
 * at the moment the game is over, still in game screen - should be stuff like
 * showing points, saving the highscore dispose(): - called when the game screen
 * is left - should be stuff like disposing models
 * 
 * @author Lukas Hahmann
 */
public class GameController implements TimeIsUpListener {
    public enum GameState {
        RUNNING, PAUSED, FINISHED
    }
    
    protected Stage stage;
    protected List<IFeatureLoad> optionalFeaturesToLoad;
    protected List<IFeatureInit> optionalFeaturesToInit;
    protected List<IFeatureUpdate> optionalFeaturesToUpdate;
    protected List<IFeatureRender> optionalFeaturesToRender;
    protected List<IFeatureDraw> optionalFeaturesToDraw;
    protected List<IFeatureDispose> optionalFeaturesToDispose;
    protected List<IFeatureFinish> optionalFeaturesToFinish;
    protected FlightController flightController;
    protected CameraController cameraController;
    protected PerspectiveCamera camera;
    protected ModelBatch batch;
    protected Level level;
    private GameState gameState;
    private TimeController timeController;
    private InputMultiplexer inputProcessor;
    
    /** Use Builder to initiate GameController */
    protected GameController() {
    }
    
    /**
     * Getter for the model batch used to draw the 3d game.
     * 
     * @return ModelBatch
     */
    public ModelBatch getBatch() {
        return batch;
    }
    
    /**
     * Getter for the stage.
     * 
     * @return {@link #stage}
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Getter for the flight controller.
     * 
     * @return {@link #flightController}
     */
    public FlightController getFlightController() {
        return flightController;
    }
    
    /**
     * Getter for the camera.
     * 
     * @return {@link #camera}
     */
    public PerspectiveCamera getCamera() {
        return camera;
    }
    
    /**
     * Setter for the input Processor.
     * 
     * @param inputProcessor
     */
    public void setInputProcessor(InputMultiplexer inputProcessor) {
        this.inputProcessor = inputProcessor;
    }
    
    /**
     * Getter for the level.
     * 
     * @return {@link #level}
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * Setter for the level.
     */
    public void setLevel(Level level) {
        this.level = level;
    }
    
    /**
     * This method is called, while the level is loading. It loads everything
     * the default functions need. Furthermore all optional features in
     * {@link #optionalFeaturesToLoad} are loaded.
     */
    public void loadGame() {
        // load features
        for (IFeatureLoad optionalFeature : optionalFeaturesToLoad) {
            optionalFeature.load(this);
        }
    }
    
    /**
     * This method is called, when the level is initialized. It initializes all
     * default functions that are needed in all levels, like render the level.
     * Furthermore all optional features in {@link #optionalFeaturesToInit} are
     * initialized.
     */
    public void initGame() {
        camera = cameraController.getCamera();
        
        // initialize features
        for (IFeatureInit optionalFeature : optionalFeaturesToInit) {
            optionalFeature.init(this);
        }
        
        PlayerManager.getInstance().getCurrentPlayer().resetLives();
        // Debug.setOverlay(0,
        // PlayerManager.getInstance().getCurrentPlayer().getLives());
        
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(inputProcessor);
        startGame();
        Gdx.app.log("GameController.initGame", "OK HAVE FUN!");
    }
    
    /**
     * Sets the game state to running.
     */
    public void startGame() {
        gameState = GameState.RUNNING;
        timeController.initAndStartTimer(level.getLeftTime());
    }
    
    /**
     * Sets the game state to paused.
     */
    public void pauseGame() {
        gameState = GameState.PAUSED;
        timeController.pause();
    }
    
    /**
     * Sets the game state to finished and ends the game.
     */
    public void finishGame() {
        System.out.println("FINISHED");
        gameState = GameState.FINISHED;
        endGame();
    }
    
    /**
     * Sets the game from paused to running
     */
    public void resumeGame() {
        gameState = GameState.RUNNING;
        timeController.resume();
    }
    
    /**
     * Getter for the game state.
     * 
     * @return GameState
     */
    public GameState getGameState() {
        return gameState;
    }
    
    /**
     * Checks if the game is running.
     * 
     * @return true if the game is running, otherwise false.
     */
    public boolean isRunning() {
        return gameState == GameState.RUNNING;
    }
    
    /**
     * Checks if the game is paused.
     * 
     * @return true if the game is paused, otherwise false.
     */
    public boolean isPaused() {
        return (gameState == GameState.PAUSED);
    }
    
    /**
     * This method is called every frame. Furthermore all optional features in
     * {@link #optionalFeaturesToRender} are updated and rendered.
     * 
     * @param delta
     *            Time after the last call.
     */
    public void renderGame(float delta) {
        stage.act(delta);
        
        if (gameState == GameState.RUNNING) {
            // update features if the game is not paused
            
            flightController.update(delta);
            camera = cameraController.updateCamera();
            level.update(delta, camera);
            
            for (IFeatureUpdate optionalFeature : optionalFeaturesToUpdate) {
                optionalFeature.update(delta);
            }
            
            CollisionDetector.getInstance().perform(delta);
            timeController.checkTime();
        }
        
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        // render features
        batch.begin(camera);
        level.render(delta, batch, camera);
        for (IFeatureRender optionalFeature : optionalFeaturesToRender) {
            optionalFeature.render(delta);
        }
        batch.end();
        
        // draw features
        for (IFeatureDraw optionalFeature : optionalFeaturesToDraw) {
            optionalFeature.draw(delta);
        }
        stage.draw();
    }
    
    /**
     * This method is called when the game is over. Furthermore all optional
     * features in {@link #optionalFeaturesToFinish} are finished.
     */
    public void endGame() {
        pauseGame();
        for (IFeatureFinish optionalFeature : optionalFeaturesToFinish) {
            optionalFeature.finish();
        }
    }
    
    /**
     * This method is called when the game is over. Furthermore all optional
     * features in {@link #optionalFeaturesToDispose} are disposed.
     */
    public void disposeGame() {
        // dispose features
        for (IFeatureDispose optionalFeature : optionalFeaturesToDispose) {
            // Gdx.app.log("GameController.disposeGame", "dispose: " +
            // optionalFeature.getClass().getSimpleName());
            optionalFeature.dispose();
        }
        CollisionDetector.getInstance().dispose();
        
        optionalFeaturesToLoad.clear();
        optionalFeaturesToInit.clear();
        optionalFeaturesToUpdate.clear();
        optionalFeaturesToRender.clear();
        optionalFeaturesToDraw.clear();
        optionalFeaturesToDispose.clear();
        optionalFeaturesToFinish.clear();
    }
    
    /**
     * Setter for the time controller.
     * 
     * @param timeController
     *            The time controller to use.
     */
    public void setTimeController(TimeController timeController) {
        this.timeController = timeController;
        timeController.registerTimeIsUpListener(this);
        
    }
    
    /**
     * Getter for the time controller.
     * 
     * @return {@link #timeController}
     */
    public TimeController getTimeController() {
        return timeController;
    }
    
    @Override
    public void timeIsUp() {
        pauseGame();
    }
}
