package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Displays the help information.
 *
 * @author Tobias Zangl
 */
public class HelpScreen implements Screen {
	private final Fly game;
	
	private SpriteBatch batch;
	private Skin skin;
	private Stage stage;
	private Table table;
	
	public HelpScreen(final Fly game) {
		this.game = game;
		
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		if(Gdx.app.getType() == ApplicationType.Android) {
			stage = new Stage(new FillViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));
		} else {
			stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		}
		
		addHelp();
	}
	
	/**
	 * Adds the scrollable help text and the Back button to the help screen.
	 */
	private void addHelp() {
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		final String helpString = "Lorem ipsum dolor sit amet,\nconsectetur adipisicing elit,\nsed do eiusmod tempor incididunt ut\nlabore et dolore magna aliqua.\nUt enim ad minim veniam,\nquis nostrud exercitation ullamco\nlaboris nisi ut aliquip ex\nea commodo consequat.\nDuis aute irure dolor in reprehenderit\nin voluptate velit esse cillum dolore eu fugiat nulla pariatur.\nExcepteur sint occaecat cupidatat non proident,\nsunt in culpa qui officia deserunt mollit anim\nid est laborum.";
		final Label helpLabel = new Label(helpString, skin);
		
		final TextButton backButton = new TextButton("Back", skin, "default");
		
		backButton.addListener(new ClickListener() {			
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setMainMenuScreen();
			}
		});
		
		final Table helpTable = new Table();
		final ScrollPane helpPane = new ScrollPane(helpTable, skin);
		helpTable.add(helpLabel).pad(10f);
		helpPane.setFadeScrollBars(false);
		
		table.add(helpPane).width(400f).height(260f);
		table.row();
		table.add(backButton).width(200f).height(40f).pad(10f);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
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
		batch.dispose();
	}
}
