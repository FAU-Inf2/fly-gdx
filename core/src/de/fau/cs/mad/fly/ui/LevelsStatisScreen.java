package de.fau.cs.mad.fly.ui;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.PostHighscoreService;
import de.fau.cs.mad.fly.HttpClient.PostUserService;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.profile.ScoreManager;

/**
 * @author Qufang Fan
 *
 */
public class LevelsStatisScreen extends BasicScreen {
	
	private Table infoTable;
	private Table scoreTable;	
	
	private TextButton uploadScoreButton;
	private TextButton globalHighScoreButton;
	private TextButtonStyle textButtonStyle;
	
	private LevelGroup levelGroup;
	
	private void initButtons() {
		textButtonStyle = skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class);
		
		
		globalHighScoreButton = new TextButton("Global highscores",
				textButtonStyle);
		globalHighScoreButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setGlobalHighScoreScreen(levelGroup);
			}
		});
	}

	public LevelsStatisScreen(LevelGroup group){
		this.levelGroup = group;
	}
	/* (non-Javadoc)
	 * @see de.fau.cs.mad.fly.ui.BasicScreen#generateContent()
	 */
	@Override
	protected void generateContent() {
		long begin = System.currentTimeMillis();
		initButtons();
		stage.clear();
		final Table table = new Table();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		table.debug();
		stage.addActor(table);
		
		infoTable = new Table();
		
		scoreTable = new Table();
		scoreTable.add(new Label("Loading...", skin));
		infoTable.add(scoreTable);

		final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
		statisticsPane.setFadeScrollBars(false);
		statisticsPane.setScrollingDisabled(true, false);
		statisticsPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE,
				ScrollPane.ScrollPaneStyle.class));
		table.row().expand();
		table.add(statisticsPane);
		Gdx.app.log("timing", "LevelsStatisScreen generateContent " + (System.currentTimeMillis()-begin));
	}
		
	@Override
	public void show() {
		long begin = System.currentTimeMillis();
		super.show();
		genarateScoreTable();
		Gdx.app.log("timing", "LevelsStatisScreen show " + (System.currentTimeMillis()-begin));
	}
	
	private void genarateScoreTable(){	
		new Thread(new showScore()).start();
	}

	public class showScore implements Runnable {
		
		Map<String, Score> scores;
		long begin,end;

		@Override
		public void run() {
			 begin = System.currentTimeMillis();
			scores = ScoreManager.getInstance().getPlayerBestScores(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), levelGroup);
			 end =System.currentTimeMillis();
			Gdx.app.log("timing", "get data from db " + (end-begin));
			begin = end;
			//it is faster if we remove this, but it may also bring in UI render exception
			Gdx.app.postRunnable(new Runnable() {				
				@Override
				public void run() {	
					end =System.currentTimeMillis();
					Gdx.app.log("timing", "begin run postrunable " + (end-begin));
					begin = end;
					scoreTable.clear();
					end =System.currentTimeMillis();
					Gdx.app.log("timing", "clear UI table " + (end-begin));
					begin = end;
					scoreTable.add(new Label(levelGroup.name, skin)).pad(6f).colspan(2);
					scoreTable.row().expand();
					
					// add scores details
					boolean haveScore = false;

					for (String levelID : scores.keySet()) {
						Score score = scores.get(levelID);
						if (score != null && score.getTotalScore() > 0) {
							haveScore = true;
							String levelname = levelGroup.getLevelName(Integer.valueOf(levelID));
							scoreTable.add(new Label(levelname, skin)).pad(6f).right();

							scoreTable.add(new Label(score.getTotalScore() + "", skin)).pad(6f)
									.uniform();
							for (ScoreDetail detail : score.getScoreDetails()) {
								scoreTable.row().expand();
								scoreTable.add(new Label(I18n.t(detail.getDetailName()), skin))
										.pad(6f).right();
								scoreTable.add(new Label(detail.getValue(), skin)).pad(6f)
										.uniform();
							}						

							Gdx.app.log("timing", " UI one score record UI builded " + (end-begin));
							uploadScoreButton = new TextButton(I18n.t("uploadScoreButtonText"),
									textButtonStyle);
							uploadScoreButton.addListener(new UploadScoreClickListener(levelGroup.id, Integer.valueOf(levelID), score.getTotalScore()));
							scoreTable.row().expand();
							scoreTable.add(uploadScoreButton).pad(6f).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).colspan(2);
							scoreTable.row().expand();
						}
					}

					// if no score at all
					if (!haveScore) {
						scoreTable.row().expand();
						scoreTable.add(new Label(I18n.t("noScore"), skin)).pad(6f).uniform();
					}

					// global high score button
					scoreTable.row().expand();
					scoreTable.add(new Label("", skin)).pad(6f).uniform();
					scoreTable.row().expand();
				
					scoreTable.add(globalHighScoreButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).colspan(2);

					//scoreTable.setColor(0, 0, 0, 0f);
					//scoreTable.addAction(Actions.fadeIn(2f));
					
					end =System.currentTimeMillis();
					Gdx.app.log("timing", " UI table builded " + (end-begin));
					begin = end;
					scoreTable.layout();
					end =System.currentTimeMillis();
					Gdx.app.log("timing", " UI table updated" + (end-begin));
					begin = end;
				}
			});

		}
	}
	
	public class UploadScoreClickListener extends ClickListener{
		
		private int levelgroupId;
		private int levelId;
		private int score;
		
		public UploadScoreClickListener( int levelgroup, int level, int score){
			super();
			levelgroupId = levelgroup;
			levelId = level;
			this.score = score;	
		}
		@Override
		public void clicked(InputEvent event, float x, float y) {
			final PostHighscoreService.RequestData requestData = new PostHighscoreService.RequestData();
			requestData.FlyID = PlayerProfileManager.getInstance().getCurrentPlayerProfile()
					.getFlyID();
			requestData.LevelID = levelId;// Integer.valueOf(levelID);
			requestData.Score = score;//score.getTotalScore();
			final FlyHttpResponseListener postScoreListener = new PostScoreHttpRespListener();
			final PostHighscoreService postHighscoreService = new PostHighscoreService(
					postScoreListener, requestData);
			if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() <= 0) {
				FlyHttpResponseListener listener = new PostUserHttpRespListener(
						requestData, postHighscoreService);
				PostUserService postUser = new PostUserService(listener);

				postUser.execute(PlayerProfileManager.getInstance()
						.getCurrentPlayerProfile().getName());
			} else {
				postHighscoreService.execute();
			}
		}
	}

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
			PlayerProfileManager.getInstance().getCurrentPlayerProfile().setFlyID(flyID);
			PlayerProfileManager.getInstance().saveFlyID(PlayerProfileManager.getInstance().getCurrentPlayerProfile());
			requestData.FlyID = flyID;
			postHighscoreService.execute();
		}

		@Override
		public void failed(String msg) {
			final String msgg = msg;
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					new Dialog("", skin) {
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
//			Gdx.app.postRunnable(new Runnable() {
//				@Override
//				public void run() {
					new Dialog("", skin) {
						{
							text("Uploaded!");
							button("OK");
						}
					}.show(stage);
//				}
//			});
		}

		@Override
		public void failed(String msg) {
			final String msgg = msg;
			// Since we are downloading on a background thread, post
			// a runnable to touch ui
//			Gdx.app.postRunnable(new Runnable() {
//				@Override
//				public void run() {
					new Dialog("", skin) {
						{
							text(msgg.substring(0, 20) + "...");
							button("OK");
						}
					}.show(stage);
//				}
//			});
		}

		@Override
		public void cancelled() {
		}
	};	
		

}
