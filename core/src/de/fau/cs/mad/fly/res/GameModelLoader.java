package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader.ObjLoaderParameters;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.game.GameModel;

/**
 * Created by danyel on 12/06/14.
 */
public class GameModelLoader extends AsynchronousAssetLoader<GameModel, GameModelLoader.GameModelParameters> {

	public GameModelLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	public GameModelLoader() {
		this(new InternalFileHandleResolver());
	}

	private GameModel object;

	private boolean hitboxExists = true;
	private String hitboxName;
	private void getHitbox(String fileName) {
		if ( hitboxName == null && hitboxExists ) {
			hitboxName = fileName.replace(".obj", ".hitbox.obj");
			hitboxExists = resolve(hitboxName).exists();
		}
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, GameModelParameters parameter) {
		fileName += ".obj";
		object = null;
		getHitbox(fileName);
		Model display = manager.get(fileName, Model.class);
		if ( hitboxExists )
			object = new GameModel(display, manager.get(hitboxName, Model.class));
		else
			object = new GameModel(display);
	}

	@Override
	public GameModel loadSync(AssetManager manager, String fileName, FileHandle file, GameModelParameters parameter) {
		GameModel g = object;
		object = null;
		hitboxExists = true;
		hitboxName = null;
		return g;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, GameModelParameters parameter) {
		fileName +=  ".obj";
		getHitbox(fileName);
		Array<AssetDescriptor> arr = new Array<AssetDescriptor>();

        ObjLoaderParameters modelParameters = new ObjLoaderParameters();
        modelParameters.textureParameter.genMipMaps = true;
        // TODO: check if the mag and min filter parameter are correct.
        modelParameters.textureParameter.magFilter = TextureFilter.MipMap;
        modelParameters.textureParameter.minFilter = TextureFilter.Nearest;
		
		arr.add(new AssetDescriptor<Model>(fileName, Model.class, modelParameters));
		if ( hitboxExists )
			arr.add(new AssetDescriptor<Model>(hitboxName, Model.class));
		return arr;
	}

	static public class GameModelParameters extends AssetLoaderParameters<GameModel> {}
}
