package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Fly.Mode3d2dChangedEvent;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameController.GameState;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Message that is shown to the player when he/she finished the game.
 * 
 * @author Tobias Zangl
 */
public class GameFinishedOverlay implements IFeatureInit, IFeatureFinish {
    private GameController gameController;
    private final Stage stage;
    private Score newScore;
    private Table messageTable;
    private TextButton backToMainMenuButton;
    
    public GameFinishedOverlay(final Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public void init(final GameController gameController) {
        this.gameController = gameController;
    }
    
    /**
     * When the game is finished, 5 states are possible:
     * <p>
     * 1) finished in time, not dead:
     * <p>
     * 1.1 it is not the last level of the level group, then message, score and
     * next level and back button are shown
     * <p>
     * 1.2 it is the last level of the level group, then message, score and next
     * group and back button are shown
     * <p>
     * 1.3 it is the last level of the last level group, then message, score and
     * back button are shown 2) spaceship broken:
     * <p>
     * 2.1 it is a normal level, then only message, restart and back button are
     * shown
     * <p>
     * 2.2 it is a endless level, then show score, restart and back button
     * <p>
     */
    @Override
    public void finish() {
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        Skin skin = SkinManager.getInstance().getSkin();
        
        backToMainMenuButton = new TextButton(I18n.t("back.to.menu"), skin);
        backToMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).getMainMenuScreen().set();
            }
        });
        
        messageTable = new Table();
        NinePatchDrawable background = new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class));
        messageTable.setBackground(background);
        
        if (gameController.getLevel().getGateCircuit().isReachedLastGate()) {
            levelSuccessfullyFinished(skin);
        } else if (gameController.getGameState() == GameState.TIME_OVER) {
            timeOver(skin);
        } else if (gameController.getPlayer().isDead()) {
            playerDead(skin);
        }
        
        messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
        outerTable.add(messageTable).center();
        stage.addActor(outerTable);
        Fly game = (Fly) Gdx.app.getApplicationListener();
        game.onMode3d2dChanged(Mode3d2dChangedEvent.MODE_2D);
    }
    
    private void timeOver(Skin skin) {
        
        backToMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Fly game = (Fly) Gdx.app.getApplicationListener();
                game.getGameController().endGame();
                ((Fly) Gdx.app.getApplicationListener()).getMainMenuScreen().set();
            }
        });
        
        TextButton restartButton = new TextButton(I18n.t("restart"), skin);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelProfile levelHead = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile();
                Loader.getInstance().loadLevel(levelHead);
            }
        });
        
        showInfoLabel(skin, "level.time.up");
        messageTable.add(restartButton).pad(UI.Buttons.SPACE_WIDTH);
    }
    
    /**
     * Displays an info label which spans the whole table.
     * 
     * @param info
     */
    private void showInfoLabel(Skin skin, String info) {
        Label infoLabel = new Label(I18n.t(info), skin);
        messageTable.add(infoLabel).colspan(3);
        messageTable.row();
    }
    
    /**
     * When the player is dead, a corresponding message should be shown and a
     * button which leads back to the {@link MainMenuScreen}. Furthermore a
     * Button to restart the level is shown.
     * <p>
     * In case of an EndlessLevel, the score is shown, too.
     */
    private void playerDead(Skin skin) {
        showInfoLabel(skin, "ship.destroyed");
        
        if (gameController.getLevel().head.isEndless() || gameController.getLevel().head.isEndlessRails()) {
            showScore(skin);
        }
        
        TextButton restartButton = new TextButton(I18n.t("restart"), skin);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // reload the level
                LevelProfile levelHead = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile();
                Loader.getInstance().loadLevel(levelHead);
            }
        });
        
        messageTable.add(restartButton).pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
        messageTable.add();
    }
    
    /**
     * When the level is finished successfully, the score is shown and a button
     * to go back to the {@link MainMenuScreen}. If there exists a next Level, a
     * button which leads to this level is shown.
     */
    private void levelSuccessfullyFinished(Skin skin) {
        final PlayerProfile currentPlayer = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        if (currentPlayer.IsLastLevel()) {
            if (currentPlayer.IsLastLevelGroup()) {
                // it is last level of last group
                currentPlayer.setPassedLevelID(currentPlayer.getCurrentLevelProfile().id + 1);
                currentPlayer.savePassedLevelID();
                showInfoLabel(skin, "ALLGroupPassed");
                showScore(skin);
                // add some space to avoid crappy layout
                messageTable.add().pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                messageTable.add();
            } else {
                if (currentPlayer.getPassedLevelgroupID() == currentPlayer.getCurrentLevelGroup().id) {
                    // it is last level, but not last group, the first time pass
                    // this level
                    currentPlayer.setPassedLevelgroupID(currentPlayer.getnextLevelGroup().id);
                    currentPlayer.savePassedLevelgroupID();
                    currentPlayer.setPassedLevelID(currentPlayer.getnextLevelGroup().getFirstLevel().id);
                    currentPlayer.savePassedLevelID();
                    showInfoLabel(skin, "OneGroupPassed");
                } else {
                    // it is last level, but not last group, not the first time
                    // pass this level
                    showInfoLabel(skin, "level.congratulations");
                }
                showLevelMessage(skin);
                showScore(skin);
                
                TextButton nextGroupButton = new TextButton(I18n.t("nextLevelGroup"), skin);
                
                currentPlayer.setToNextLevelGroup();
                currentPlayer.saveCurrentLevelGroup();
                currentPlayer.saveCurrentLevelProfile();
                nextGroupButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // set and load new level
                        Loader.getInstance().loadLevel(currentPlayer.getCurrentLevelProfile());
                    }
                });
                
                messageTable.add(nextGroupButton).pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                messageTable.add();
            }
            
        } else {
            if (currentPlayer.getPassedLevelgroupID() == currentPlayer.getCurrentLevelGroup().id && currentPlayer.getPassedLevelID() <= currentPlayer.getCurrentLevelProfile().id) {
                currentPlayer.setPassedLevelID(currentPlayer.getNextLevel().id);
                currentPlayer.savePassedLevelID();
            }
            
            showInfoLabel(skin, "level.congratulations");
            showLevelMessage(skin);
            showScore(skin);
            TextButton nextLevelButton = new TextButton(I18n.t("nextLevel"), skin);
            
            currentPlayer.setToNextLevel();
            currentPlayer.saveCurrentLevelProfile();
            nextLevelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // load new level
                    Loader.getInstance().loadLevel(currentPlayer.getCurrentLevelProfile());
                }
            });
            messageTable.add(nextLevelButton).pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
            messageTable.add();
        }
    }
    
    /**
     * Shows a message with the level name of the finished level.
     * 
     * @param skin
     *            The skin used to display the message.
     */
    private void showLevelMessage(Skin skin) {
        String levelMessage;
        if (PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile().isTutorial()) {
            levelMessage = I18n.t("tutorial.finished.first") + " '" + PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile().name + "' " + I18n.t("level.finished.last");
        } else {
            levelMessage = I18n.t("level.finished.first") + " '" + PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile().name + "' " + I18n.t("level.finished.last");
        }
        Label infoLabel = new Label(levelMessage, skin);
        messageTable.add(infoLabel).colspan(3);
        messageTable.row();
    }
    
    /**
     * Shows the score for the current level. When a new record is achieved,
     * this is shown, too.
     */
    private void showScore(Skin skin) {
        
        newScore = gameController.getScoreController().generateEndScore(gameController);
        
        // adds an amount of money to the players profile that equals the score
        // he got in this level
        PlayerProfileManager.getInstance().getCurrentPlayerProfile().addMoney(newScore.getTotalScore());
        
        Label scoreName = new Label(I18n.t("newScore"), skin);
        messageTable.columnDefaults(1).width(50f);
        messageTable.add(scoreName).right();
        messageTable.add();
        messageTable.add(new Label(newScore.getTotalScore() + "", skin)).left();
        messageTable.row().expand();
        
        // gates
        ScoreDetail detail = newScore.getScoreDetails().get(0);
        messageTable.add(new Label(I18n.t(detail.getDetailName()), skin, "medium-font")).right();
        messageTable.add();
        StringBuilder gatesBuilder = new StringBuilder();
        gatesBuilder.append(GameController.getInstance().getScoreController().getGatesPassed());
        gatesBuilder.append(" (");
        gatesBuilder.append(detail.getValue());
        gatesBuilder.append(")");
        
        messageTable.add(new Label(gatesBuilder, skin, "medium-font")).left();
        
        // time
        messageTable.row().expand();
        detail = newScore.getScoreDetails().get(1);
        messageTable.add(new Label(I18n.t(detail.getDetailName()), skin, "medium-font")).right();
        messageTable.add();
        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(gameController.getTimeController().getIntegerTime());
        timeBuilder.append(" s (");
        timeBuilder.append(detail.getValue());
        timeBuilder.append(")");
        messageTable.add(new Label(timeBuilder, skin, "medium-font")).left();
        
        // bonus points
        messageTable.row().expand();
        detail = newScore.getScoreDetails().get(2);
        messageTable.add(new Label(I18n.t(detail.getDetailName()), skin, "medium-font")).right();
        messageTable.add();
        messageTable.add(new Label(detail.getValue(), skin, "medium-font")).left();
        
        Score tmpScore = ScoreManager.getInstance().getCurrentLevelBestScore();
        if ((tmpScore == null && newScore.getTotalScore() > 0) || newScore.getTotalScore() > tmpScore.getTotalScore()) {
            
            // solution B for totalHighScoreOfall
            int score0 = (tmpScore == null) ? 0 : tmpScore.getTotalScore();
            int addScore = newScore.getTotalScore() - score0;
            PlayerProfileManager.getInstance().getCurrentPlayerProfile().addScore(addScore);
            
            newScore.setServerScoreId(tmpScore == null ? -1 : tmpScore.getServerScoreId());
            ScoreManager.getInstance().saveBestScore(newScore);
            
            messageTable.row();
            messageTable.add(new Label(I18n.t("newRecord"), skin)).colspan(3);
        }
        messageTable.row().expand();
    }
}
