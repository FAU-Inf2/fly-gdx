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
	
	/** This class should not be instantiated */
	private UI () {
	}

	/** This class contains basic parameter for creating windows */
	public static final class Window {
		
		/** This class should not be instantiated */
		private Window () {
		}

		/** This percentage of the screen width is covered with button surface */
		public static final float RELATIVE_WIDTH_OF_ALL_BUTTONS = .8f;

		/**
		 * This percentage of the screen width is covered with space between
		 * buttons
		 */
		public static final float RELATIVE_WIDTH_OF_ALL_SPACE = 1 - RELATIVE_WIDTH_OF_ALL_BUTTONS;

		/**
		 * This percentage of the screen height is covered with button surface.
		 * <p>
		 * If more levels exist, than there is place for buttons, show a row
		 * with halve buttons to indicate that there are more levels left. Hence
		 * {@link #RELATIVE_HEIGHT_OF_ALL_BUTTONS} + {@link #RELATIVE_HEIGHT_OF_ALL_SPACE}
		 * < 1.0.
		 */
		public static final float RELATIVE_HEIGHT_OF_ALL_BUTTONS = .7f;

		/**
		 * This percentage of the screen width is covered with space between
		 * buttons
		 */
		public static final float RELATIVE_HEIGHT_OF_ALL_SPACE = .15f;

		public static final float SINGLE_SPACE_WIDTH = Window.RELATIVE_WIDTH_OF_ALL_SPACE / 6 * Gdx.graphics.getWidth();
		public static final float SINGLE_SPACE_HEIGHT = Window.RELATIVE_HEIGHT_OF_ALL_SPACE / 6 * Gdx.graphics.getHeight();
		
		/** Background color of the windows */
		public static final Color BACKGROUND_COLOR = Color.BLACK;
	}

	/** This class contains all parameters related to smaller buttons. */
	public static final class SmallButtons {
		
		/** This class should not be instantiated */
		private SmallButtons () {
		}
		
		/** Max. number of buttons for level to show in a row */
		public static final int BUTTONS_IN_A_ROW = 3;

		public static final int BUTTONS_IN_A_COLUMN = 3;

		public static final float BUTTON_WIDTH = Window.RELATIVE_WIDTH_OF_ALL_BUTTONS / (float) BUTTONS_IN_A_ROW * (float) Gdx.graphics.getWidth();
		public static final float BUTTON_HEIGHT = Window.RELATIVE_HEIGHT_OF_ALL_BUTTONS / (float) BUTTONS_IN_A_COLUMN * (float) Gdx.graphics.getHeight();

		public static final float SPACE_WIDTH = Window.SINGLE_SPACE_WIDTH;
		public static final float SPACE_HEIGHT = Window.SINGLE_SPACE_HEIGHT;
	}
}