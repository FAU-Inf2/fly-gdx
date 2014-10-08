package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.res.Assets;

public abstract class BasicScreen implements Screen {
    
    /** saves weather the static members have been initialized or not */
    private static boolean initialized = false;
    
    private static float widthScalingFactor;
    private static float heightScalingFactor;
    private static float scalingFactor;
    private static float xSkalingFactor;
    private static float ySkalingFactor;
    private static float deltaX;
    private static float deltaY;
    private static Sprite background;
    
    protected static SpriteBatch batch;
    protected static Viewport viewport;
    
    protected final Stage stage;
    
    /**
     * Processes all the input within the {@link #LevelChooserScreen(Fly)}. the
     * multiplexer offers the possibility to add several InputProcessors
     */
    protected InputMultiplexer inputProcessor;
    
    public BasicScreen() {
        // stage has to be created before initialize because it is needed for
        // creating the viewport
        stage = new Stage();
        if (!initialized) {
            initialize();
            initialized = true;
        }
        stage.setViewport(viewport);
        inputProcessor = new InputMultiplexer(stage, new BackProcessor());
        generateContent();
    }
    
    private void initialize() {
        batch = new SpriteBatch();
        Assets.load(Assets.background);
        background = new Sprite(Assets.manager.get(Assets.background));
        widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) Gdx.graphics.getWidth();
        heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) Gdx.graphics.getHeight();
        scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        
        updateBackground();
        viewport = new FitViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
    }
    
    private void updateBackground() {
        xSkalingFactor = Gdx.graphics.getWidth() / background.getWidth();
        ySkalingFactor = Gdx.graphics.getHeight() / background.getHeight();
        deltaX = 0f;
        deltaY = 0f;
        background.setOrigin(0, 0);
        if (xSkalingFactor >= ySkalingFactor) {
            background.setScale(xSkalingFactor);
            deltaY = (Gdx.graphics.getHeight() - background.getHeight() * xSkalingFactor) / 2.0f;
        } else {
            background.setScale(ySkalingFactor);
            deltaX = (Gdx.graphics.getWidth() - background.getWidth() * ySkalingFactor) / 2.0f;
        }
        background.setPosition(deltaX, deltaY);
    }
    
    /** You have to overwrite this method to create your custom content */
    protected void generateContent() {
    };
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
       // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        batch.begin();
        background.draw(batch);
        batch.end();
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void show() {
        // allow this screen to catch the back key
        Gdx.input.setCatchBackKey(true);
        // delegate all inputs to the #inputProcessor
        Gdx.input.setInputProcessor(inputProcessor);
    }
    
    @Override
    public void hide() {
        // nothing to do, that is common for all screens
    }
    
    @Override
    public void pause() {
        // nothing to do, that is common for all screens
    }
    
    @Override
    public void resume() {
        // nothing to do, that is common for all screens
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        initialized = false;
    }
    
    /**
     * Sets the current screen.
     * 
     */
    public void set() {
        ((Fly) Gdx.app.getApplicationListener()).setScreen(this);
    }
    
}
