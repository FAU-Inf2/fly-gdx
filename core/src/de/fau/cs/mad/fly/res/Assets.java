package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import de.fau.cs.mad.fly.game.GameModel;

/**
 * Loads and stores the AssetManager who cares about the Assets.
 * 
 * @author Lukas Hahmann 
 */
public class Assets {
	public static AssetManager manager;

	public static final AssetDescriptor<GameModel> arrow = new AssetDescriptor<GameModel>("3dArrow/arrow", GameModel.class);
	public static final AssetDescriptor<Texture> flyTextureLoadingScreen = new AssetDescriptor<Texture>("Fly.png", Texture.class);
	public static final AssetDescriptor<Texture> background = new AssetDescriptor<Texture>("background.png", Texture.class);

	public static void init() {
		manager = new AssetManager();
		manager.setLoader(Level.class, new LevelLoader());
		manager.setLoader(GameModel.class, new GameModelLoader());
		manager.load(flyTextureLoadingScreen);
		manager.finishLoading();
	}

	public static void dispose() {
		manager.dispose();
	}
	
	public static void load(AssetDescriptor descriptor) {
		manager.load(descriptor);
		manager.finishLoading();
	}
}
