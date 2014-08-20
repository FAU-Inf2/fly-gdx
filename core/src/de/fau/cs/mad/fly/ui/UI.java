package de.fau.cs.mad.fly.ui;

/**
 * This file contains all parameter to modify the 2d UI.
 * 
 * @author Lukas Hahmann
 * 
 */
public final class UI {
    
    /** This class should not be instantiated */
    private UI() {
    }
    
    /** This class contains basic parameter for creating windows */
    public static final class Window {
        
        /** This class should not be instantiated */
        private Window() {
        }
        
        public static final float SPACE_WIDTH = 200;
        public static final float SPACE_HEIGHT = 150;
        
        /** Background color of the windows */
        public static final String BACKGROUND_COLOR = "lightGrey";
        
        /**
         * This is used to find the transparent style for a {@link ScrollPane}
         * in the uiskin.json
         */
        public static final String TRANSPARENT_SCROLL_PANE_STYLE = "transparent";
        
        /**
         * This is the amount of pixels which should be left blank between
         * border of the screen and the outer widgets
         */
        public static final int BORDER_SPACE = 100;
        
        /** Width in Pixel for that all bitmaps are optimized */
        public static final float REFERENCE_WIDTH = 4000;
        
        /** Height in Pixel for that all bitmaps are optimized */
        public static final float REFERENCE_HEIGHT = 2200;
    }
    
    public static final class Overlay {
        
        /** This class should not be instantiated */
        private Overlay() {
        }
        
        public static final int OVERLAY_FONT_SIZE = 60;
    }
    
    /** This class contains all parameters related to buttons. */
    public static final class Buttons {
        
        /** This class should not be instantiated */
        private Buttons() {
        }
        
        public static final int MAIN_BUTTON_WIDTH = 1600;
        
        public static final int MAIN_BUTTON_HEIGHT = 380;
        
        /** This is the default font size for all Buttons */
        public static final int DEFAULT_FONT_SIZE = 150;
        
        public static final int SMALL_BUTTON_WIDTH = 1200;
        
        public static final int SMALL_BUTTON_HEIGHT = 260;
		
		/** This is the default font size for small Buttons */
        public static final int SMALL_FONT_SIZE = 100;
        
        public static final String DEFAULT_STYLE = "rounded";
        
        /** Max. number of buttons for level to show in a row */
        public static final int BUTTONS_IN_A_ROW = 2;
        
        public static final float SPACE_WIDTH = 100;
        public static final float SPACE_HEIGHT = 100;
        
        /** Style used for settings button in main menu */
        public static final String SETTING_BUTTON_STYLE = "settings";
        public static final String HELP_BUTTON_STYLE = "help";
    }
}