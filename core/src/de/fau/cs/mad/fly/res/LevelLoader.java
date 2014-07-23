package de.fau.cs.mad.fly.res;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
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

/**
 * Created by danyel on 26/05/14.
 */
public class LevelLoader extends AsynchronousAssetLoader<Level, LevelLoader.LevelParameters> {
    Level level;
    private final JsonReader reader;
    private final Json auto;
    private JsonValue json;
    private Map<String, GameModel> models;
    private Map<String, String> dependencies;
    private Map<String, GameObject> components;
    private AssetManager manager;
    private FileHandle file;
    private LevelParameters parameter;
    private final Matrix4 identityMatrix = new Matrix4();
    
    /**
     * Constructor, sets the
     * {@link com.badlogic.gdx.assets.loaders.FileHandleResolver} to use to
     * resolve the file associated with the asset name.
     * */
    public LevelLoader() {
        super(new InternalFileHandleResolver());
        reader = new JsonReader();
        auto = new Json();
        models = new HashMap<String, GameModel>();
        components = new HashMap<String, GameObject>();
    }
    
    public Level fromJson() {
        parseJson();
        for (Map.Entry<String, String> e : dependencies.entrySet()) {
            models.put(e.getKey(), dependencyFor(e.getKey()));
        }
        parseComponents();
        Perspective start = auto.fromJson(Perspective.class, json.get("start").toString());
        JsonValue gates = json.get("gates");
        Map<Integer, Gate> gateMap = new HashMap<Integer, Gate>();
        
        Gate p;
        Gate dummy = null;
        int len = gates.size;
        JsonValue jsonGate;
        for (int i = 0; i < len; i++) {
            jsonGate = gates.get(i);
            JsonValue gid = jsonGate.get("id");
            if (gid != null) {
                p = new Gate(gid.asInt());
                p.display = components.get(jsonGate.getString("display"));
                p.goal = components.get(jsonGate.getString("goal"));
                // TODO: make this explicit (don't just blacklist identity
                // matrix...). Currently this makes sure that the right gate is marked as next gate
                if (Arrays.equals(p.goal.transform.getValues(), identityMatrix.getValues()))
                    p.goal.transform = p.display.transform.cpy();
                gateMap.put(p.id, p);
            }
            else {
                p = new Gate(-1);
                dummy = p;
            }
            p.successors = jsonGate.get("successors").asIntArray();
        }
        
        if (dummy == null)
            throw new RuntimeException("No dummy gate found.");
        
        ArrayList<GameObject> componentsList = new ArrayList<GameObject>();
        componentsList.addAll(components.values());
        Level level = new Level(json.getString("name"), start, componentsList, models, dummy);
        JsonValue levelClass = json.get("class");
        if (levelClass != null) {
        	level.levelClass = levelClass.asString();
        }
        level.setGates(gateMap);
        level.head.id = json.getInt("id");
        level.setLeftTime(json.getInt("time"));
        
        return level;
    }
    
    private void parseJson() {
        if (json == null)
            json = reader.parse(file);
    }
    
    private void parseDependencies() {
        if (dependencies == null) {
            parseJson();
            dependencies = new HashMap<String, String>();
            JsonValue e;
            JsonValue jsonDependencies = json.get("dependencies"); 
            int len = jsonDependencies.size;
            for (int i = 0; i < len; i++) {
                e = jsonDependencies.get(i);
                String fileName = e.asString();
                dependencies.put(e.name(), fileName);
            }
        }
    }
    
    private void parseComponents() {
        
        parseJson();
        components.clear();
        GameObject o;
        for (JsonValue e : json.get("components")) {
            String ref = e.getString("ref");
            long millis = System.currentTimeMillis();
            o = new GameObject(models.get(ref));
            Gdx.app.log("LevelLoader.parseComponents", "GameObject: " + String.valueOf(System.currentTimeMillis()-millis));
            o.modelId = ref;
            o.id = e.getString("id");
            JsonValue visible = e.get("visible");
            if (visible != null && !visible.asBoolean()) {
            	o.hide();
            }
            
            JsonValue transform = e.get("transformMatrix");
            JsonValue position = e.get("position");
            if (transform != null) {
                o.transform.set(transform.asFloatArray());
                // Gdx.app.log("LevelLoader.getComponents",
                // "TransformMatrix: " + o.transform.toString());
            } else if (position != null) {
                Vector3 pos = new Vector3(position.asFloatArray());
                // Gdx.app.log("LevelLoader.getComponents", "Position: " + pos.toString());
                
                Vector3 scl = new Vector3(1.0f, 1.0f, 1.0f);
                JsonValue scale = e.get("scale");
                if (scale != null) {
                    scl.set(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
                    // Gdx.app.log("LevelLoader.getComponents", "Scaling: "
                    // + scl.toString());
                }
                
                JsonValue euler = e.get("euler");
                JsonValue quaternion = e.get("quaternion");
                if (euler != null) {
                    // Gdx.app.log("LevelLoader.getComponents", "Euler: " +
                    // euler.getFloat(0) + ", " + euler.getFloat(1) + ", " +
                    // euler.getFloat(2));
                    
                    Quaternion quat = new Quaternion();
                    quat.setEulerAngles(euler.getFloat(1), euler.getFloat(0), euler.getFloat(2));
                    o.transform.set(pos, quat, scl);
                } else if (quaternion != null) {
                    // Gdx.app.log("LevelLoader.getComponents",
                    // "Quaternion: " + quaternion.getFloat(0) + ", " +
                    // quaternion.getFloat(1) + ", " +
                    // quaternion.getFloat(2) + ", " +
                    // quaternion.getFloat(3));
                    
                    Quaternion quat = new Quaternion(quaternion.getFloat(0), quaternion.getFloat(1), quaternion.getFloat(2), quaternion.getFloat(3));
                    o.transform.set(pos, quat, scl);
                } else {
                    o.transform.idt();
                    o.transform.trn(pos);
                    o.transform.scl(scl);
                }
            } else {
                o.transform.idt();
                // Gdx.app.log("LevelLoader.getComponents",
                // "No 3D info found: " + o.transform.toString());
            }
            
            JsonValue linearVelocity = e.get("linear_velocity");
            if (linearVelocity != null) {
                o.setStartLinearVelocity(new Vector3(linearVelocity.getFloat(0), linearVelocity.getFloat(1), linearVelocity.getFloat(2)));
            }
            JsonValue angularVelocity = e.get("angular_velocity");
            if (angularVelocity != null) {
                o.setStartAngularVelocity(new Vector3(angularVelocity.getFloat(0), angularVelocity.getFloat(1), angularVelocity.getFloat(2)));
            }
            
            components.put(o.id, o);
        }
    }
    
    private GameModel dependencyFor(String ref) {
        return manager.get(dependencies.get(ref), GameModel.class);
    }
    
    private void deinit() {
        level = null;
        json = null;
        components.clear();
        dependencies = null;
        file = null;
        parameter = null;
        manager = null;
        // Models are needed for the features, dont clear them!
        //models.clear();
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
        this.parameter = parameter;
        parseDependencies();
        Array<AssetDescriptor> deps = new Array<AssetDescriptor>();        
        for (String name : dependencies.values()) {
            // Gdx.app.log("LevelLoader.getDependencies", name);
            deps.add(new AssetDescriptor<GameModel>(name, GameModel.class));
        }
        return deps;
    }
    
    static public class LevelParameters extends AssetLoaderParameters<Level> {
    }
}