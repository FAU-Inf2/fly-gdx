package de.fau.cs.mad.fly;

import java.io.FileNotFoundException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.levelLoader.LevelManager;

/**
 * Displays the 3D-world.
 *
 * @author Tobias Zangl
 */
public class GameScreen implements Screen {
	private final Fly game;
	
	private float startRoll, startPitch;

	private PerspectiveCamera camera;
	private float cameraSpeed = 0.1f;

	private ModelBatch batch;
	private Model model;
	private Model[] skyBox = new Model[6];
    private ModelInstance instance;
    private ModelInstance[] skyBoxInstance = new ModelInstance[6];
    private Environment environment;	
	
	public GameScreen(final Fly game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		if(Gdx.input.justTouched()) {
			LevelManager lm = new LevelManager();
			try {
				lm.loadLevel("level1");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			game.setMainMenuScreen();
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		
		
		// rotating the camera according to UserInput
		interpretUserInput();
		
		// move the camera (first person flight)
		Vector3 dir = new Vector3(camera.direction.x, camera.direction.y, camera.direction.z);
		camera.translate(dir.scl(cameraSpeed));
		camera.update();
		
		// rendering a small box and a skybox 
		batch.begin(camera);
		batch.render(instance,environment);
		for(int i = 0; i < 6; i++){
			batch.render(skyBoxInstance[i],environment);
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		setUpCamera();
		setUpEnvironment();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		batch.dispose();
		model.dispose();
		for(int i = 0; i < 6; i++) {
        	skyBox[i].dispose();
        }
	}
	
	/**
	 * Sets up the camera
	 */
	private void setUpCamera() {
        
		// initializing Roll- and Pitch-Values for later comparison
		startRoll = Gdx.input.getRoll();
		startPitch = Gdx.input.getPitch();
		
        // setting up the camera
		float screenHeight = Gdx.graphics.getHeight();
		float screenWidth = Gdx.graphics.getWidth();
		camera = new PerspectiveCamera(67, screenWidth, screenHeight);
		camera.position.set(0, 0, 40);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 200f;
		camera.update();
        
	}
	
	/**
	 *  Sets up the environment in which the camera should fly
	 */
	private void setUpEnvironment() {
		batch = new ModelBatch();
		
		// setting up the environment
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		// create a small box at (0,0,0)
		ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
        instance = new ModelInstance(model); 
        
        // creating a skybox where each plane has a different color
        skyBox[0] = modelBuilder.createRect(-50f, -50f, -50f, 50f, -50f, -50f, 50f, 50f, -50f, -50f, 50f, -50f, 0, 0, 1, new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)), Usage.Position | Usage.Normal);
        skyBox[1] = modelBuilder.createRect(-50f, -50f, 50f, -50f, -50f, -50f, -50f, 50f, -50f, -50f, 50f, 50f, 1, 0, 0, new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);
        skyBox[2] = modelBuilder.createRect(-50f, 50f, -50f, 50f, 50f, -50f, 50f, 50f, 50f, -50f, 50f, 50f, 0, -1, 0, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)), Usage.Position | Usage.Normal);
        skyBox[3] = modelBuilder.createRect(-50f, -50f, 50f, 50f, -50f, 50f, 50f, -50f, -50f, -50f, -50f, -50f, 0, 1, 0, new Material(ColorAttribute.createDiffuse(Color.ORANGE)), Usage.Position | Usage.Normal);
        skyBox[4] = modelBuilder.createRect(50f, -50f, -50f, 50f, -50f, 50f, 50f, 50f, 50f, 50f, 50f, -50f, -1, 0, 0, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
        skyBox[5] = modelBuilder.createRect(50f, -50f, 50f, -50f, -50f, 50f, -50f, 50f, 50f, 50f, 50f, 50f, 0, 0, -1, new Material(ColorAttribute.createDiffuse(Color.GRAY)), Usage.Position | Usage.Normal);
        
        for(int i = 0; i < 6; i++) {
        	skyBoxInstance[i] = new ModelInstance(skyBox[i]);
        }
	}
	
	/**
	 * Interprets userinput like rotation of the smartphone or touches on the screen and rotates the camera accordingly
	 */
	private void interpretUserInput() {		
		float roll = Gdx.input.getRoll();
		float pitch = Gdx.input.getPitch();
		
		float difRoll = roll - startRoll;
		float difPitch = pitch - startPitch;
		
		// camera rotation according to smartphone rotation
		// rotation up or down
		if(difRoll > 10.0f) {
			camera.rotate(camera.direction.cpy().crs(camera.up), -1.0f);
		} else if(difRoll < -10.0f) {
			camera.rotate(camera.direction.cpy().crs(camera.up), 1.0f);
		}
		// rotation around camera.direction/viewDirection (roll)
		if(difPitch > 10.f) {
			camera.rotate(camera.direction, -1.0f);
		} else if(difPitch < -10.0f) {
			camera.rotate(camera.direction, 1.0f);
		}
		
		//Gdx.app.log("myApp", "direction: " + camera.direction);
		//Gdx.app.log("myApp", "up: " + camera.up);
		camera.update();
		
	}
}
