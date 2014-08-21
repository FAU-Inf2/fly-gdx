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
        Assets.load(Assets.textureAtlas);
        skin = new Skin(Assets.manager.get(Assets.textureAtlas));
        
        Texture texture = new Texture(Gdx.files.internal("fonts/sans.png"), true);
        texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/sans.fnt"), new TextureRegion(texture), false);
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
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
    }
}
