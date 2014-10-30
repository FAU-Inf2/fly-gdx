package de.fau.cs.mad.fly.ui.screens;

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
import de.fau.cs.mad.fly.HttpClient.PostScoreHttpRespListener;
import de.fau.cs.mad.fly.HttpClient.PostUserHttpRespListener;
import de.fau.cs.mad.fly.HttpClient.PostUserService;
import de.fau.cs.mad.fly.HttpClient.PutHighscoreService;
import de.fau.cs.mad.fly.HttpClient.PutUserHttpRespListener;
import de.fau.cs.mad.fly.HttpClient.PutUserService;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.ui.DialogWithOneButton;
import de.fau.cs.mad.fly.ui.LevelScoreEntry;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Display the highscores of all levels in one level group
 * 
 * @author Qufang Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class LevelGroupHighscoreScreen extends BasicScreenWithBackButton {
    
    private Table scoreTable;
    
    private TextButton globalHighScoreButton;
    private TextButton uploadAllScores;
    
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
        scoreTable.clear();
        contentTable.clear();
        genarateScoreTable();
        super.show();
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
            highscoreButtonTable.add().width(UI.Buttons.IMAGE_BUTTON_WIDTH).pad(UI.Window.BORDER_SPACE).bottom().left().expand();
            highscoreButtonTable.add(globalHighScoreButton).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Window.BORDER_SPACE).center().bottom().expand();
            
            uploadAllScores = new TextButton(I18n.t("uploadAll"), skin);
            highscoreButtonTable.add(uploadAllScores).height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(UI.Window.BORDER_SPACE).bottom().right().expand();
            uploadAllScores.setDisabled(true);
            
            scoreTable.add(new Label(I18n.t("StatusLoading"), skin));
            final ScrollPane statisticsPane = new ScrollPane(scoreTable, skin, "semiTransparentBackground");
            statisticsPane.setFadeScrollBars(false);
            statisticsPane.setScrollingDisabled(true, false);
            
            contentTable.add(new Label(levelGroup.name, skin)).pad(0, 0, UI.Buttons.SPACE, 0);
            contentTable.row();
            contentTable.add(statisticsPane).padBottom(UI.Buttons.TEXT_BUTTON_HEIGHT + UI.Buttons.SPACE + UI.Window.BORDER_SPACE).expand();
            
            scores = ScoreManager.getInstance().getPlayerBestScores(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), levelGroup);
            // it is faster if we remove this postRunnable, but it may also
            // bring in UI render exception
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    scoreTable.clear();
                    boolean scoresExist = false;
                    TextButton uploadScoreButton;
                    
                    List<Integer> sortedKeys = new ArrayList<Integer>(scores.keySet());
                    Collections.sort(sortedKeys);
                    Collections.reverse(sortedKeys);
                    
                    UploadScoreClickListener uploadAllListener = new UploadScoreClickListener();
                    
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
                            
                            if (score.isUploaded()) {
                                uploadScoreButton.setDisabled(true);
                            } else {
                                UploadScoreClickListener listener = new UploadScoreClickListener();
                                LevelScoreEntry levelScoreEntry = new LevelScoreEntry(levelGroup.id, Integer.valueOf(levelID), score, uploadScoreButton);
                                listener.addLevelScoreEntry(levelScoreEntry);
                                uploadAllListener.addLevelScoreEntry(levelScoreEntry);
                                uploadScoreButton.addListener(listener);
                            }
                            
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
                    
                    if (uploadAllListener.getNumberOfEntries() > 0) {
                        // enable upload all button
                        uploadAllScores.setDisabled(false);
                        uploadAllScores.addListener(uploadAllListener);
                    }
                }
            });
            
        }
    }
    
    private void updateUserNameFirst() {
        BasicScreen editPlayerNameFirstScreen = new EditPlayerNameFirstScreen(this, ((Fly) Gdx.app.getApplicationListener()).getMainMenuScreen());
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
        
        private List<LevelScoreEntry> levelScoreEntries = new ArrayList<LevelScoreEntry>();
        private int uploadedScores = 0;
        private int failedScores = 0;
        
        private void addLevelScoreEntry(LevelScoreEntry levelScoreEntry) {
            levelScoreEntries.add(levelScoreEntry);
        }
        
        private int getNumberOfEntries() {
            return levelScoreEntries.size();
        }
        
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            // check if the user name has still its default name and show an
            // input field in this case
            if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName().equals(I18n.t("default.playerName"))) {
                updateUserNameFirst();
            }
            
            for (int i = 0; i < levelScoreEntries.size(); i++) {
                
                final PostHighscoreService.RequestData requestData = new PostHighscoreService.RequestData();
                requestData.FlyID = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID();
                requestData.LevelID = levelScoreEntries.get(i).getLevelId();
                requestData.Score = levelScoreEntries.get(i).getScore();
                requestData.LevelGroupID = levelScoreEntries.get(i).getLevelGroupId();
                
                // disable button to avoid spamming of the same score
                levelScoreEntries.get(i).getButton().setDisabled(true);
                
                FlyHttpResponseListener postScoreListener = new PostScoreHttpRespListener(requestData, levelScoreEntries.get(i).getButton(), this);
                PostHighscoreService postHighscoreService = new PostHighscoreService(postScoreListener, requestData);
                
                if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getFlyID() <= 0) {
                    // if the current user has no fly-id (user id got from
                    // server side) in the database, then call another service
                    // PostUserService to get the fly-id
                    FlyHttpResponseListener listener = new PostUserHttpRespListener(requestData, postHighscoreService, postScoreListener);
                    PostUserService postUser = new PostUserService(listener);
                    postUser.execute(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getName());
                } else {
                    
                    if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().isNewnameUploaded() == false) {
                        new PutUserService(new PutUserHttpRespListener(PlayerProfileManager.getInstance().getCurrentPlayerProfile())).execute(PlayerProfileManager.getInstance().getCurrentPlayerProfile());
                    }
                    
                    if (levelScoreEntries.get(i).getScore().getServerScoreId() > 0) {
                        PutHighscoreService putHighscoreService = new PutHighscoreService(postScoreListener, requestData);
                        putHighscoreService.execute();
                    } else {
                        postHighscoreService.execute();
                    }
                }
            }
        }
        
        public void uploadSuccessfull() {
            uploadedScores++;
            if (uploadedScores >= levelScoreEntries.size()) {
                // show dialog with message for user
                Dialog uploadSuccessfullMessage = new DialogWithOneButton(I18n.t("ScoreUploaded"), I18n.t("ok"));
                uploadSuccessfullMessage.show(stage);
                uploadedScores = 0;
            } else {
                checkForPartlyFailedUpload();
            }
        }
        
        public void uploadFailed() {
            failedScores++;
            if (failedScores >= levelScoreEntries.size()) {
                // show dialog with message for user
                Dialog uploadFailedMessage = new DialogWithOneButton(I18n.t("ConnectServerError"), I18n.t("ok"));
                uploadFailedMessage.show(stage);
                failedScores = 0;
            } else {
                checkForPartlyFailedUpload();
            }
        }
        
        /**
         * It may be that only some have been uploaded. In this case a
         * corresponding message should be shown.
         */
        private void checkForPartlyFailedUpload() {
            if (failedScores + uploadedScores == levelScoreEntries.size()) {
                StringBuilder builder = new StringBuilder();
                builder.append(uploadedScores);
                builder.append("/");
                builder.append(levelScoreEntries.size());
                builder.append(" ");
                builder.append(I18n.t("partlyUploaded"));
                Dialog uploadFailedMessage = new DialogWithOneButton(builder.toString(), I18n.t("ok"));
                uploadFailedMessage.show(stage);
            }
        }
    }
    
}
