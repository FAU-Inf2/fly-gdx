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
         * This is the amount of pixels which should be left blank between
         * border of the screen and the outer widgets
         */
        public static final int BORDER_SPACE = 100;
        
        /**
         * This space is left from the bottom border of the screen to the lowest
         * element in case of a screen with a back button.
         */
        public static final int BOTTOM_SPACE_FOR_BACK_KEY = 500;
        
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
        
        public static final int TEXT_BUTTON_WIDTH = 1550;
        
        public static final int TEXT_BUTTON_HEIGHT = 320;
        
        public static final int IMAGE_BUTTON_WIDTH = 320;
        
        public static final int IMAGE_BUTTON_HEIGHT = 320;
        
        /** This is the default font size for all Buttons */
        public static final int DEFAULT_FONT_SIZE = 150;
        
        /** This is the default font size for small Buttons */
        public static final int SMALL_FONT_SIZE = 100;
        
        /** Max. number of buttons for level to show in a row */
        public static final int BUTTONS_IN_A_ROW = 2;
        
        public static final float SPACE = 50;
        
        public static final float SPACE_WIDTH = 100;
        public static final float SPACE_HEIGHT = 100;
        
        /** Style used for settings button in main menu */
        public static final String SETTING_BUTTON_STYLE = "settings";
        public static final String HELP_BUTTON_STYLE = "help";
    }
    
    /** This class contains all parameters related to labels. */
    public static final class Labels {
        
        /** This class should not be instantiated */
        private Labels() {
        }
        
        public static final float SPACE_WIDTH = 100;
        public static final float SPACE_HEIGHT = 100;
    }
    
    public static final class Dialogs {
        
        private Dialogs() {
        }
        
        public static final int PADDING = 50;
    }
}