package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.overlay.PlaneUpgradesOverlay;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.res.Assets;

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
	private String name, speed, pitch, turnSpeed ,lives;

	private IPlane.Head currentPlane;
	
	public PlaneUpgradeScreen() {
		
		currentPlane = PlaneManager.getInstance().getChosenPlane();

		name = I18n.t("name");
		speed = I18n.t("speed");
		pitch = I18n.t("pitch");
		turnSpeed = I18n.t("turnSpeed");
		lives = I18n.t("lives");
		
		skin = ((Fly) Gdx.app.getApplicationListener()).getSkin();
		backgroundColor = skin.getColor(UI.Window.BACKGROUND_COLOR);
		
		backgroundBatch = new SpriteBatch();
		
		// initialize the stage
		stage = new Stage();
		float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) screenWidth;
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) screenHeight;
        float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
        
        // adding the background
        initBackground();
        
        // initializing the overlay which contains the details of the current spaceship
     	initOverlay();
		
		// initialize the upgradeOverlay
		upgradeOverlay = new PlaneUpgradesOverlay(skin, stage, this);
		upgradeOverlay.init();
		
		// initialize the InputProcessor
		inputProcessor = new InputMultiplexer(stage, new BackProcessor());
	}
	
	public void update() {
		updateOverlay();
	}
	
	public void initBackground() {
		Assets.load(new AssetDescriptor<Texture>("spaceships/previews/" + currentPlane.modelRef + ".png", Texture.class));
		
		background = new Sprite(Assets.manager.get(new AssetDescriptor<Texture>("spaceships/previews/" + currentPlane.modelRef + ".png", Texture.class)));
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
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		backgroundBatch.begin();
		background.draw(backgroundBatch);
		backgroundBatch.end();
		
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
		
		updateOverlay();
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
		stage.dispose();
	}

}
