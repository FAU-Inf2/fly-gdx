package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ScalableProgressBar extends Actor {

	private NinePatch background;
	public NinePatch foreground;
	float progress = 0f;

	public ScalableProgressBar(ScalableProgressBarStyle style) {
		background = style.background;
		foreground = style.foreground;
		super.setHeight(133f);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
		foreground.draw(batch, super.getX(), super.getY(), super.getWidth()*progress, super.getHeight());
	}

	/**
	 * Sets the progress
	 * @param progress 0..1
	 * */
	public void setProgress(float progress) {
		this.progress = progress;
		Gdx.app.log("progress", String.valueOf(progress));
	}

	static public class ScalableProgressBarStyle {

		/** Background for the progress bar */
		public NinePatch background;

		/**
		 * Foreground for the progress bar, that continuously covers the
		 * background
		 */
		public NinePatch foreground;
	}
}
