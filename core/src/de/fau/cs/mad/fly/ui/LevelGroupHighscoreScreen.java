package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import de.fau.cs.mad.fly.HttpClient.PostScoreHttpRespListener;
import de.fau.cs.mad.fly.HttpClient.PostUserHttpRespListener;
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
        // check if the user name has still its default name and show an
        // input field in this case
        if(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName().equals(I18n.t("default.playerName"))) {
            updateUserNameFirst();
        }
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
        
        private Map<Integer, Score> scores;
        private int scoresPerRow = 2;
        private int scoresUntilLineBreak = scoresPerRow;
        
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
                    boolean scoresExist = false;
                    
                    List<Integer> sortedKeys = new ArrayList<Integer>(scores.keySet());
                    Collections.sort(sortedKeys);
                    Collections.reverse(sortedKeys);
                    
                    for (Integer levelID : sortedKeys) {
                        Score score = scores.get(levelID);
                        if (!levelGroup.getLevelProfile(levelID).isTutorial() && score != null && score.getTotalScore() > 0) {
                            scoresExist = true;
                            String levelname = levelGroup.getLevelName(Integer.valueOf(levelID));
                            // for longer level-names, make only one score per
                            // row
                            if (levelname.length() > 3) {
                                scoresPerRow = 1;
                                scoresUntilLineBreak = 1;
                            }
                            scoreTable.add(new Label(levelname + ":", skin)).pad(UI.Buttons.SPACE, 100, UI.Buttons.SPACE, UI.Buttons.SPACE).right();
                            
                            scoreTable.add(new Label(score.getTotalScore() + "", skin)).pad(UI.Buttons.SPACE, 20, UI.Buttons.SPACE, UI.Buttons.SPACE).right();
                            
                            uploadScoreButton = new TextButton(I18n.t("uploadScoreButtonText"), skin);
                            if (score.getIsUploaded()) {
                                uploadScoreButton.setDisabled(true);
                            }
                            uploadScoreButton.addListener(new UploadScoreClickListener(levelGroup.id, Integer.valueOf(levelID), score, uploadScoreButton));

                            scoreTable.add(uploadScoreButton).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(0, UI.Buttons.SPACE, 40, 120);
                        }
                        scoresUntilLineBreak--;
                        if (scoresUntilLineBreak == 0) {
                            scoreTable.row();
                            scoresUntilLineBreak = scoresPerRow;
                        }
                    }
                    
                    // if no score at all
                    if (!scoresExist) {
                        scoreTable.add(new Label(I18n.t("noScore"), skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Buttons.SPACE);
                        scoreTable.row();
                    }
                }
            });
            
        }
    }
    
    private void updateUserNameFirst() {
        BasicScreen editPlayerNameFirstScreen = new EditPlayerNameFirstScreen(this, ((Fly)Gdx.app.getApplicationListener()).getMainMenuScreen());
        editPlayerNameFirstScreen.set();
    }
    
    /**
     * Upload score to server if the current user has no fly-id (user id got
     * from server side) in database, then call another service to get fly-id
     * 
     * @author Lenovo
     * 
     */
    public class UploadScoreClickListener extends ChangeListener {
        
        private int levelgroupId;
        private int levelId;
        private Score score;
        private Button button;
        
        public UploadScoreClickListener(int levelgroup, int level, Score score, TextButton button) {
            super();
            levelgroupId = levelgroup;
            levelId = level;
            this.score = score;
            this.button = button;
        }
        
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            // check if the user name has still its default name and show an
            // input field in this case
            if(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName() == I18n.t("default.playerName")) {
                updateUserNameFirst();
            }
            
            final PostHighscoreService.RequestData requestData = new PostHighscoreService.RequestData();
            requestData.FlyID = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID();
            requestData.LevelID = levelId;
            requestData.Score = score;
            requestData.LevelgroupID = levelgroupId;
            final FlyHttpResponseListener postScoreListener = new PostScoreHttpRespListener(requestData, button, stage);
            final PostHighscoreService postHighscoreService = new PostHighscoreService(postScoreListener, requestData);
            final PutHighscoreService putHighscoreService = new PutHighscoreService(postScoreListener, requestData);
            // if the current user has no fly-id (user id got from server side)
            // in the database, then call another service PostUserService to get
            // fly-id
            if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() <= 0) {
                FlyHttpResponseListener listener = new PostUserHttpRespListener(requestData, postHighscoreService, stage);
                PostUserService postUser = new PostUserService(listener);
                
                postUser.execute(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName());
            } else if (score.getServerScoreId() > 0) {
                putHighscoreService.execute();
            } else {
                postHighscoreService.execute();
            }
        }
    }
    
}
