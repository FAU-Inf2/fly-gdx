package de.fau.cs.mad.fly.ui;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
 * Display the highscores of all levels in one level group
 * 
 * @author Qufang Fan
 * 
 */
public class LevelsStatisScreen extends BasicScreen {
    
    private Table infoTable;
    private Table scoreTable;
    
    private TextButton uploadScoreButton;
    private TextButton globalHighScoreButton;
    
    private LevelGroup levelGroup;
    
    /**
     * init buttons, which don't need to be created dynamically
     */
    private void initButtons() {
        globalHighScoreButton = new TextButton(I18n.t("GlobalHighscores"), skin);
        globalHighScoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setGlobalHighScoreScreen(levelGroup);
            }
        });
    }
    
    public LevelsStatisScreen(LevelGroup group) {
        this.levelGroup = group;
    }
    
    /**
     * generate Content Display text "loading" at first, then start a new thread
     * to loading scores data from Database and display
     */
    @Override
    protected void generateContent() {
        long begin = System.currentTimeMillis();
        initButtons();
        stage.clear();
        final Table table = new Table();
        table.pad(UI.Window.BORDER_SPACE);
        table.setFillParent(true);
        stage.addActor(table);
        
        infoTable = new Table();
        
        scoreTable = new Table();
        scoreTable.add(new Label(I18n.t("StatusLoading"), skin));
        infoTable.add(scoreTable);
        
        final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
        statisticsPane.setFadeScrollBars(false);
        statisticsPane.setScrollingDisabled(true, false);
        statisticsPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));
        table.row().expand();
        table.add(statisticsPane);
        Gdx.app.log("timing", "LevelsStatisScreen generateContent " + (System.currentTimeMillis() - begin));
    }
    
    /**
     * start a new thread to loading scores data from Database and display
     */
    @Override
    public void show() {
        long begin = System.currentTimeMillis();
        super.show();
        genarateScoreTable();
        Gdx.app.log("timing", "LevelsStatisScreen show " + (System.currentTimeMillis() - begin));
    }
    
    /**
     * 
     * start a new thread to loading scores data from Database and display
     */
    private void genarateScoreTable() {
        new Thread(new showScore()).start();
    }
    
    /**
     * a thread for loading scores data from Database and display
     * 
     * @author Fan
     * 
     */
    public class showScore implements Runnable {
        
        Map<String, Score> scores;
        long begin, end;
        
        @Override
        public void run() {
            begin = System.currentTimeMillis();
            scores = ScoreManager.getInstance().getPlayerBestScores(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), levelGroup);
            end = System.currentTimeMillis();
            Gdx.app.log("timing", "get data from db " + (end - begin));
            begin = end;
            // it is faster if we remove this postRunnable, but it may also
            // bring in UI render exception
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    end = System.currentTimeMillis();
                    Gdx.app.log("timing", "begin run postrunable " + (end - begin));
                    begin = end;
                    scoreTable.clear();
                    end = System.currentTimeMillis();
                    Gdx.app.log("timing", "clear UI table " + (end - begin));
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
                            
                            scoreTable.add(new Label(score.getTotalScore() + "", skin)).pad(6f).uniform();
                            for (ScoreDetail detail : score.getScoreDetails()) {
                                scoreTable.row().expand();
                                scoreTable.add(new Label(I18n.t(detail.getDetailName()), skin)).pad(6f).right();
                                scoreTable.add(new Label(detail.getValue(), skin)).pad(6f);
                            }
                            
                            Gdx.app.log("timing", " UI one score record UI builded " + (end - begin));
                            uploadScoreButton = new TextButton(I18n.t("uploadScoreButtonText"), skin);
                            if(score.getIsUploaded()) {
                            	uploadScoreButton.setDisabled(true);
                            }
                            uploadScoreButton.addListener(new UploadScoreClickListener(levelGroup.id, Integer.valueOf(levelID), score.getTotalScore(),uploadScoreButton));
                            scoreTable.row().expand();
                            scoreTable.add(uploadScoreButton).pad(6f).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).colspan(2);
                            scoreTable.row().expand();
                        }
                        scoreTable.add(new Label("", skin)).pad(6f).uniform();
                        scoreTable.row().expand();
                    }
                    
                    // if no score at all
                    if (!haveScore) {
                        scoreTable.row().expand();
                        scoreTable.add(new Label(I18n.t("noScore"), skin)).pad(6f).uniform();
                    }
                    
                    // global high score button          
                    
                    scoreTable.add(globalHighScoreButton).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).colspan(2);
                    
                    end = System.currentTimeMillis();
                    Gdx.app.log("timing", " UI table builded " + (end - begin));
                    begin = end;
                    scoreTable.layout();
                    end = System.currentTimeMillis();
                    Gdx.app.log("timing", " UI table updated" + (end - begin));
                    begin = end;
                }
            });
            
        }
    }
    
    /**
     * Upload score to server if the current user have to fly-id (user id got
     * from server side) in database, then call another service to get fly-id
     * 
     * @author Lenovo
     * 
     */
    public class UploadScoreClickListener extends ChangeListener {
        
        private int levelgroupId;
        private int levelId;
        private int score;
        private TextButton button;
        
        public UploadScoreClickListener(int levelgroup, int level, int score, TextButton button) {
            super();
            levelgroupId = levelgroup;
            levelId = level;
            this.score = score;
            this.button = button;
        }
        
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            final PostHighscoreService.RequestData requestData = new PostHighscoreService.RequestData();
            requestData.FlyID = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID();
            requestData.LevelID = levelId;
            requestData.Score = score;
            requestData.LevelgroupID = levelgroupId;
            final FlyHttpResponseListener postScoreListener = new PostScoreHttpRespListener(button);
            final PostHighscoreService postHighscoreService = new PostHighscoreService(postScoreListener, requestData);
            // if the current user have to fly-id (user id got from server side)
            // in database,
            // then call another service PostUserService to get fly-id
            if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() <= 0) {
                FlyHttpResponseListener listener = new PostUserHttpRespListener(requestData, postHighscoreService);
                PostUserService postUser = new PostUserService(listener);
                
                postUser.execute(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName());
            } else {
                postHighscoreService.execute();
            }
        }
    }
    
    /**
     * call service to get fly-id from server
     * 
     * @author Fan
     * 
     */
    public class PostUserHttpRespListener implements FlyHttpResponseListener {
        final PostHighscoreService.RequestData requestData;
        final PostHighscoreService postHighscoreService;
        
        public PostUserHttpRespListener(PostHighscoreService.RequestData data, PostHighscoreService service) {
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
                    Dialog dialog = new Dialog("", skin, "dialog");
                    if(msgg!=null && msgg.length()>21){
                    	dialog.text(I18n.t("ConnectServerError") + msgg.substring(0, 20) + "...");
                    } else {
                    	dialog.text(I18n.t("ConnectServerError") + msgg);
                    }
                    TextButton button = new TextButton(I18n.t("ok"), skin);
                    dialog.button(button);
                    dialog.show(stage);
                }
            });
        }
        
        @Override
        public void cancelled() {
        }
    }
    
    /**
     * call service to upload local highscore to server
     * 
     * @author Fan
     * 
     */
    public class PostScoreHttpRespListener implements FlyHttpResponseListener {
    	private TextButton button;
    	
    	public PostScoreHttpRespListener(TextButton button){
    		this.button = button;
    	}
        @Override
        public void successful(Object obj) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                	button.setDisabled(true);
                    Dialog dialog = new Dialog("", skin, "dialog");
                    dialog.text(I18n.t("ScoreUploaded"));
                    TextButton button = new TextButton(I18n.t("ok"), skin);
                    dialog.button(button);
                    dialog.show(stage);
                }
            });
        }
        
        @Override
        public void failed(String msg) {
            final String msgg = msg;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = new Dialog("", skin, "dialog");
                    if(msgg!=null && msgg.length()>21){
                    	dialog.text(I18n.t("ConnectServerError") + msgg.substring(0, 20) + "...");
                    } else {
                    	dialog.text(I18n.t("ConnectServerError") + msgg);
                    }
                    TextButton button = new TextButton(I18n.t("ok"), skin);
                    dialog.button(button);
                    dialog.show(stage);
                }
            });
        }
        
        @Override
        public void cancelled() {
        }
    };
    
}
