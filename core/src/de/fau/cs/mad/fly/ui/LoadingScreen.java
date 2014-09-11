package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
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
    protected final Sprite background;
    protected ScalableProgressBar progressBar;
    protected final float progressBarWidth = 2000f;
    protected final TextButton button;
    
    protected final Table table;
    
    protected final Loadable<T> listenable;
    
    public LoadingScreen(Loadable<T> listenable) {
        batch = new SpriteBatch();
        this.listenable = listenable;
        Assets.load(Assets.background);
        background = new Sprite(Assets.manager.get(Assets.background));
        table = new Table();
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
        
        table.pad(UI.Window.BORDER_SPACE).setFillParent(true);
        stage.addActor(table);
        
        // add button
        button = new TextButton(I18n.t("button.start"), skin);
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
        table.add(button).bottom().width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).expand();
        button.setVisible(false);
        if (Gdx.app.getType().equals(Application.ApplicationType.iOS)) {
            final TextButton button2 = new TextButton("Compass available: " + Boolean.toString(Gdx.input.isPeripheralAvailable(Input.Peripheral.Compass)), skin);
            table.add(button2).bottom().expand();
        }
        table.row();
        
        // add progress bar
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
