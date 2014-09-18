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
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Message that is shown to the player when he/she finished the game.
 * 
 * @author Tobias Zangl
 */
public class GameFinishedOverlay implements IFeatureInit, IFeatureFinish {
    private GameController gameController;
    private final Skin skin;
    private final Stage stage;
    private Score newScore;
    private Table messageTable;
    private TextButton backToMainMenuButton;
    
    public GameFinishedOverlay(final Skin skin, final Stage stage) {
        this.stage = stage;
        this.skin = skin;
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
        
        backToMainMenuButton = new TextButton(I18n.t("back.to.menu"), skin);
        backToMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerProfileManager.getInstance().getCurrentPlayerProfile().setToNextLevel();
                PlayerProfileManager.getInstance().getCurrentPlayerProfile().saveCurrentLevelProfile();
                ((Fly) Gdx.app.getApplicationListener()).setMainMenuScreen();
            }
        });
        
        messageTable = new Table();
        NinePatchDrawable background = new NinePatchDrawable(skin.get("dialog-background", NinePatch.class));
        messageTable.setBackground(background);
        
        if (gameController.getLevel().getGateCircuit().isReachedLastGate()) {
            levelSuccessfullyFinished();
        } else if (gameController.getPlayer().isDead()) {
            playerDead();
        }
        
        messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        outerTable.add(messageTable).center();
        stage.addActor(outerTable);
        Fly game = (Fly) Gdx.app.getApplicationListener();
        game.onMode3d2dChanged(Mode3d2dChangedEvent.MODE_2D);
    }
    
    /**
     * Displays an info label which spans the whole table.
     * 
     * @param info
     */
    private void showInfoLabel(String info) {
        Label infoLabel = new Label(I18n.t(info), skin, "black");
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
    private void playerDead() {
        showInfoLabel("ship.destroyed");
        
        if (gameController.getLevel().head.isEndless()) {
            showScore();
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
        
        messageTable.add(restartButton).pad(UI.Buttons.SPACE_WIDTH).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
        messageTable.add();
    }
    
    /**
     * When the level is finished successfully, the score is shown and a button
     * to go back to the {@link MainMenuScreen}. If there exists a next Level, a
     * button which leads to this level is shown.
     */
    private void levelSuccessfullyFinished() {
        final PlayerProfile currentPlayer = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        if (currentPlayer.IsLastLevel()) {
            if (currentPlayer.IsLastLevelGroup()) {
                currentPlayer.setPassedLevelID(currentPlayer.getCurrentLevelProfile().id + 1);
                currentPlayer.savePassedLevelID();
                showInfoLabel("ALLGroupPassed");
                
                showScore();
                // add some space to avoid crappy layout
                messageTable.add().pad(UI.Buttons.SPACE_WIDTH).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
                messageTable.add();
            } else {
                if (currentPlayer.getPassedLevelgroupID() == currentPlayer.getCurrentLevelGroup().id) {
                    currentPlayer.setPassedLevelgroupID(currentPlayer.getnextLevelGroup().id);
                    currentPlayer.savePassedLevelgroupID();
                    currentPlayer.setPassedLevelID(currentPlayer.getnextLevelGroup().getFirstLevel().id);
                    currentPlayer.savePassedLevelID();
                    showInfoLabel("OneGroupPassed");
                } else {
                    showInfoLabel("level.congratulations");
                }
                
                showScore();
                
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
                
                messageTable.add(nextGroupButton).pad(UI.Buttons.SPACE_WIDTH).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
                messageTable.add();
            }
            
        } else {
            if (currentPlayer.getPassedLevelgroupID() == currentPlayer.getCurrentLevelGroup().id && currentPlayer.getPassedLevelID() <= currentPlayer.getCurrentLevelProfile().id) {
                currentPlayer.setPassedLevelID(currentPlayer.getNextLevel().id);
                currentPlayer.savePassedLevelID();
            }
            
            showInfoLabel("level.congratulations");
            showScore();
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
            messageTable.add(nextLevelButton).pad(UI.Buttons.SPACE_WIDTH).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT);
            messageTable.add();
        }
    }
    
    /**
     * TODO: comments
     * 
     * @param messageTable
     */
    private void showScore() {
        
        newScore = gameController.getScoreController().getEndScore(gameController);
        
        // adds an amount of money to the players profile that equals the score
        // he got in this level
        PlayerProfileManager.getInstance().getCurrentPlayerProfile().addMoney(newScore.getTotalScore());
        
        Label scoreName = new Label(I18n.t("newScore"), skin, "black");
        messageTable.columnDefaults(1).width(50f);
        messageTable.add(scoreName).right();
        messageTable.add();
        messageTable.add(new Label(newScore.getTotalScore() + "", skin, "black")).left();
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ScoreManager.getInstance().saveBestScore(newScore);
                }
            }).start();
            
            messageTable.row();
            messageTable.add(new Label(I18n.t("newRecord"), skin)).colspan(3);
        }
        messageTable.row().expand();
    }
}
