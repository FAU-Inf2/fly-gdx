package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScalableProgressBar extends Actor {
    
    private NinePatch background;
    public NinePatch foreground;
    float progress = 0f;
    
    public ScalableProgressBar(Skin skin) {
        ScalableProgressBarStyle style = skin.get("default", ScalableProgressBarStyle.class);
        background = style.background;
        foreground = style.foreground;
        super.setHeight(133f);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        foreground.draw(batch, super.getX() + 11, super.getY() + 11, super.getWidth() * progress - 14, 110);
    }
    
    /**
     * Sets the progress
     * 
     * @param progress
     *            0..1
     * */
    public void setProgress(float progress) {
        this.progress = progress;
        Gdx.app.log("progress", String.valueOf(progress));
    }
    
    static public class ScalableProgressBarStyle implements java.io.Serializable {
        
        /** Background for the progress bar */
        public NinePatch background;
        
        /**
         * Foreground for the progress bar, that continuously covers the
         * background
         */
        public NinePatch foreground;
    }
}
