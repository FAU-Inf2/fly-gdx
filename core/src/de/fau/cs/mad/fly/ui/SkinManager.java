package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages the skin and the fonts in the skin.
 * 
 * @author Tobi, Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class SkinManager implements Disposable {
    
    private Skin skin;
    private static SkinManager instance;
    
    public static SkinManager getInstance() {
        if (instance == null) {
            instance = new SkinManager("uiskin.json");
        }
        return instance;
    }
    
    /**
     * Creates the skin for the UI. Load all the necessary fonts and add them to
     * the skin.
     */
    private SkinManager(String skinFile) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(atlas);
        
        Texture texture = new Texture(Gdx.files.internal("fonts/default.png"), true);
        texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
        TextureRegion textureRegion = new TextureRegion(texture);
        FileHandle fontFile = Gdx.files.internal("fonts/default.fnt");
        
        // font for version info
        BitmapFont font = new BitmapFont(fontFile, textureRegion, false);
        skin.add("small-font", font);
        
        // medium font, it has to be recreated, because adding it to the skin,
        // does not make a copy
        font = new BitmapFont(fontFile, textureRegion, false);
        font.scale(1.03f);
        skin.add("medium-font", font);
        
        // standard font, it has to be recreated, because adding it to the skin,
        // does not make a copy
        font = new BitmapFont(fontFile, textureRegion, false);
        font.scale(1.3f);
        skin.add("default-font", font);
        
        skin.load(Gdx.files.internal(skinFile));
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
        Gdx.app.log("SkinManager", ".dispose() - enter");
        if (skin != null) {
            Gdx.app.log("SkinManager", ".dispose() - skin disposed");
            skin.dispose();
            skin = null;
            instance = null;
        }
    }
}
