package de.fau.cs.mad.fly.ui;

import java.util.List;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.res.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
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
public class PlaneChooserScreen implements Screen{
	
	private List<IPlane.Head> allPlanes;
	//private PlayerProfile profile;
	
	private IPlane.Head currentPlane;
	
	private TextButton rightButton;
	private TextButton leftButton;
	
	private Skin skin;
	protected final Color backgroundColor;
	
    private Stage stage;
    private Table table;
    private InputMultiplexer inputProcessor;
	
	 Viewport viewport;

	public PlaneChooserScreen(/*PlayerProfile profile*/) {

		
		//this.profile = profile;
		allPlanes = PlaneManager.getInstance().getSpaceshipList();
		
		skin = ((Fly) Gdx.app.getApplicationListener()).getSkin();
		backgroundColor = skin.getColor(UI.Window.BACKGROUND_COLOR);
		
		stage = new Stage();
		
		float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) Gdx.graphics.getWidth();
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) Gdx.graphics.getHeight();
        float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
		
		
		table = new Table(skin);
		table.bottom();
		table.setFillParent(true);
		//leftButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
		leftButton = new TextButton(I18n.t("lastPlane"), skin, UI.Buttons.DEFAULT_STYLE);
		
		//rightButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
		rightButton = new TextButton(I18n.t("nextPlane"), skin, UI.Buttons.DEFAULT_STYLE);
		
		leftButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("clickedLeft", "clickedLeft");
				currentPlane = PlaneManager.getInstance().getNextPlane(true);
				updateOverlay();
			}
		});
		
		table.add(leftButton).bottom().left().pad(UI.Window.BORDER_SPACE);
		
		rightButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("clickedRight", "clickedRight");
				currentPlane = PlaneManager.getInstance().getNextPlane(false);
				updateOverlay();
			}
		});
		
		table.add(rightButton).bottom().right().pad(UI.Window.BORDER_SPACE);
		
		// adding the overlay
		stage.addActor(table);
		
		LabelStyle labelStyle = skin.get("red", LabelStyle.class);
		nameLabel = new Label("", labelStyle);
        stage.addActor(nameLabel);
        nameLabel.setPosition(100, viewport.getWorldHeight()-200);
		
		// initialize the InputProcessor
		inputProcessor = new InputMultiplexer(stage, new BackProcessor());
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	Label nameLabel, speedLabel, rollingSpeedLabel, azimuthSpeedLabel, livesLabel;

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputProcessor);
		
		currentPlane = PlaneManager.getInstance().getChosenPlane();
		updateOverlay();
	}
	
	private void updateOverlay() {
		nameLabel.setText(I18n.t("name") + ": " + currentPlane.name);
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
		stage.dispose();
	}
}