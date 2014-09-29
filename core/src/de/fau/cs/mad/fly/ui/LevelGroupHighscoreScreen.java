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
    
    private GlobalHighscoreScreen globalHighscoreScreen;
    
    public LevelGroupHighscoreScreen(BasicScreen screenToReturn) {
        super(screenToReturn);
    }
    
    public void setLevelGroup(LevelGroup levelGroup) {
        this.levelGroup = levelGroup;
    }
    
    /**
     * Switches to {@link GlobalHighscoreScreen} which show the defined
     * {@link LevelGroup}.
     */
    private void setGlobalHighscoreScreen(LevelGroup levelGroup) {
        if (globalHighscoreScreen == null) {
            globalHighscoreScreen = new GlobalHighscoreScreen(this);
        }
        globalHighscoreScreen.setLevelGroup(levelGroup);
        globalHighscoreScreen.set();
    }
    
    /**
     * generate Content Display text "loading" at first, then start a new thread
     * to loading scores data from Database and display
     */
    @Override
    protected void generateContent() {
        generateBackButton();
        scoreTable = new Table();
    }
    
    /**
     * start a new thread to loading scores data from Database and display
     */
    @Override
    public void show() {
        super.show();
        scoreTable.clear();
        contentTable.clear();
        genarateScoreTable();
    }
    
    @Override
    public void dispose() {
        if (globalHighscoreScreen != null) {
            globalHighscoreScreen.dispose();
            globalHighscoreScreen = null;
        }
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
        boolean newRow = false;
        
        @Override
        public void run() {
            final Skin skin = SkinManager.getInstance().getSkin();

            // global high score button
            globalHighScoreButton = new TextButton(I18n.t("GlobalHighscores"), skin);
            globalHighScoreButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setGlobalHighscoreScreen(levelGroup);
                }
            });
            
            Table highscoreButtonTable = new Table();
            highscoreButtonTable.setFillParent(true);
            stage.addActor(highscoreButtonTable);
            highscoreButtonTable.add(globalHighScoreButton).width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Window.BORDER_SPACE).bottom().expand();
            
            scoreTable.add(new Label(I18n.t("StatusLoading"), skin));
            final ScrollPane statisticsPane = new ScrollPane(scoreTable, skin, "semiTransparentBackground");
            statisticsPane.setFadeScrollBars(false);
            statisticsPane.setScrollingDisabled(true, false);
            
            contentTable.add(new Label(levelGroup.name, skin)).pad(0, 0, UI.Buttons.SPACE, 0);
            contentTable.row();
            contentTable.add(statisticsPane).expand();
            
            scores = ScoreManager.getInstance().getPlayerBestScores(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), levelGroup);
            // it is faster if we remove this postRunnable, but it may also
            // bring in UI render exception
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    scoreTable.clear();
                    
                    // add scores details
                    boolean haveScore = false;
                    
                    List<String> sortedKeys = new ArrayList<String>(scores.keySet());
                    Collections.sort(sortedKeys);
                    Collections.reverse(sortedKeys);
                    
                    for (String levelID : sortedKeys) {
                        Score score = scores.get(levelID);
                        if (score != null && score.getTotalScore() > 0) {
                            haveScore = true;
                            String levelname = levelGroup.getLevelName(Integer.valueOf(levelID));
                            scoreTable.add(new Label(levelname + ":", skin)).pad(UI.Buttons.SPACE, 100, UI.Buttons.SPACE, UI.Buttons.SPACE).right();
                            
                            scoreTable.add(new Label(score.getTotalScore() + "", skin)).pad(UI.Buttons.SPACE, 20, UI.Buttons.SPACE, UI.Buttons.SPACE).right();
                            
                            Gdx.app.log("timing", " UI one score record UI builded " + (end - begin) + " " + levelID + levelname);
                            uploadScoreButton = new TextButton(I18n.t("uploadScoreButtonText"), skin);
                            if (score.getIsUploaded()) {
                                uploadScoreButton.setDisabled(true);
                            }
                            uploadScoreButton.addListener(new UploadScoreClickListener(levelGroup.id, Integer.valueOf(levelID), score, uploadScoreButton));
                            
                            scoreTable.add(uploadScoreButton).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(0, UI.Buttons.SPACE, 40, 120);
                        }
                        if (newRow) {
                            scoreTable.row();
                            newRow = false;
                        } else {
                            newRow = true;
                        }
                    }
                    
                    // if no score at all
                    if (!haveScore) {
                        scoreTable.add(new Label(I18n.t("noScore"), skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE);
                        scoreTable.row();
                    }
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
            
            Gdx.app.log("Hallo", "Is Uploaded. levelID: " + requestData.LevelID + ", group: " + requestData.LevelgroupID + ", id: " + requestData.Score.getServerScoreId());
            requestData.Score.setIsUploaded(true);
            ScoreManager.getInstance().updateIsUploaded(requestData.Score, PlayerProfileManager.getInstance().getCurrentPlayerProfile().getId(), requestData.LevelgroupID, requestData.LevelID);
            if (requestData.Score.getServerScoreId() <= 0) {
                PostHighscoreService.ResponseData response = (PostHighscoreService.ResponseData) obj;
                if (response != null) {
                    requestData.Score.setServerScoreId(response.scoreID);
                }
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
