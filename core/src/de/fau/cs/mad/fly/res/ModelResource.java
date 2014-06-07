package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by danyel on 18/05/14.
 */
public class ModelResource extends Resource {

	AssetDescriptor<Model> descriptor;

	public ModelResource(FileHandle f) {
		descriptor = new AssetDescriptor<Model>(f, Model.class);
		Assets.manager.load(descriptor);
	}
}
