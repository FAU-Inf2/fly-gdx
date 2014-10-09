package de.fau.cs.mad.fly.ui;

import java.util.Collection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.graphics.shaders.FlyShaderProvider;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.profile.PlaneUpgradeManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.PlaneUpgrade;

/**
 * The Screen in which the Player can upgrade his Planes
 * 
 * @author Sebastian
 * 
 */
public class PlaneUpgradeScreen extends BasicScreenWithBackButton implements Screen {

	private Skin skin;
    private Viewport viewport;    
    
    private float screenHeight = Gdx.graphics.getHeight();
    private float screenWidth = Gdx.graphics.getWidth();
    
    private IPlane.Head currentPlane;
    
    private ModelBatch batch;
    private Environment environment;
    private PerspectiveCamera camera;   
    
    private GameObject currentSpaceship;
    private Vector3 xRotationAxis = new Vector3(1.f, 0.f, 0.f);
    private Vector3 yRotationAxis = new Vector3(0.f, 1.f, 0.f);
    
    private Table upgradesListTable;
    final LabelStyle labelStyle;
    
    /** Labels to show the currents status of the current plane*/
    private Label nameLabel, speedLabel, rollingSpeedLabel, azimuthSpeedLabel, livesLabel;

    private PlaneUpgradeDetailScreen planeUpgradeDetailScreen;
    
	public PlaneUpgradeScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
        
        currentPlane = PlaneManager.getInstance().getChosenPlane();

        setUpEnvironment();
        setUpCamera();
        
        skin = SkinManager.getInstance().getSkin();       
        labelStyle = skin.get( LabelStyle.class);
        
        batch = new ModelBatch(null, new FlyShaderProvider(), null);
        
        float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) screenWidth;
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) screenHeight;
        float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
        
        upgradesListTable = new Table();
        
        initChosenPlaneDetail();
        initUpgradeButtons();
        generateBackButton();       
    }
    
	private void initUpgradeButtons() {
		Table outTable = new Table();
		outTable.setFillParent(true);
		outTable.pad(0f);
		
		ScrollPane scrollPane = new ScrollPane(outTable, skin);
		scrollPane.setFillParent(true);
		scrollPane.setFadeScrollBars(false);

		final Collection<PlaneUpgrade> upgrades = PlaneUpgradeManager.getInstance().getUpgradeList().values();

		// Creates one Button for each Upgrade
		for (final PlaneUpgrade upgrade : upgrades) {
			final TextButton button = new TextButton(I18n.t(upgrade.name), skin);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {					
					openUpgradeDetailScreen(upgrade);
				}
			});

			upgradesListTable.add(button).width(UI.Buttons.TEXT_BUTTON_WIDTH).pad(15f);
			upgradesListTable.row();
		}

		
		outTable.add(upgradesListTable).right().top().padRight(25f).padTop(0f).expand();
		stage.addActor(scrollPane);
	}
	
	 /**
     * Initializes the overlay which contains the details of the current
     * spaceship
     */
    private void initChosenPlaneDetail() {
        LabelStyle labelStyle = skin.get(LabelStyle.class);
        Table outTable = new Table();
        outTable.setFillParent(true);
        Table planeDetailTable = new Table();
        
        planeDetailTable.add(new Label(I18n.t("name") + ":", labelStyle)).pad(UI.Tables.PADDING).right();
        nameLabel = new Label("", labelStyle);
        planeDetailTable.add(nameLabel).pad(UI.Tables.PADDING).left();
        planeDetailTable.row().left().top().expand();
        
        planeDetailTable.add(new Label(I18n.t("speed") + ":", labelStyle)).pad(UI.Tables.PADDING).right();
        speedLabel = new Label("", labelStyle);
        planeDetailTable.add(speedLabel).pad(UI.Tables.PADDING).left();
        planeDetailTable.row().left().top().expand();
        
        planeDetailTable.add(new Label(I18n.t("pitch") + ":", labelStyle)).pad(UI.Tables.PADDING).right();
        rollingSpeedLabel = new Label("", labelStyle);
        planeDetailTable.add(rollingSpeedLabel).pad(UI.Tables.PADDING).left();
        planeDetailTable.row().left().top().expand();
        
        planeDetailTable.add(new Label(I18n.t("turnSpeed") + ":", labelStyle)).pad(UI.Tables.PADDING).right();
        azimuthSpeedLabel = new Label("", labelStyle);
        planeDetailTable.add(azimuthSpeedLabel).pad(UI.Tables.PADDING).left();
        planeDetailTable.row().left().top().expand();
        
        planeDetailTable.add(new Label(I18n.t("lives") + ":", labelStyle)).pad(UI.Tables.PADDING).right();
        livesLabel = new Label("", labelStyle);
        planeDetailTable.add(livesLabel).pad(UI.Tables.PADDING).left();
        planeDetailTable.row().left().top().expand();
        outTable.add(planeDetailTable).top().left().expand().pad(100f);
        stage.addActor(outTable);
    }
   
	 /**
     * Updates the overlay with the details of the current plane
     */
    private void updateChosenPlaneDetail() {
        nameLabel.setText(currentPlane.name);
        speedLabel.setText(Float.toString(currentPlane.speed));
        rollingSpeedLabel.setText(Float.toString(currentPlane.rollingSpeed));
        azimuthSpeedLabel.setText(Float.toString(currentPlane.azimuthSpeed));
        livesLabel.setText(Integer.toString(currentPlane.lives));
    }
    
    private void openUpgradeDetailScreen(PlaneUpgrade upgrade){
    	if( planeUpgradeDetailScreen == null ){
    		planeUpgradeDetailScreen = new PlaneUpgradeDetailScreen(this, upgrade);
    	}
    	planeUpgradeDetailScreen.setChosenUpgrade(upgrade);
    	planeUpgradeDetailScreen.set();
    }
	
    
    private void loadCurrentPlane() {
    	 // adding the preview of the first plane
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
       super.render(delta);
        
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

		updateChosenPlaneDetail();
	}
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
