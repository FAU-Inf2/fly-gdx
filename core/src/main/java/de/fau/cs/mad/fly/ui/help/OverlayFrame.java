package de.fau.cs.mad.fly.ui.help;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.ui.UI;

/**
 * Abstract class that represents a frame that is layed above a visible screen.
 * <p>
 * To use an OverlayFrame, extend this class. Make sure to call
 * {@link #setupBatchAndStage()} before you place elements on the screen. This
 * makes sure that your stuff is displayed independent from the resolution.
 * After you have placed your stuff set the {@link #viewport} to the width and
 * the height of the screen.
 * 
 * @author Lukas Hahmann
 * 
 */
public abstract class OverlayFrame {
    
    protected Batch batch;
    protected Stage stage;
    protected float scalingFactor;
    protected Viewport viewport;
    
    /**
     * Set up batch and stage that are independent from the resolution. Call
     * this method before you create your content.
     */
    public void setupBatchAndStage() {
        this.batch = new SpriteBatch();
        
        stage = new Stage();
        float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) Gdx.graphics.getWidth();
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) Gdx.graphics.getHeight();
        scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
    }
    
    /**
     * Is called when this frame is shown. Should be rendered on top of the
     * current visible content.
     */
    public abstract void render();
    
    /**
     * Method that is called before the first render call is done. Makes sure
     * that everything that is necessary for creating the content is already
     * properly positioned.
     */
    public void generateContent() {
        
    }
}
