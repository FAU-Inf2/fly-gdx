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
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

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
		int levelID = json.getInt("id");
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
				JsonValue scoreJS = gid.get("score");
				if(scoreJS != null)
					p.score = scoreJS.asInt();
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
			//Gdx.app.log("LevelLoader.fromJson", "gid " + gid);
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

//		Collection<String> scripts = Arrays.asList(json.get("scripts").asStringArray());

		Gdx.app.log("LevelLoader.fromJson", "Exit.");
		Level level = new Level(name, start, components.values(), models, dummy);
		level.head.id = levelID;
		level.setLeftTime(json.getInt("time"));
		return level;
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
				JsonValue position = e.get("position");
				if ( transform != null ) {					
					o.transform = new Matrix4(transform.asFloatArray());
					//Gdx.app.log("LevelLoader.getComponents", "TransformMatrix: " + o.transform.toString());
				} else if ( position != null ) {
					Vector3 pos = new Vector3(position.asFloatArray());
					//Gdx.app.log("LevelLoader.getComponents", "Position: " + pos.toString());
					
					Vector3 scl = new Vector3(1.0f, 1.0f, 1.0f);
					JsonValue scale = e.get("scale");
					if( scale != null ) {
						scl.set(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
						//Gdx.app.log("LevelLoader.getComponents", "Scaling: " + scl.toString());
					}
					
					JsonValue euler = e.get("euler");
					JsonValue quaternion = e.get("quaternion");					
					if( euler != null ) {
						//Gdx.app.log("LevelLoader.getComponents", "Euler: " + euler.getFloat(0) + ", " + euler.getFloat(1) + ", " + euler.getFloat(2));
						
						Quaternion quat = new Quaternion();
						quat.setEulerAngles(euler.getFloat(1), euler.getFloat(0), euler.getFloat(2));
						o.transform = new Matrix4(pos, quat, scl);
					} else if ( quaternion != null ) {
						//Gdx.app.log("LevelLoader.getComponents", "Quaternion: " + quaternion.getFloat(0) + ", " + quaternion.getFloat(1) + ", " + quaternion.getFloat(2) + ", " + quaternion.getFloat(3));
						
						Quaternion quat = new Quaternion(quaternion.getFloat(0), quaternion.getFloat(1), quaternion.getFloat(2), quaternion.getFloat(3));
						o.transform = new Matrix4(pos, quat, scl);
					} else {
						o.transform = new Matrix4();
						o.transform.trn(pos);
						o.transform.scl(scl);
					}
				} else {
					o.transform = new Matrix4();
					//Gdx.app.log("LevelLoader.getComponents", "No 3D info found: " + o.transform.toString());
				}
				
				JsonValue linearVelocity = e.get("linear_velocity");
				if(linearVelocity != null) {
					o.setStartLinearVelocity(new Vector3(linearVelocity.getFloat(0), linearVelocity.getFloat(1), linearVelocity.getFloat(2)));
				}
				JsonValue angularVelocity = e.get("angular_velocity");
				if(angularVelocity != null) {
					o.setStartAngularVelocity(new Vector3(angularVelocity.getFloat(0), angularVelocity.getFloat(1), angularVelocity.getFloat(2)));
				}
				
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
			//Gdx.app.log("LevelLoader.getDependencies", name);
			deps.add(new AssetDescriptor<GameModel>(name, GameModel.class));
		}
		return deps;
	}

	static public class LevelParameters extends AssetLoaderParameters<Level> {}
}