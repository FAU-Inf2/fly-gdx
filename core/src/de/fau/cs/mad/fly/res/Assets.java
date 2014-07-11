package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import de.fau.cs.mad.fly.game.GameModel;

/**
 * Loads and stores the AssetManager who cares about the Assets.
 * 
 * @author Lukas Hahmann 
 */
public class Assets {
	public static AssetManager manager;

	public static final AssetDescriptor<GameModel> arrow = new AssetDescriptor<GameModel>("3d/arrow/arrow", GameModel.class);
	public static final AssetDescriptor<Texture> background = new AssetDescriptor<Texture>("background.png", Texture.class);
	public static final AssetDescriptor<TextureAtlas> textureAtlas = new AssetDescriptor<TextureAtlas>("uiskin.atlas", TextureAtlas.class);
	public static final AssetDescriptor<GameModel> spaceship = new AssetDescriptor<GameModel>("spaceship", GameModel.class);

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
	
	public static void loadSpaceship() {
		if(!manager.isLoaded(spaceship.fileName, spaceship.type))
			manager.load(spaceship);
		manager.finishLoading();
	}
}
