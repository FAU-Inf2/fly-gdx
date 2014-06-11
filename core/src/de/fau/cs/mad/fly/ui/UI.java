package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

/**
 * This file contains all parameter to modify the 2d UI.
 * 
 * @author Lukas Hahmann
 * 
 */
public final class UI {

	/** This class contains basic parameter for creating windows */
	public static final class window {

		/** This percentage of the screen width is covered with button surface */
		public static final float percentageOfButtonsWitdth = .8f;

		/**
		 * This percentage of the screen width is covered with space between
		 * buttons
		 */
		public static final float percentageOfSpaceWidth = 1 - percentageOfButtonsWitdth;

		/**
		 * This percentage of the screen height is covered with button surface.
		 * <p>
		 * If more levels exist, than there is place for buttons, show a row
		 * with halve buttons to indicate that there are more levels left. Hence
		 * {@link #percentageOfButtonsHeight} + {@link #percentageOfSpaceHeight}
		 * < 1.0.
		 */
		public static final float percentageOfButtonsHeight = .7f;

		/**
		 * This percentage of the screen width is covered with space between
		 * buttons
		 */
		public static final float percentageOfSpaceHeight = .15f;

		public static final float spaceWidth = window.percentageOfSpaceWidth / 6 * Gdx.graphics.getWidth();
		public static final float spaceHeight = window.percentageOfSpaceHeight / 6 * Gdx.graphics.getHeight();
		
		/** Background color of the windows */
		public static final Color backgroundColor = Color.BLACK;
	}

	/** This class contains all parameters related to smaller buttons. */
	public static final class smallButtons {
		/** Max. number of buttons for level to show in a row */
		public static final int buttonsInARow = 3;

		public static final int buttonsInAColumn = 3;

		public static final float buttonWidth = window.percentageOfButtonsWitdth / (float) buttonsInARow * (float) Gdx.graphics.getWidth();
		public static final float buttonHeight = window.percentageOfButtonsHeight / (float) buttonsInAColumn * (float) Gdx.graphics.getHeight();

		public static final float spaceWidth = window.spaceWidth;
		public static final float spaceHeight = window.spaceHeight;
	}
}