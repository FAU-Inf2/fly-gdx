package de.fau.cs.mad.fly.ui;

import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.res.Level;

/**
 * Displays the loading screen with a progress bar.
 * <p>
 * If the value of the progress bar reaches 100f the game screen is loaded.
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LevelLoadingScreen extends LoadingScreen<Level> {
    
    private boolean add = true;
    
    public LevelLoadingScreen(Loader loader) {
        super(loader);
    }
    
    public void showButton() {
        if (add) {
            progressBar.setVisible(false);
            button.setVisible(true);
        }
        
        add = false;
    }
}
