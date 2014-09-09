package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.res.Assets;

/**
 * Manages the skin.
 * 
 * @author Tobi
 * 
 */
public class SkinManager implements Disposable {
    
    private Skin skin;
    
    /**
     * Creates the skin manager.
     */
    public SkinManager(String skinFile) {
        createSkin(skinFile);
    }
    
    /**
     * Creates the skin for the UI.
     */
    public void createSkin(String skinFile) {
        long time = System.currentTimeMillis();
        Assets.load(Assets.textureAtlas);
        Gdx.app.log("timing", "createSkin, loadAssets : " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        skin = new Skin(Assets.manager.get(Assets.textureAtlas));
        Gdx.app.log("timing", "createSkin, create Skin : " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        Texture texture = new Texture(Gdx.files.internal("fonts/sans.png"), true);
        Gdx.app.log("timing", "createSkin, create Texture: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
        Gdx.app.log("timing", "createSkin, set filter: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/sans.fnt"), new TextureRegion(texture), false);
        Gdx.app.log("timing", "createSkin, great bitmap font: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        skin.add("default-font", font);
        Gdx.app.log("timing", "createSkin, add font: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        font = new BitmapFont(Gdx.files.internal("fonts/sans.fnt"), new TextureRegion(texture), false);
        Gdx.app.log("timing", "createSkin, small font: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        font.scale(-0.5f);
        Gdx.app.log("timing", "createSkin, scale font: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        skin.add("small-font", font);
        Gdx.app.log("timing", "createSkin, add small font: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        skin.load(Gdx.files.internal(skinFile));
        Gdx.app.log("timing", "createSkin, load skin file: " + String.valueOf(System.currentTimeMillis()-time));
    }
    
    /**
     * Getter for the skin.
     * 
     * @return skin
     */
    public Skin getSkin() {
        return skin;
    }
    
    @Override
    public void dispose() {
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
    }
}
