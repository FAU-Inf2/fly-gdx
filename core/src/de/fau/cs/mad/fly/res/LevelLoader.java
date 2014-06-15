package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.geo.Perspective;

import java.util.*;

/**
 * Created by danyel on 26/05/14.
 */
public class LevelLoader extends AsynchronousAssetLoader<Level, LevelLoader.LevelParameters> {
	Level level;
	private final JsonReader reader;
	private final Json auto;

	public LevelLoader() {
		this(new InternalFileHandleResolver(), new JsonReader());
	}

	/**
	 * Constructor, sets the {@link com.badlogic.gdx.assets.loaders.FileHandleResolver} to use to resolve the file associated with the asset name.
	 *
	 * @param resolver
	 */
	public LevelLoader(FileHandleResolver resolver, JsonReader reader) {
		this(resolver, reader, new Json());
	}

	public LevelLoader(FileHandleResolver resolver, JsonReader reader, Json json) {
		super(resolver);
		this.reader = reader;
		this.auto = json;
	}

	private Map<String, GameModel> models;
	public Level fromJson() {
		getJson();
		models = new HashMap<String, GameModel>();
		for ( Map.Entry<String, String> e : dependencies.entrySet() )
			models.put(e.getKey(), dependencyFor(e.getKey()));
		getComponents();
		int id = json.getInt("id");
		String name = json.getString("name");
		Perspective start = auto.fromJson(Perspective.class, json.get("start").toString());
		System.out.println(start);
		JsonValue gates = json.get("gates");
		Map<Integer, Level.Gate> ps = new HashMap<Integer, Level.Gate>();
		Matrix4 identityMatrix = new Matrix4();
		Gdx.app.log("LevelLoader.fromJson", "first iteration on gates");
		for (JsonValue e : gates) {
			JsonValue gid = e.get("id");
			if ( gid != null ) {
				Level.Gate p = new Level.Gate(gid.asInt());
				p.display = components.get(e.getString("display"));
				p.goal = components.get(e.getString("goal"));
				// TODO: make this explicit (don't just blacklist identity matrix...)
				if ( Arrays.equals(p.goal.transform.getValues(), identityMatrix.getValues()) )
					p.goal.transform = p.display.transform.cpy();
				ps.put(p.id, p);
			}
		}

		Gdx.app.log("LevelLoader.fromJson", "second iteration on gates");
		Level.Gate dummy = null;
		for (JsonValue e : gates) {
			JsonValue gid = e.get("id");
			Gdx.app.log("LevelLoader.fromJson", "gid " + gid);
			Level.Gate p;
			if ( gid != null )
				p = ps.get(gid.asInt());
			else {
				p = new Level.Gate(-1);
				dummy = p;
			}
			Collection<Level.Gate> successors = new ArrayList<Level.Gate>();
			int[] ss = e.get("successors").asIntArray();
			for (int s : ss)
				successors.add(ps.get(s));
			p.successors = successors;
		}
		if ( dummy == null )
			throw new RuntimeException("No dummy gate found.");
		Gdx.app.log("LevelLoader.fromJson", "Exit.");
		return new Level(name, start, components.values(), models, dummy);
	}

	private JsonValue json;
	private void getJson() {
		if ( json == null )
			json = reader.parse(file);
	}

	private Map<String, String> dependencies;
	private void getDependencies() {
		if ( dependencies == null ) {
			getJson();
			dependencies = new HashMap<String, String>();
			for (JsonValue e : json.get("dependencies")) {
				String fileName = e.asString();
				dependencies.put(e.name(), fileName);
			}
		}
	}

	private Map<String, GameObject> components;
	private void getComponents() {
		if ( components == null ) {
			getJson();
			components = new HashMap<String, GameObject>();
			for (JsonValue e : json.get("components")) {
				String id = e.getString("id");
				String ref = e.getString("ref");
				GameObject o = new GameObject(models.get(ref));
				o.modelId = ref;
				o.id = id;
				JsonValue transform = e.get("transformMatrix");
				if ( transform != null )
					o.transform = new Matrix4(transform.asFloatArray());
				components.put(id, o);
			}
		}
	}

	private GameModel dependencyFor(String ref) {
		return manager.get(dependencies.get(ref), GameModel.class);
	}

	private AssetManager manager;
	private String fileName;
	private FileHandle file;
	private LevelParameters parameter;

	private void deinit() {
		level = null;
		json = null;
		components = null;
		dependencies = null;
		file = null;
		fileName = null;
		parameter = null;
		manager = null;
		models = null;
	}

	@Override
	public Level loadSync(AssetManager manager, String fileName, FileHandle file, LevelParameters parameter) {
		Gdx.app.log("LevelLoader.loadSync", "LOADING.");
		Level l = level;
		deinit();
		return l;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, LevelParameters parameter) {
		Gdx.app.log("LevelLoader.loadAsync", "LOADING.");
		this.level = null;
		this.manager = manager;
		this.level = fromJson();
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, LevelParameters parameter) {
		this.file = file;
		this.fileName = fileName;
		this.parameter = parameter;
		getDependencies();
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		for( String name : dependencies.values() ) {
			Gdx.app.log("LevelLoader.getDependencies", name);
			deps.add(new AssetDescriptor<GameModel>(name, GameModel.class));
		}
		return deps;
	}

	static public class LevelParameters extends AssetLoaderParameters<Level> {}
}