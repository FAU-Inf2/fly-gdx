package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Displays and updates the 2D overlay while the 3D world is rendered.
 * 
 * @author Tobias Zangl
 */
public class GameOverlay {
	private final Fly game;
	
	private Skin skin;
	private Stage stage;
	private Label timeDescription, timeCounter;
	private float time;
	private Label fpsDescription, fpsCounter;
	private int framesPerSecond;
	
	private float steeringX, steeringY;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	
	private boolean showOverlay = false;
	
	public GameOverlay(final Fly game) {
		this.game = game;
		
		skin = game.getSkin();
		
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		steeringX = 0.0f;
		steeringY = 0.0f;

		LabelStyle labelStyle = new LabelStyle(skin.getFont("default-font"), Color.RED);
		
		timeDescription = addLabel("Time:", labelStyle, 0.05f, 0.0f);
		timeCounter = addLabel("0", labelStyle, 0.15f, 0.0f);
		
		fpsDescription = addLabel("FPS:", labelStyle, 0.05f, 0.92f);
		fpsCounter = addLabel("0", labelStyle, 0.15f, 0.92f);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		shapeRenderer = new ShapeRenderer();

		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("arrow.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		  
		TextureRegion region = new TextureRegion(texture, 0, 0, 128, 256);
		
		sprite = new Sprite(region);
		sprite.setSize(50, 100);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(game.getAbsoluteX(0.35f), game.getAbsoluteY(0.25f));

		initOverlay();
	}

	/**
	 * Adds a Label to the screen.
	 * @param text
	 *            the default text for the Label
	 * @param labelStyle
	 *            the LabelStyle for the Label
	 * @param x
	 *            the x position on the screen in percent, should be between -100.0, +100.0
	 * @param y
	 *            the y position on the screen in percent, should be between -100.0, +100.0
	 */
	public Label addLabel(String text, LabelStyle labelStyle, float x, float y) {
		Label label = new Label(text, labelStyle);
		label.setPosition(20.0f, 0.0f);
		label.setPosition(game.getAbsoluteX(x), game.getAbsoluteY(y));
		stage.addActor(label);
		return label;
	}
	
	/**
	 * Resets the Overlay and its values.
	 */
	public void initOverlay() {
		time = 0.0f;
		
		showOverlay = game.getSettingManager().getCheckBoxValue("showOverlay");

		if(!game.getSettingManager().getCheckBoxValue("showTime")) {
			timeDescription.setVisible(false);
			timeCounter.setVisible(false);
		} else {
			timeDescription.setVisible(true);
			timeCounter.setVisible(true);
		}
		
		if(!game.getSettingManager().getCheckBoxValue("showFPS")) {
			fpsDescription.setVisible(false);
			fpsCounter.setVisible(false);
		} else {
			fpsDescription.setVisible(true);
			fpsCounter.setVisible(true);
		}
	}
	
	/**
	 * Renders the Overlay.
	 * @param delta
	 *            the difference in seconds since the last render call
	 */
	public void render(float delta) {
		if(!showOverlay)
			return;
		
		framesPerSecond = (int) (1.0 / delta);

		timeCounter.setText(String.valueOf((long) time));
		fpsCounter.setText(String.valueOf(framesPerSecond));
		stage.act(delta);
		stage.draw();
		
		sprite.rotate(- delta * 50.0f);
		
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		sprite.draw(batch);
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.combined);
			      
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(game.getAbsoluteX(0.2f), - game.getAbsoluteY(0.48f), game.getAbsoluteX(0.25f), game.getAbsoluteX(0.05f), Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW);
		drawSteering();
		shapeRenderer.end();
		
		time += delta;
	}
	
	/**
	 * Draws a dot for the steering.
	 */
	private void drawSteering() {
		shapeRenderer.setColor(1.0f, 0.2f, 0.2f, 1.0f);
		shapeRenderer.circle(game.getAbsoluteX(0.01f) * steeringX, game.getAbsoluteY(0.01f) * steeringY, game.getAbsoluteX(0.01f), 20);
	}
	
	/**
	 * Setter for the Steering.
	 * @param x
	 *            the value in x direction of the steering, should be between -30.0, +30.0
	 * @param y
	 *            the value in  direction of the steering, should be between -30.0, +30.0
	 */
	public void setSteering(float x, float y) {
		steeringX = x;
		steeringY = y;
	}
	
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}
}
