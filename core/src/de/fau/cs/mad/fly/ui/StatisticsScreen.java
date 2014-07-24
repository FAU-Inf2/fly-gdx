package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.PostHighscoreService;
import de.fau.cs.mad.fly.HttpClient.PostUserService;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.profile.ScoreManager;

/**
 * UI for checking scores of user, also for add and change user
 * 
 * 
 * @author Qufang Fan
 */
public class StatisticsScreen extends BasicScreen {

	TextButton addUserButton;
	TextButton uploadScoreButton;
	TextButtonStyle textButtonStyle;
	Table infoTable;
	Table userTable;
	TextField newUserField;
	
	public void initButtons() {
		textButtonStyle = skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class);
		addUserButton = new TextButton(I18n.t("addUserButtonText"), textButtonStyle);

		addUserButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String name = newUserField.getText().trim();
				if (!name.equals(""))// todo more check
				{
					Player player = new Player();
					player.setName(name);

					PlayerManager.getInstance().savePlayer(player);
					generateContentDynamic();
				}
			}
		});
	}

	@Override
	protected void generateContent() {
		initButtons();
		stage.clear();
		final Table table = new Table();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);

		infoTable = new Table();

		final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
		statisticsPane.setFadeScrollBars(false);
		statisticsPane.setScrollingDisabled(true, false);
		statisticsPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE,
				ScrollPane.ScrollPaneStyle.class));
		table.row().expand();
		table.add(statisticsPane);
	}

	private Player player;

	public class PostUserHttpRespListener implements FlyHttpResponseListener {
		final PostHighscoreService.RequestData requestData;
		final PostHighscoreService postHighscoreService;

		public PostUserHttpRespListener(PostHighscoreService.RequestData data,
				PostHighscoreService service) {
			requestData = data;
			postHighscoreService = service;
		}

		@Override
		public void successful(Object obj) {

			int flyID = Integer.valueOf(obj.toString());
			PlayerManager.getInstance().getCurrentPlayer().setFlyID(flyID);
			PlayerManager.getInstance().saveFlyID(PlayerManager.getInstance().getCurrentPlayer());
			requestData.FlyID = flyID;
			postHighscoreService.execute();
		}

		@Override
		public void failed(String msg) {
			final String msgg = msg;
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					new Dialog("Failed", skin) {
						{
							text(msgg.substring(0, 20) + "...");
							button("OK");
						}
					}.show(stage);
				}
			});
		}

		@Override
		public void cancelled() {
		}
	}

	public class PostScoreHttpRespListener implements FlyHttpResponseListener {
		@Override
		public void successful(Object obj) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					new Dialog("Info", skin) {
						{
							text("Uploaded!");
							button("OK");
						}
					}.show(stage);
				}
			});
		}

		@Override
		public void failed(String msg) {
			final String msgg = msg;
			// Since we are downloading on a background thread, post
			// a runnable to touch ui
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					new Dialog("Failed", skin) {
						{
							text(msgg.substring(0, 20) + "...");
							button("OK");
						}
					}.show(stage);
				}
			});
		}

		@Override
		public void cancelled() {
		}
	};

	private void generateContentDynamic() {
		Gdx.app.log("StaticScreen", "begin generateContentDynamic " + System.currentTimeMillis());
		player = PlayerManager.getInstance().getCurrentPlayer();

		infoTable.clear();

		//add user name and user change& add buttons
		String userName = player.getName();
		infoTable.row().expand();

		infoTable.add(new Label(I18n.t("usernameLableText"), skin)).pad(6f).uniform();
		List userList = new List(skin);
		int i = 0;
		int index = 0;
		ArrayList<String> nameList = new ArrayList<String>();
		for (Player player : PlayerManager.getInstance().getAllPlayer()) {
			nameList.add(player.getName());
			if (userName.equals(player.getName())) {
				index = i;
			}
			i++;
		}

		userList.setItems(nameList.toArray());
		userList.setSelectedIndex(index);
		userList.getSelection().setRequired(false);	
		userList.getSelection().setToggle(true);
		infoTable.add(userList).pad(6f).uniform();

		infoTable.row().expand();
		
		newUserField = new TextField("", skin);
		newUserField.setBounds(0, 0, 200, 100);
		infoTable.add(newUserField).pad(6f).uniform();
		infoTable.add(addUserButton).pad(6f).uniform();
		
		//add scores details
		boolean haveScore = false;
		Map<String, Score> scores = ScoreManager.getInstance().getcurrentBestScores();
		for (String levelID : scores.keySet()) {
			Score score = scores.get(levelID);
			if (score != null && score.getTotalScore() > 0) {
				infoTable.row().expand();
				infoTable.add(new Label("", skin)).pad(6f).uniform();

				haveScore = true;
				infoTable.row().expand();
				String levelname = LevelManager.getInstance()
						.getLevelName(Integer.valueOf(levelID));
				infoTable.add(new Label(levelname, skin)).pad(6f).uniform();

				infoTable.add(new Label(score.getTotalScore() + "", skin)).pad(6f).uniform();
				for (ScoreDetail detail : score.getScoreDetails()) {
					infoTable.row().expand();
					infoTable.add(new Label(I18n.t(detail.getDetailName()), skin)).pad(6f)
							.uniform();
					infoTable.add(new Label(detail.getValue(), skin)).pad(6f).uniform();
				}

				final PostHighscoreService.RequestData requestData = new PostHighscoreService.RequestData();
				requestData.FlyID = PlayerManager.getInstance().getCurrentPlayer().getFlyID();
				requestData.LevelID = Integer.valueOf(levelID);
				requestData.Score = score.getTotalScore();
				final FlyHttpResponseListener postScoreListener = new PostScoreHttpRespListener();
				final PostHighscoreService postHighscoreService = new PostHighscoreService(
						postScoreListener, requestData);

				uploadScoreButton = new TextButton(I18n.t("uploadScoreButtonText"), textButtonStyle);
				uploadScoreButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						if (PlayerManager.getInstance().getCurrentPlayer().getFlyID() <= 0) {
							FlyHttpResponseListener listener = new PostUserHttpRespListener(
									requestData, postHighscoreService);
							PostUserService postUser = new PostUserService(listener);

							postUser.execute(PlayerManager.getInstance().getCurrentPlayer()
									.getName());
						} else {
							postHighscoreService.execute();
						}
					}
				});
				infoTable.row().expand();
				infoTable.add(uploadScoreButton).pad(7f).uniform();
			}
		}

		//if no score at all
		if (!haveScore) {
			infoTable.row().expand();
			infoTable.add(new Label(I18n.t("noScore"), skin)).pad(6f).uniform();
		}

		//global high score button
		infoTable.row().expand();
		infoTable.add(new Label("", skin)).pad(6f).uniform();
		infoTable.row().expand();
		TextButton globalHighScoreButton = new TextButton("Global high scores", textButtonStyle);
		globalHighScoreButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setGlobalHighScoreScreen();
			}
		});
		infoTable.add(globalHighScoreButton);
		Gdx.app.log("StaticScreen", "end generateContentDynamic " + System.currentTimeMillis());
	}
	
	@Override
	public void show() {
		super.show();
		generateContentDynamic();
	}
}
