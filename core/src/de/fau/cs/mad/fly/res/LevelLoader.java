package de.fau.cs.mad.fly.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.fau.cs.mad.fly.features.upgrades.types.ChangePointsUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.ChangeSteeringUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.ChangeTimeUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.InstantSpeedUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.LinearSpeedUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.ResizeGatesUpgrade;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

/**
 * Created by danyel on 26/05/14.
 */
public class LevelLoader extends AsynchronousAssetLoader<Level, LevelLoader.LevelParameters> {
	private static final String MODEL_FOLDER = "models/";
	
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

        ArrayList<GameObject> componentsList = new ArrayList<GameObject>();
        componentsList.addAll(components.values());
        Level level = new Level(json.getString("name"), start, componentsList, models);
        JsonValue levelClass = json.get("class");
        if (levelClass != null) {
        	level.levelClass = levelClass.asString();
        }
        level.head.id = json.getInt("id");
        level.setLeftTime(json.getInt("time"));

        GateCircuit gateCircuit = parseGates();
        level.addGateCircuit(gateCircuit);
        
        level.setUpgrades(parseUpgrades());
        
        return level;
    }
    
    /**
     * Parses the gates in the level file and creates a gate circuit.
     * @return GateCircuit
     */
    private GateCircuit parseGates() {    	
        Map<Integer, GateGoal> gateMap = new HashMap<Integer, GateGoal>();
        
    	JsonValue gates = json.get("gates");
    	if(gates == null) {
    		return null;
    	}
    	
    	// dummy gate goal at the start
    	GateGoal dummyGate = null;

    	int len = gates.size;
    	JsonValue jsonGate;
    	GateDisplay display;
    	GateGoal goal;
    	
    	for (int i = 0; i < len; i++) {
            jsonGate = gates.get(i);
            JsonValue gateId = jsonGate.get("gateId");
            
            if (gateId != null) {
	            String ref = jsonGate.getString("ref");
	            String refHole = jsonGate.getString("refHole");
	
	            display = new GateDisplay(models.get(ref));
	            goal = new GateGoal(gateId.asInt(), models.get(refHole), display);
	            goal.hide();
	            display.setGoal(goal);
	            
	            parseTransform(display, jsonGate);
	            goal.transform = display.transform.cpy();
	            gateMap.put(gateId.asInt(), goal);
            } else {
            	goal = new GateGoal(-1, models.get("hole"), null);
            	goal.hide();
	            dummyGate = goal;
            }

            goal.successors = jsonGate.get("successors").asIntArray();
            gateMap.put(goal.getId(), goal);
        }
    	
	    if (dummyGate == null) {
	        throw new RuntimeException("No dummy gate found.");
	    }
	    
        GateCircuit gateCircuit = new GateCircuit(dummyGate);
        gateCircuit.setGates(gateMap);
    	
    	return gateCircuit;
    }
    
    /**
     * Parses the additional information of the current game object like the id.
     * @param o		The game object.
     * @param e		The json value of the game object.
     */
    private void parseInformation(GameObject o, JsonValue e) {
    	o.id = e.getString("id");
    	
        JsonValue visible = e.get("visible");
        if (visible != null && !visible.asBoolean()) {
        	o.hide();
        }
    }
    
    /**
     * Parses the velocity information of the current game object.
     * @param o		The game object.
     * @param e		The json value of the game object.
     */
    private void parseVelocity(GameObject o, JsonValue e) {
        JsonValue linearVelocity = e.get("linear_velocity");
        if (linearVelocity != null) {
            o.setStartLinearVelocity(new Vector3(linearVelocity.getFloat(0), linearVelocity.getFloat(1), linearVelocity.getFloat(2)));
        }
        JsonValue angularVelocity = e.get("angular_velocity");
        if (angularVelocity != null) {
            o.setStartAngularVelocity(new Vector3(angularVelocity.getFloat(0), angularVelocity.getFloat(1), angularVelocity.getFloat(2)));
        }
    }
    
    /**
     * Parses the transform matrix of the current game object.
     * @param o		The game object.
     * @param e		The json value of the game object.
     */
    private void parseTransform(GameObject o, JsonValue e) {
        JsonValue transform = e.get("transformMatrix");
        JsonValue position = e.get("position");
        if (transform != null) {
            o.transform.set(transform.asFloatArray());
            // Gdx.app.log("LevelLoader.getComponents", "TransformMatrix: " + o.transform.toString());
        } else if (position != null) {
            Vector3 pos = new Vector3(position.asFloatArray());
            // Gdx.app.log("LevelLoader.getComponents", "Position: " + pos.toString());
            JsonValue scale = e.get("scale");
            if (scale != null) {
                o.scaling.set(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
            }
            
            JsonValue euler = e.get("euler");
            JsonValue quaternion = e.get("quaternion");
            if (euler != null) {
                // Gdx.app.log("LevelLoader.getComponents", "Euler: " + euler.getFloat(0) + ", " + euler.getFloat(1) + ", " + euler.getFloat(2));
                Quaternion quat = new Quaternion();
                quat.setEulerAngles(euler.getFloat(1), euler.getFloat(0), euler.getFloat(2));
                o.transform.set(pos, quat, new Vector3(1.0f, 1.0f, 1.0f));
            } else if (quaternion != null) {
                // Gdx.app.log("LevelLoader.getComponents", "Quaternion: " + quaternion.getFloat(0) + ", " + quaternion.getFloat(1) + ", " +
                // quaternion.getFloat(2) + ", " + quaternion.getFloat(3));
                Quaternion quat = new Quaternion(quaternion.getFloat(0), quaternion.getFloat(1), quaternion.getFloat(2), quaternion.getFloat(3));
                o.transform.set(pos, quat, new Vector3(1.0f, 1.0f, 1.0f));
            } else {
                o.transform.idt();
                o.transform.trn(pos);
            }
        } else {
            o.transform.idt();
            // Gdx.app.log("LevelLoader.getComponents", "No 3D info found: " + o.transform.toString());
        }
    }
    
    /**
     * Parses the upgrades in the level file.
     * @return list of collectibles.
     */
    private List<Collectible> parseUpgrades() {
    	List<Collectible> upgradeList = new ArrayList<Collectible>();
    	JsonValue upgrades = json.get("upgrades");
    	if(upgrades == null) {
    		return upgradeList;
    	}

    	int len = upgrades.size;
    	JsonValue jsonUpgrade;
    	
    	for (int i = 0; i < len; i++) {
            jsonUpgrade = upgrades.get(i);
            JsonValue upgradeType = jsonUpgrade.get("type");
            if (upgradeType != null) {
            	String type = upgradeType.asString();
            	Collectible c = null;
            	String ref = jsonUpgrade.getString("ref");
            	if(type.equals("ChangeTimeUpgrade")) {
            		c = new ChangeTimeUpgrade(models.get(ref), jsonUpgrade.get("time").asInt());
            	} else if(type.equals("ChangePointsUpgrade")) {
            		c = new ChangePointsUpgrade(models.get(ref), jsonUpgrade.get("points").asInt());
            	} else if(type.equals("InstantSpeedUpgrade")) {
            		c = new InstantSpeedUpgrade(models.get(ref), jsonUpgrade.get("speedFactor").asFloat(), jsonUpgrade.get("duration").asFloat());
            	} else if(type.equals("LinearSpeedUpgrade")) {
            		c = new LinearSpeedUpgrade(models.get(ref), jsonUpgrade.get("increaseFactor").asFloat(), jsonUpgrade.get("increaseDuration").asFloat(), jsonUpgrade.get("decreaseFactor").asFloat());
            	} else if(type.equals("ResizeGatesUpgrade")) {
            		JsonValue jsonScale = jsonUpgrade.get("scale");
            		Vector3 scale = new Vector3(jsonScale.getFloat(0), jsonScale.getFloat(1), jsonScale.getFloat(2));
            		c = new ResizeGatesUpgrade(models.get(ref), scale);
            	} else if(type.equals("ChangeSteeringUpgrade")) {
            		c = new ChangeSteeringUpgrade(models.get(ref), jsonUpgrade.get("roll").asFloat(), jsonUpgrade.get("azimuth").asFloat(), jsonUpgrade.get("duration").asFloat());
            	}
                
            	if(c != null) {            		
            		parseInformation(c, jsonUpgrade);
            		parseVelocity(c, jsonUpgrade);
            		parseTransform(c, jsonUpgrade);
            		
            		upgradeList.add(c);
            	} else {
            		Gdx.app.log("LevelLoader.parseUpgrades", "Upgrade type not found.");
            	}
            }
        }
    	
    	return upgradeList;
    }
    
    /**
     * Parses the json file.
     */
    private void parseJson() {
        if (json == null)
            json = reader.parse(file);
    }
    
    /**
     * Parses the dependencies.
     */
    private void parseDependencies() {
        if (dependencies == null) {
            parseJson();
            dependencies = new HashMap<String, String>();
            JsonValue e;
            JsonValue jsonDependencies = json.get("dependencies"); 
            int len = jsonDependencies.size;
            for (int i = 0; i < len; i++) {
                e = jsonDependencies.get(i);
                String fileName = MODEL_FOLDER + e.asString() + "/" + e.asString();
                dependencies.put(e.name(), fileName);
            }
        }
    }
    
    /**
     * Parses the components.
     * <p>
     * Components are all the decoration stuff in the level.
     */
    private void parseComponents() {
        parseJson();
        components.clear();
        GameObject o;
        for (JsonValue e : json.get("components")) {            
            String ref = e.getString("ref");
            o = new GameObject(models.get(ref));
            o.modelId = ref;

            parseInformation(o, e);	            
            parseTransform(o, e);
            parseVelocity(o, e);
            
            components.put(o.id, o);
            System.out.println(o.id);
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