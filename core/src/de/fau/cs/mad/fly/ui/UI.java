package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;

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
		public static final float RELATIVE_HEIGHT_OF_ALL_BUTTONS = .5f;

		/**
		 * This percentage of the screen width is covered with space between
		 * buttons
		 */
		public static final float RELATIVE_HEIGHT_OF_ALL_SPACE = .5f;

		public static final float SINGLE_SPACE_WIDTH = 200;
		public static final float SINGLE_SPACE_HEIGHT = 150;
		
		/** Background color of the windows */
		public static final String BACKGROUND_COLOR = "black";
		
		/** This is used to find the transparent style for a {@link ScrollPane} in the uiskin.json */
		public static final String TRANSPARENT_SCROLL_PANE_STYLE = "transparent";
		
		/** This is the amount of pixels which should be left blank between border of the screen and the outer widgets */
		public static final int BORDER_SPACE = Gdx.graphics.getWidth()/40;
		
		/** Width in Pixel for that all bitmaps are optimized */
		public static final float REFERENCE_WIDTH = 4000;
		
		/** Height in Pixel for that all bitmaps are optimized */
		public static final float REFERENCE_HEIGHT = 2200;
		
		
	}

	/** This class contains all parameters related to buttons. */
	public static final class Buttons {
		
		/** This class should not be instantiated */
		private Buttons () {
		}
		
		public static final int MAIN_BUTTON_WIDTH = 1600;
		
		public static final int MAIN_BUTTON_HEIGHT = 380;
		
		/** This is the default font size for all Buttons */
		public static final int FONT_SIZE = 150;
		
		public static final String STYLE = "rounded";
		
		/** Max. number of buttons for level to show in a row */
		public static final int BUTTONS_IN_A_ROW = 2;

		public static final int BUTTONS_IN_A_COLUMN = 3;

		public static final float BUTTON_WIDTH = Window.RELATIVE_WIDTH_OF_ALL_BUTTONS / (float) BUTTONS_IN_A_ROW * (float) Gdx.graphics.getWidth();
		public static final float BUTTON_HEIGHT = Window.RELATIVE_HEIGHT_OF_ALL_BUTTONS / (float) BUTTONS_IN_A_COLUMN * (float) Gdx.graphics.getHeight();

		public static final float SPACE_WIDTH = Window.SINGLE_SPACE_WIDTH;
		public static final float SPACE_HEIGHT = Window.SINGLE_SPACE_HEIGHT;
		
		/** Style used for settings button in main menu */
		public static final String SETTING_BUTTON_STYLE = "settings";
	}
	
}