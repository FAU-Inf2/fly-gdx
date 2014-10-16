package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
public class PlaneBasicScreen extends BasicScreenWithBackButton implements Screen {
    
    protected Skin skin;
    protected Viewport viewport;
    
    protected float screenHeight = Gdx.graphics.getHeight();
    protected float screenWidth = Gdx.graphics.getWidth();
    
    protected IPlane.Head currentPlane;
    
    protected ModelBatch batch;
    protected Environment environment;
    protected PerspectiveCamera camera;
    
    protected GameObject currentSpaceship;
    protected Vector3 xRotationAxis = new Vector3(1.f, 0.f, 0.f);
    protected Vector3 yRotationAxis = new Vector3(0.f, 1.f, 0.f);
    
    final LabelStyle labelStyle;
    
    /** Labels to show the currents status of the current plane */
    private Label nameLabel, speedLabel, azimuthSpeedLabel;
    
    private IPlane.Head displayedPlane = null;
    
    public PlaneBasicScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
        
        currentPlane = PlaneManager.getInstance().getChosenPlane();
        
        setUpEnvironment();
        setUpCamera();
        
        skin = SkinManager.getInstance().getSkin();
        labelStyle = skin.get(LabelStyle.class);
        
        batch = new ModelBatch(null, new FlyShaderProvider(), null);
        
        float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) screenWidth;
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) screenHeight;
        float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
    }
    
    /**
     * Initializes the overlay which contains the details of the current
     * spaceship
     */
    protected void initChosenPlaneDetail() {
        Table outTable = new Table();
        outTable.setFillParent(true);
        Table planeDetailTable = new Table();
        planeDetailTable.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
        
        nameLabel = new Label("", labelStyle);
        planeDetailTable.add(nameLabel).pad(UI.Tables.PADDING).colspan(2).center();
        planeDetailTable.row().expand();
        
        planeDetailTable.add(new Label(I18n.t("speed") + ":", labelStyle)).right();
        speedLabel = new Label("", labelStyle);
        planeDetailTable.add(speedLabel).right();
        planeDetailTable.row().expand();
        
        planeDetailTable.add(new Label(I18n.t("turnSpeed") + ":", labelStyle)).right();
        azimuthSpeedLabel = new Label("", labelStyle);
        planeDetailTable.add(azimuthSpeedLabel).right();
        
        outTable.add(planeDetailTable).top().left().expand().pad(UI.Window.BORDER_SPACE);
        stage.addActor(outTable);
    }
    
    /**
     * Updates the overlay with the details of the current plane
     */
    protected void updateChosenPlaneDetail() {
        nameLabel.setText(currentPlane.name);
        speedLabel.setText(Float.toString(currentPlane.speed));
        azimuthSpeedLabel.setText(Float.toString(currentPlane.azimuthSpeed));
    }
    
    protected void loadCurrentPlane() {
		if (displayedPlane == null || displayedPlane.id != currentPlane.id) {
			// adding the preview of the first plane
			String ref = "models/planes/" + currentPlane.modelRef + "/" + currentPlane.modelRef;

			Assets.load(new AssetDescriptor<GameModel>(ref, GameModel.class));
			GameModel model = Assets.manager.get(ref, GameModel.class);

			currentSpaceship = new GameObject(model, "spaceship");

			currentSpaceship.transform.rotate(yRotationAxis, 180.f);
			xRotationAxis.rotate(yRotationAxis, -180.f);
			currentSpaceship.transform.rotate(xRotationAxis, -20.f);
			yRotationAxis.rotate(xRotationAxis, 20.f);
			displayedPlane = currentPlane;
		}
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
    }
    
//    @Override
//    public void render(float delta) {
//        super.render(delta);
//        
//        batch.begin(camera);
//        currentSpaceship.render(batch, environment, camera);
//        batch.end();
//        
//        stage.act(delta);
//        stage.draw();
//    }
//    
    @Override
    public void moreRender(float delta){
    	 batch.begin(camera);
         currentSpaceship.render(batch, environment, camera);
         batch.end();
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
        loadCurrentPlane();
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
