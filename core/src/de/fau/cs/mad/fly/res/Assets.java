package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import de.fau.cs.mad.fly.game.GameModel;

/**
 * Loads and stores the AssetManager who cares about the Assets.
 * 
 * @author Lukas Hahmann
 */
public class Assets {
    public static AssetManager manager;

    public static final AssetDescriptor<TextureAtlas> textureAtlas = new AssetDescriptor<TextureAtlas>("uiskin.atlas", TextureAtlas.class);
    public static final AssetDescriptor<Texture> background = new AssetDescriptor<Texture>("background.jpg", Texture.class);
    
    public static void init() {
        manager = new AssetManager();
        manager.setLoader(Level.class, new LevelLoader());
        manager.setLoader(GameModel.class, new GameModelLoader());
        manager.finishLoading();
    }
    
    public static void dispose() {
        manager.dispose();
    }
    
    public static void load(AssetDescriptor descriptor) {
        manager.load(descriptor);
        manager.finishLoading();
    }
    
    public static void unload(String assetName) {
    	//TODO: manager.contains needs the assetName but manager.unload the fileName
    	// currently assetName is actually the fileName so manager.contains is always false
        //if (manager.containsAsset(assetName)) {
            manager.unload(assetName);
        //}
    }
}