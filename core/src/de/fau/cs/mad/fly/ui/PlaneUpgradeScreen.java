package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.graphics.shaders.FlyShaderProvider;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.res.Assets;

/**
 * The Screen in which the Player can upgrade his Planes
 * 
 * @author Sebastian
 * 
 */
public class PlaneUpgradeScreen implements Screen {
    private PlaneUpgradesOverlay upgradeOverlay;
    
    private Skin skin;
    private Stage stage;
    private Viewport viewport;
    protected final Color backgroundColor;
    private Batch backgroundBatch;
    private Sprite background;
    
    private InputMultiplexer inputProcessor;
    
    private float screenHeight = Gdx.graphics.getHeight();
    private float screenWidth = Gdx.graphics.getWidth();
    
    private Label nameLabel, speedLabel, rollingSpeedLabel, azimuthSpeedLabel, livesLabel;
    private String name, speed, pitch, turnSpeed, lives;
    
    private IPlane.Head currentPlane;
    
    private ModelBatch batch;
    private Environment environment;
    private PerspectiveCamera camera;   
    
    private GameObject currentSpaceship;
    private Vector3 xRotationAxis = new Vector3(1.f, 0.f, 0.f);
    private Vector3 yRotationAxis = new Vector3(0.f, 1.f, 0.f);
    
    public PlaneUpgradeScreen() {
        
        currentPlane = PlaneManager.getInstance().getChosenPlane();
        
        name = I18n.t("name");
        speed = I18n.t("speed");
        pitch = I18n.t("pitch");
        turnSpeed = I18n.t("turnSpeed");
        lives = I18n.t("lives");
        
        setUpEnvironment();
        setUpCamera();
        
        skin = SkinManager.getInstance().getSkin();
        backgroundColor = skin.getColor(UI.Window.BACKGROUND_COLOR);
        
        backgroundBatch = new SpriteBatch();
        
        batch = new ModelBatch(null, new FlyShaderProvider(), null);
        
        // initialize the stage
        stage = new Stage();
        float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) screenWidth;
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) screenHeight;
        float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
        
        // adding the background
        initBackground();
        
        // load the plane
        loadCurrentPlane();
        
        // initialize the upgradeOverlay
        upgradeOverlay = new PlaneUpgradesOverlay(skin, stage, this);
        upgradeOverlay.init();
        
        // initializing the overlay which contains the details of the current
        // spaceship
        initOverlay();
        
        // initialize the InputProcessor
        inputProcessor = new InputMultiplexer(stage, new BackProcessor());
    }
    
    /**
     * Getter for the UpgradesOverlay
     * 
     * @return The Overlay that contains the Buttons and Labels for the Upgrades
     */
    public PlaneUpgradesOverlay getOverlay() {
        return upgradeOverlay;
    }
    
    /**
     * Updates the overlay to the current status of the current plane
     */
    public void update() {
        updateOverlay();
    }
    
    /**
     * Initiates the background of the screen
     */
    private void initBackground() {
        Assets.load(new AssetDescriptor<Texture>("background.jpg", Texture.class));
        
        background = new Sprite(Assets.manager.get(new AssetDescriptor<Texture>("background.jpg", Texture.class)));
        float xSkalingFactor = Gdx.graphics.getWidth() / background.getWidth();
        float ySkalingFactor = Gdx.graphics.getHeight() / background.getHeight();
        float deltaX = 0f;
        float deltaY = 0f;
        background.setOrigin(0, 0);
        if (xSkalingFactor >= ySkalingFactor) {
            background.setScale(xSkalingFactor);
            deltaY = (Gdx.graphics.getHeight() - background.getHeight() * xSkalingFactor) / 2.0f;
        } else {
            background.setScale(ySkalingFactor);
            deltaX = (Gdx.graphics.getWidth() - background.getWidth() * ySkalingFactor) / 2.0f;
        }
        background.setPosition(deltaX, deltaY);
    }
    
    private void loadCurrentPlane() {
        String ref = "models/planes/" + currentPlane.modelRef + "/" + currentPlane.modelRef;
        
        Assets.load(new AssetDescriptor<GameModel>(ref, GameModel.class));
        GameModel model = Assets.manager.get(ref, GameModel.class);
        
        currentSpaceship = new GameObject(model, "spaceship");
        
        currentSpaceship.transform.rotate(yRotationAxis, 180.f);
        xRotationAxis.rotate(yRotationAxis, -180.f);
        currentSpaceship.transform.rotate(xRotationAxis, -20.f);
        yRotationAxis.rotate(xRotationAxis, 20.f);
    }
    
    /**
     * Initializes the overlay which contains the details of the current
     * spaceship
     */
    public void initOverlay() {
        // Calculates the Position to put the Overlay, so that it doesn't
        // overlap with the Buttons
        float xPos = Gdx.graphics.getWidth()*0.65f;//upgradeOverlay.getButtonWidth() * 2.8f;
        
        LabelStyle labelStyle = skin.get(LabelStyle.class);
        nameLabel = new Label("", labelStyle);
        stage.addActor(nameLabel);
        nameLabel.setPosition(xPos, 1000);
        
        speedLabel = new Label("", labelStyle);
        stage.addActor(speedLabel);
        speedLabel.setPosition(xPos, 800);
        
        rollingSpeedLabel = new Label("", labelStyle);
        stage.addActor(rollingSpeedLabel);
        rollingSpeedLabel.setPosition(xPos, 600);
        
        azimuthSpeedLabel = new Label("", labelStyle);
        stage.addActor(azimuthSpeedLabel);
        azimuthSpeedLabel.setPosition(xPos, 400);
        
        livesLabel = new Label("", labelStyle);
        stage.addActor(livesLabel);
        livesLabel.setPosition(xPos, 200);
    }
    
    /**
     * Updates the overlay with the details of the current plane
     */
    private void updateOverlay() {
        nameLabel.setText(name + ": " + currentPlane.name);
        speedLabel.setText(speed + ": " + Float.toString(currentPlane.speed));
        rollingSpeedLabel.setText(pitch + ": " + Float.toString(currentPlane.rollingSpeed));
        azimuthSpeedLabel.setText(turnSpeed + ": " + Float.toString(currentPlane.azimuthSpeed));
        livesLabel.setText(lives + ": " + Integer.toString(currentPlane.lives));
    }
    
    /**
     * Adds the Overlay with information about the current plane to the screen
     */
    public void addOverlay() {
        stage.addActor(nameLabel);
        stage.addActor(speedLabel);
        stage.addActor(rollingSpeedLabel);
        stage.addActor(azimuthSpeedLabel);
        stage.addActor(livesLabel);
    }
    
    /**
     * Sets up the environment for the level with its light.
     */
    private void setUpEnvironment() {
    	environment = new Environment();
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
        
        //camVec = camera.position.cpy();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        backgroundBatch.begin();
        background.draw(backgroundBatch);
        backgroundBatch.end();
        
        batch.begin(camera);
        currentSpaceship.render(batch, environment, camera);
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
        currentPlane = PlaneManager.getInstance().getChosenPlane();
        
        initBackground();
        
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(inputProcessor);
        
        // adding the preview of the first plane
        String ref = "models/planes/" + currentPlane.modelRef + "/" + currentPlane.modelRef;
        Assets.load(new AssetDescriptor<GameModel>(ref, GameModel.class));
        GameModel model = Assets.manager.get(ref, GameModel.class);
        
        currentSpaceship = new GameObject(model, "spaceship");
        
        currentSpaceship.transform.rotate(yRotationAxis, 180.f);
        xRotationAxis.rotate(yRotationAxis, -180.f);
        currentSpaceship.transform.rotate(xRotationAxis, -20.f);
        yRotationAxis.rotate(xRotationAxis, 20.f);
        
        addOverlay();
        update();
        upgradeOverlay.show();
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
    
}
