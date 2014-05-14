package de.fau.cs.mad.fly;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

public class Assets {
	public static final AssetManager manager = new AssetManager();
	
	public static final AssetDescriptor<Model> space = new AssetDescriptor<Model>("spacesphere.obj", Model.class); 
	public static final AssetDescriptor<Model> torus = new AssetDescriptor<Model>("torus.obj", Model.class);
	public static final AssetDescriptor<Texture> flyTextureLoadingScreen = new AssetDescriptor<Texture>("Fly.png", Texture.class);
	
	public static void loadAssetsForLoadingScreen() {
		manager.load(flyTextureLoadingScreen);
		manager.finishLoading();
	}
	
	public static void load() {
		manager.load(space);
		manager.load(torus);
		manager.finishLoading();
	}
	
	public static void dispose() {
		manager.dispose();
	}
}
