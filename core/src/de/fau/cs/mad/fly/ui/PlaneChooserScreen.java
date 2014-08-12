package de.fau.cs.mad.fly.ui;

import java.util.List;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.res.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Shows all available Spaceships and gives the user the ability to choose the one he wants to use
 * @author Sebastian
 *
 */
public class PlaneChooserScreen implements Screen, InputProcessor {
	
	private List<IPlane.Head> allPlanes;
	//private PlayerProfile profile;
	
	private GameObject instance;
	
	private IPlane.Head currentPlane;
	
	private TextButton rightButton;
	private TextButton leftButton;
	
	private Skin skin;
	protected final Color backgroundColor;
	
    private Stage stage;
    private ModelBatch batch;
	private Batch backgroundBatch;
    private Table table;
	private Sprite background;
	
	private Label nameLabel, speedLabel, rollingSpeedLabel, azimuthSpeedLabel, livesLabel;
	
    private InputMultiplexer inputProcessor;

    private Vector3 xAxis = new Vector3(1.f, 0.f, 0.f);
    private Vector3 yAxis = new Vector3(0.f, 1.f, 0.f);
    private Vector3 xRotationAxis = new Vector3(1.f, 0.f, 0.f);
    private Vector3 yRotationAxis = new Vector3(0.f, 1.f, 0.f);
    
    private Environment environment;
    private PerspectiveCamera camera;
	private float screenHeight = Gdx.graphics.getHeight();
	private float screenWidth = Gdx.graphics.getWidth();
	
	private Viewport viewport;
	
	private int xDif, yDif;
	private boolean touched;
	float xFactor = 0.f, yFactor = 0.f;
	private int lastX = 0, lastY = 0;

	public PlaneChooserScreen(/*PlayerProfile profile*/) {
		environment = new Environment();
		
		setUpEnvironment();
		setUpCamera();
		
		allPlanes = PlaneManager.getInstance().getSpaceshipList();
		
		skin = ((Fly) Gdx.app.getApplicationListener()).getSkin();
		backgroundColor = skin.getColor(UI.Window.BACKGROUND_COLOR);
		
		// initialize the stage
		stage = new Stage();
		float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) screenWidth;
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) screenHeight;
        float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
        
		batch = new ModelBatch();
		backgroundBatch = new SpriteBatch();
		Assets.load(new AssetDescriptor<Texture>("models/spacesphere/spacesphere.jpg", Texture.class));
		
		table = new Table(skin);
		table.bottom();
		table.setFillParent(true);
		leftButton = new TextButton(I18n.t("lastPlane"), skin, UI.Buttons.DEFAULT_STYLE);
		rightButton = new TextButton(I18n.t("nextPlane"), skin, UI.Buttons.DEFAULT_STYLE);
		
		leftButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				currentPlane = PlaneManager.getInstance().getNextPlane(1);
				loadCurrentPlane();
				updateOverlay();
				resetVectors();
			}
		});
		
		table.add(leftButton).bottom().left().pad(UI.Window.BORDER_SPACE);
		
		rightButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				currentPlane = PlaneManager.getInstance().getNextPlane(-1);
				loadCurrentPlane();
				updateOverlay();
				resetVectors();
			}
		});
		
		table.add(rightButton).bottom().right().pad(UI.Window.BORDER_SPACE);
		
		// adding the buttons overlay
		stage.addActor(table);
		
		// initializing the overlay which contains the details of the current spaceship
		initOverlay();
		
		// adding the background
		background = new Sprite(Assets.manager.get(new AssetDescriptor<Texture>("models/spacesphere/spacesphere.jpg", Texture.class)));
		float xSkalingFactor = Gdx.graphics.getWidth()/background.getWidth();
		float ySkalingFactor = Gdx.graphics.getHeight()/background.getHeight();
		float deltaX = 0f;
		float deltaY = 0f;
		background.setOrigin(0,0);
		if(xSkalingFactor >= ySkalingFactor) {
			background.setScale(xSkalingFactor);
			deltaY = (Gdx.graphics.getHeight() - background.getHeight() * xSkalingFactor)/2.0f;
		}
		else {
			background.setScale(ySkalingFactor);
			deltaX = (Gdx.graphics.getWidth() - background.getWidth() * ySkalingFactor)/2.0f;
		}
		background.setPosition(deltaX, deltaY);
		
		// initialize the InputProcessor
		inputProcessor = new InputMultiplexer(this, stage, new BackProcessor());
	}
	
	private void resetVectors() {
		xRotationAxis.set(1.f, 0.f, 0.f);
		yRotationAxis.set(0.f, 1.f, 0.f);
		xAxis.set(1.f, 0.f, 0.f);
		yAxis.set(0.f, 1.f, 0.f);
	}
	
	private void loadCurrentPlane() {
		String ref = "models/planes/" + currentPlane.modelRef + "/" + currentPlane.modelRef;
		
		Assets.load(new AssetDescriptor<GameModel>(ref, GameModel.class));
		GameModel model = Assets.manager.get(ref, GameModel.class);
		
		instance = new GameObject(model, "spaceship");
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if(!touched) {
			instance.transform.rotate(yRotationAxis, 0.2f);
			xRotationAxis.rotate(yRotationAxis, -0.2f);
		}
		backgroundBatch.begin();
		background.draw(backgroundBatch);
		backgroundBatch.end();
		
		batch.begin(camera);
		instance.render(batch, environment, camera);
		batch.end();
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputProcessor);
		
		currentPlane = PlaneManager.getInstance().getChosenPlane();
		
		// adding the preview of the first plane
		String ref = "models/planes/" + currentPlane.modelRef + "/" + currentPlane.modelRef;
		Assets.load(new AssetDescriptor<GameModel>(ref, GameModel.class));
		GameModel model = Assets.manager.get(ref, GameModel.class);
		
		instance = new GameObject(model, "spaceship");
		
		updateOverlay();
	}
	
	/**
	 * initializes the overlay which contains the details of the current spaceship
	 */
	private void initOverlay() {
		LabelStyle labelStyle = skin.get("red", LabelStyle.class);
		nameLabel = new Label("", labelStyle);
        stage.addActor(nameLabel);
        nameLabel.setPosition(100, viewport.getWorldHeight()-200);
        
        speedLabel = new Label("", labelStyle);
        stage.addActor(speedLabel);
        speedLabel.setPosition(100, viewport.getWorldHeight()-400);
        
        rollingSpeedLabel = new Label("", labelStyle);
        stage.addActor(rollingSpeedLabel);
        rollingSpeedLabel.setPosition(100, viewport.getWorldHeight()-600);
        
        azimuthSpeedLabel = new Label("", labelStyle);
        stage.addActor(azimuthSpeedLabel);
        azimuthSpeedLabel.setPosition(100, viewport.getWorldHeight()-800);
        
        livesLabel = new Label("", labelStyle);
        stage.addActor(livesLabel);
        livesLabel.setPosition(100, viewport.getWorldHeight()-1000);
	}
	
	String name = I18n.t("name");
	String speed = I18n.t("speed");
	String pitch = I18n.t("pitch");
	String turnSpeed = I18n.t("turnSpeed");
	String lives = I18n.t("lives");
	
	/**
	 * updates the overlay with the details of the current plane
	 */
	private void updateOverlay() {
		nameLabel.setText(name + ": " + currentPlane.name);
		speedLabel.setText(speed + ": " + Float.toString(currentPlane.speed));
		rollingSpeedLabel.setText(pitch + ": " + Float.toString(currentPlane.rollingSpeed));
		azimuthSpeedLabel.setText(turnSpeed + ": " + Float.toString(currentPlane.azimuthSpeed));
		livesLabel.setText(lives + ": " + Integer.toString(currentPlane.lives));
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	/**
     * Sets up the environment for the level with its light.
     */
    private void setUpEnvironment() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }
    
    /**
	 * Sets up the camera for the initial view.
	 */
	private final void setUpCamera() {
		camera = new PerspectiveCamera(67, screenWidth, screenHeight);
		
		camera.position.set(0.f, 0.f, -2.f);
		camera.lookAt(0.f, 0.f, 0.f);
		camera.near = 0.1f;
		camera.far = 100.f;
		camera.update();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touched = true;
		lastX = screenX;
		lastY = screenY;
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		touched = false;
		xFactor = 0;
		yFactor = 0;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		xDif = lastX - screenX;
		yDif = lastY - screenY;
		
		xFactor = -xDif / screenWidth;
		yFactor = yDif / screenHeight;
		
		instance.transform.rotate(yRotationAxis, xFactor * 360);
		instance.transform.rotate(xRotationAxis, yFactor * 360);
		yAxis = yRotationAxis;
		xAxis = xRotationAxis;
		xRotationAxis.rotate(yAxis, -xFactor * 360);
		yRotationAxis.rotate(xAxis, -yFactor * 360);
		
		lastX = screenX;
		lastY = screenY;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}