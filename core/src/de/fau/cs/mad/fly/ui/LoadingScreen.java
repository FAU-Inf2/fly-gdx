package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.Loadable;
import de.fau.cs.mad.fly.ProgressListener;
import de.fau.cs.mad.fly.res.Assets;

/**
 * Displays the splash screen.
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LoadingScreen<T> extends BasicScreen {
    protected final SpriteBatch batch;
    protected Sprite background;
    protected ScalableProgressBar progressBar;
    protected float progressBarWidth = 2000f;
    
    protected Table table;
    
    protected Loadable<T> listenable;
    
    public LoadingScreen(Loadable<T> listenable) {
        this.listenable = listenable;
        batch = new SpriteBatch();
        Assets.load(Assets.background);
        background = new Sprite(Assets.manager.get(Assets.background));
        // Here the current display size should be used, to make sure the image
        // is not stretched
        float xSkalingFactor = Gdx.graphics.getWidth() / background.getWidth();
        float ySkalingFactor = Gdx.graphics.getHeight() / background.getHeight();
        float deltaX = 0f;
        float deltaY = 0f;
        background.setOrigin(0, 0);
        if (xSkalingFactor >= ySkalingFactor) {
            background.setScale(xSkalingFactor);
            deltaY = (Gdx.graphics.getHeight() - background.getHeight() * xSkalingFactor) / 2.0f;
        } else {
            background.setScale(ySkalingFactor);
            deltaX = (Gdx.graphics.getWidth() - background.getWidth() * ySkalingFactor) / 2.0f;
        }
        background.setPosition(deltaX, deltaY);
        
        progressBar = new ScalableProgressBar(skin);
        progressBar.setWidth(progressBarWidth);
        
        table = new Table();
        table.pad(UI.Window.BORDER_SPACE).setFillParent(true);
        stage.addActor(table);
        
        table.add(progressBar).bottom().expand();
        listenable.addProgressListener(new ProgressListener.ProgressAdapter<T>() {
            @Override
            public void progressUpdated(float percent) {
                progressBar.setProgress(percent / 100f);
            }
        });
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        listenable.update();
        
        batch.begin();
        background.draw(batch);
        batch.end();
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
