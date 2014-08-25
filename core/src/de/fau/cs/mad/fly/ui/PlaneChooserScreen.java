package de.fau.cs.mad.fly.ui;

import java.util.List;
import java.util.Map;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.overlay.PlaneUpgradesOverlay;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.player.IPlane.Head;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Shows all available Spaceships and gives the user the ability to choose the one he wants to use
 * @author Sebastian
 *
 */
public class PlaneChooserScreen implements Screen, InputProcessor {
	
	//private List<IPlane.Head> allPlanes;
	private Map<Integer, IPlane.Head> allPlanes;
	//private PlayerProfile profile;
	
	private GameObject instance;
	
	private IPlane.Head currentPlane;
	
	private Skin skin;
	protected final Color backgroundColor;
	
    private Stage stage;
    private ModelBatch batch;
	private Batch backgroundBatch;
	private Sprite background;
	
	private Label nameLabel, speedLabel, rollingSpeedLabel, azimuthSpeedLabel, livesLabel;
	private PlaneUpgradesOverlay upgradeOverlay;
	
    private InputMultiplexer inputProcessor;

    private Vector3 xAxis = new Vector3(1.f, 0.f, 0.f);
    private Vector3 yAxis = new Vector3(0.f, 1.f, 0.f);
    private Vector3 xRotationAxis = new Vector3(1.f, 0.f, 0.f);
    private Vector3 yRotationAxis = new Vector3(0.f, 1.f, 0.f);
    private Vector3 camVec;
    
    private Environment environment;
    private PerspectiveCamera camera;
	private float screenHeight = Gdx.graphics.getHeight();
	private float screenWidth = Gdx.graphics.getWidth();
	
	private Viewport viewport;
	
	private int xDif, yDif;
	private boolean touched, secondTouch;
	private float xFactor = 0.f, yFactor = 0.f, touchDistance;
	private int lastX = 0, lastY = 0;
	private float scale = 1;
	
	private String name, speed, pitch, turnSpeed ,lives;

	public PlaneChooserScreen(/*PlayerProfile profile*/) {
		
		environment = new Environment();
		
		name = I18n.t("name");
		speed = I18n.t("speed");
		pitch = I18n.t("pitch");
		turnSpeed = I18n.t("turnSpeed");
		lives = I18n.t("lives");
		
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
		Assets.load(new AssetDescriptor<Texture>("spaceships/previews/background.jpg", Texture.class));
		
		// adding the table containing the buttons with preview of every plane
		Table scrollableTable = new Table(skin);
		scrollableTable.bottom();
		ScrollPane scrollPane = new ScrollPane(scrollableTable, skin);
		scrollPane.setScrollingDisabled(false, true);
		scrollPane.setPosition(0, 0);
		scrollPane.setBounds(0, 0, UI.Window.REFERENCE_WIDTH, UI.Window.REFERENCE_HEIGHT/3);
		scrollPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));
		
		int size = allPlanes.size();
		for(int i = 1; i <= size; i++) {
			
			Texture texture1 = new Texture(Gdx.files.internal("spaceships/previews/" + allPlanes.get(i).modelRef + ".png"));
			TextureRegion image = new TextureRegion(texture1);
			ImageButtonStyle style = new ImageButtonStyle(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
			style.imageUp = new TextureRegionDrawable(image);
			style.imageDown = new TextureRegionDrawable(image);
			
			ImageButton button = new ImageButton(style);
			final int index = i;
			
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					currentPlane = allPlanes.get(index);
					PlaneManager.getInstance().setChosenPlane(currentPlane);
					loadCurrentPlane();
					updateOverlay();
					resetVectors();
				}
			});
			
			scrollableTable.add(button);
		}
		
		stage.addActor(scrollPane);
		
		// initializing the overlay which contains the details of the current spaceship
		initOverlay();
		
		// adding the background
		background = new Sprite(Assets.manager.get(new AssetDescriptor<Texture>("spaceships/previews/background.jpg", Texture.class)));
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
		
		// initialize the upgradeOverlay
		upgradeOverlay = new PlaneUpgradesOverlay(skin, stage, this);
		upgradeOverlay.init();
		
		// initialize the InputProcessor
		//inputProcessor = new InputMultiplexer(this, stage, new BackProcessor());
		inputProcessor = new InputMultiplexer(stage, this, new BackProcessor());
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
	
	public void update() {
		updateOverlay();
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
		
		upgradeOverlay.show();
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
		
		camVec = camera.position.cpy();
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
		if(pointer == 0) {
			touched = true;
			lastX = screenX;
			lastY = screenY;
		} else if(pointer == 1) {
			secondTouch = true;
			float xDif = lastX - screenX;
			float yDif = lastY - screenY;
			touchDistance = (float) Math.sqrt(xDif * xDif + yDif * yDif);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0) {
			touched = false;
			xFactor = 0;
			yFactor = 0;
		}else if(pointer == 1) {
			secondTouch = false;
			touchDistance = 0;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pointer == 0) {
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
		} if(pointer == 1 && touched) {
			float xDif = lastX - screenX;
			float yDif = lastY - screenY;
			float newTouchDistance = (float) Math.sqrt(xDif * xDif + yDif * yDif);
			
			float scale = touchDistance / newTouchDistance;
			
			touchDistance = newTouchDistance;
			
			camera.translate(camVec.cpy().scl(scale - 1));
			camera.update();
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		scale = amount * 0.1f;
		camera.translate(camVec.cpy().scl(scale));
		camera.update();
		return false;
	}
}