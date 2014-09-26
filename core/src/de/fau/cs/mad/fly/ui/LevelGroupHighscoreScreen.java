package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.PostHighscoreService;
import de.fau.cs.mad.fly.HttpClient.PostUserService;
import de.fau.cs.mad.fly.HttpClient.PutHighscoreService;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreManager;

/**
 * Display the highscores of all levels in one level group
 * 
 * @author Qufang Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class LevelGroupHighscoreScreen extends BasicScreenWithBackButton {
    
    private Table scoreTable;
    
    private TextButton uploadScoreButton;
    private TextButton globalHighScoreButton;
    
    private LevelGroup levelGroup;
    
    
    public LevelGroupHighscoreScreen(LevelGroup group, BasicScreen screenToReturn) {
        super(screenToReturn);
        this.levelGroup = group;
    }
    
    /**
     * init buttons, which don't need to be created dynamically
     */
    private void initButtons() {
        Skin skin = SkinManager.getInstance().getSkin();
        globalHighScoreButton = new TextButton(I18n.t("GlobalHighscores"), skin);
        globalHighScoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setGlobalHighScoreScreen(levelGroup);
            }
        });
    }
    
    /**
     * generate Content Display text "loading" at first, then start a new thread
     * to loading scores data from Database and display
     */
    @Override
    protected void generateContent() {
        
        generateBackButton();
        long begin = System.currentTimeMillis();
        initButtons();
        
        
        scoreTable = new Table();
        Skin skin = SkinManager.getInstance().getSkin();
        
        final ScrollPane statisticsPane = new ScrollPane(scoreTable, skin);
        statisticsPane.setFadeScrollBars(false);
        statisticsPane.setScrollingDisabled(true, false);
        scoreTable.add(new Label(I18n.t("StatusLoading"), skin));
        Gdx.app.log("timing", "LevelsStatisScreen generateContent " + (System.currentTimeMillis() - begin));
        contentTable.add(statisticsPane);
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
                    Skin skin = SkinManager.getInstance().getSkin();
                    scoreTable.add(new Label(levelGroup.name, skin)).colspan(3);
                    scoreTable.row().expand();
                    
                    // add scores details
                    boolean haveScore = false;
                    
                    List<String> sortedKeys=new ArrayList<String>(scores.keySet());
                    Collections.sort(sortedKeys);
                    Collections.reverse(sortedKeys);
                    
                    for (String levelID : sortedKeys) {
                        Score score = scores.get(levelID);
                        if (score != null && score.getTotalScore() > 0) {
                            haveScore = true;
                            String levelname = levelGroup.getLevelName(Integer.valueOf(levelID));
                            scoreTable.add(new Label(levelname, skin));
                            
                            scoreTable.add(new Label(score.getTotalScore() + "", skin));
                            // for (ScoreDetail detail :
                            // score.getScoreDetails()) {
                            // scoreTable.row().expand();
                            // scoreTable.add(new
                            // Label(I18n.t(detail.getDetailName()),
                            // skin)).pad(6f).right();
                            // scoreTable.add(new Label(detail.getValue(),
                            // skin)).pad(6f);
                            // }
                            
                            Gdx.app.log("timing", " UI one score record UI builded " + (end - begin) + " " + levelID + levelname);
                            uploadScoreButton = new TextButton(I18n.t("uploadScoreButtonText"), skin);
                            if (score.getIsUploaded()) {
                                uploadScoreButton.setDisabled(true);
                            }
                            uploadScoreButton.addListener(new UploadScoreClickListener(levelGroup.id, Integer.valueOf(levelID), score, uploadScoreButton));
                            // scoreTable.row().expand();
                            scoreTable.add(uploadScoreButton).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                            scoreTable.row().expand();
                        }
                        scoreTable.add(new Label("", skin)).pad(6f).uniform();
                        scoreTable.row().expand();
                    }
                    
                    // if no score at all
                    if (!haveScore) {
                        scoreTable.row();
                        scoreTable.add(new Label(I18n.t("noScore"), skin)).pad(6f).uniform();
                        scoreTable.row().expand();
                    }
                    
                    // global high score button
                    scoreTable.row();
                    scoreTable.add(globalHighScoreButton).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).colspan(3);
                    
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
        private Score score;
        private TextButton button;
        
        public UploadScoreClickListener(int levelgroup, int level, Score score, TextButton button) {
            super();
            levelgroupId = levelgroup;
            levelId = level;
            this.score = score;
            this.button = button;
        }
        
        @Override
        public void changed(ChangeEvent event, Actor actor) {
        	button.setDisabled(true);
        	
            final PostHighscoreService.RequestData requestData = new PostHighscoreService.RequestData();
            requestData.FlyID = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID();
            requestData.LevelID = levelId;
            requestData.Score = score;
            requestData.LevelgroupID = levelgroupId;
            final FlyHttpResponseListener postScoreListener = new PostScoreHttpRespListener(requestData, button);
            final PostHighscoreService postHighscoreService = new PostHighscoreService(postScoreListener, requestData);
            final PutHighscoreService putHighscoreService = new PutHighscoreService(postScoreListener, requestData);
            // if the current user have to fly-id (user id got from server side)
            // in database,
            // then call another service PostUserService to get fly-id
            if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() <= 0) {
                FlyHttpResponseListener listener = new PostUserHttpRespListener(requestData, postHighscoreService);
                PostUserService postUser = new PostUserService(listener);
                
                postUser.execute(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName());
            } else if (score.getServerScoreId() > 0) {
                putHighscoreService.execute();
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
                    Skin skin = SkinManager.getInstance().getSkin();
                    Dialog dialog = new Dialog("", skin, "dialog");
                    if (msgg != null && msgg.length() > 21) {
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
        private PostHighscoreService.RequestData requestData;
        
        public PostScoreHttpRespListener(PostHighscoreService.RequestData requestData, TextButton button) {
            this.requestData = requestData;
            this.button = button;
        }
        
        @Override
        public void successful(Object obj) {
        	button.setDisabled(false);
        	
            if (requestData.Score.getServerScoreId() > 0) {
                requestData.Score.setIsUploaded(true);
                ScoreManager.getInstance().updateIsUploaded(requestData.Score, PlayerProfileManager.getInstance().getCurrentPlayerProfile().getId(), requestData.LevelgroupID, requestData.LevelID);
            } else {
                requestData.Score.setIsUploaded(true);
                ScoreManager.getInstance().updateIsUploaded(requestData.Score, PlayerProfileManager.getInstance().getCurrentPlayerProfile().getId(), requestData.LevelgroupID, requestData.LevelID);
                PostHighscoreService.ResponseData response = (PostHighscoreService.ResponseData) obj;
                if (response != null) {
                    requestData.Score.setServerScoreId(response.scoreID);
                }
                requestData.Score.setIsUploaded(true);
                ScoreManager.getInstance().updateServerScoreId(requestData.Score, PlayerProfileManager.getInstance().getCurrentPlayerProfile().getId(), requestData.LevelgroupID, requestData.LevelID);
            }
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Skin skin = SkinManager.getInstance().getSkin();
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
        	button.setDisabled(false);
        	
            final String msgg = msg;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Skin skin = SkinManager.getInstance().getSkin();
                    Dialog dialog = new Dialog("", skin, "dialog");
                    if (msgg != null && msgg.length() > 21) {
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
