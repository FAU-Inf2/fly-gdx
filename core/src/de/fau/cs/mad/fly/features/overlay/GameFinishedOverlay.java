package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

    private final void loadNextLevel() {
        LevelProfile levelHead = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile();
        Loader.getInstance().loadLevel(levelHead);
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
                Fly game = (Fly) Gdx.app.getApplicationListener();
                game.getGameController().endGame();
                game.getMainMenuScreen().set();
            }
        });

        stage.addCaptureListener(new InputListener() {
            public boolean keyDown( InputEvent e, int keycode ) {
                if ( keycode == Keys.SPACE || keycode == Keys.ENTER )
                    loadNextLevel();
                return false;
            }
        });
        
        messageTable = new Table();

        NinePatchDrawable background = new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class));
        messageTable.setBackground(background);
        
        String textKey = "";
        if (gameController.getLevel().getGateCircuit().isReachedLastGate()) {
            textKey = levelSuccessfullyFinished(skin);
        } else if (gameController.getGameState() == GameState.TIME_OVER) {
            textKey = timeOver(skin);
        } else if (gameController.getPlayer().isDead()) {
            textKey = playerDead(skin);
        }

        if ( textKey != null ) {
            TextButton variableButton = new TextButton(I18n.t(textKey), skin);
            variableButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    loadNextLevel();
                }
            });

            messageTable.add(variableButton).pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
            messageTable.add();
        }
        
        messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
        outerTable.add(messageTable).center();
        stage.addActor(outerTable);
        Fly game = (Fly) Gdx.app.getApplicationListener();
        game.onMode3d2dChanged(Mode3d2dChangedEvent.MODE_2D);
    }

    private String timeOver(Skin skin) {
        showInfoLabel(skin, "level.time.up");
        return "restart";
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
    private String playerDead(Skin skin) {
        showInfoLabel(skin, "ship.destroyed");

        if (gameController.getLevel().head.isEndless() || gameController.getLevel().head.isEndlessRails())
            showScore(skin);
        
        return "restart";
    }
    
    /**
     * When the level is finished successfully, the score is shown and a button
     * to go back to the {@link MainMenuScreen}. If there exists a next Level, a
     * button which leads to this level is shown.
     */
    private String levelSuccessfullyFinished(Skin skin) {
        final PlayerProfile currentPlayer = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        if (currentPlayer.isLastLevel()) {
            if (currentPlayer.isLastLevelGroup()) {
                // it is last level of last group
                currentPlayer.setPassedLevelID(currentPlayer.getCurrentLevelProfile().id + 1);
                currentPlayer.savePassedLevelID();
                showInfoLabel(skin, "ALLGroupPassed");
                showScore(skin);
                // add some space to avoid crappy layout
                messageTable.add().pad(UI.Buttons.SPACE_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                messageTable.add();
                return null;
            } else {
                if (currentPlayer.getPassedLevelgroupID() == currentPlayer.getCurrentLevelGroup().id) {
                    // it is last level, but not last group, the first time pass
                    // this level
                    currentPlayer.setPassedLevelgroupID(currentPlayer.getnextLevelGroup().id);
                    currentPlayer.savePassedLevelgroupID();
                    currentPlayer.setPassedLevelID(currentPlayer.getnextLevelGroup().getFirstLevel().id);
                    currentPlayer.savePassedLevelID();
                    showInfoLabel(skin, "OneGroupPassed");
                } 
                showLevelMessage(skin);
                showScore(skin);
                
                return "nextLevelGroup";
            }
            
        } else {
            if (currentPlayer.getPassedLevelgroupID() == currentPlayer.getCurrentLevelGroup().id && currentPlayer.getPassedLevelID() <= currentPlayer.getCurrentLevelProfile().id) {
                currentPlayer.setPassedLevelID(currentPlayer.getNextLevel().id);
                currentPlayer.savePassedLevelID();
            }
            
            showLevelMessage(skin);
            showScore(skin);

            currentPlayer.setToNextLevel();
            currentPlayer.saveCurrentLevelProfile();

            return "nextLevel";
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
    	if(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile().isTutorial()) {
    		return;
    	}
        
        newScore = gameController.getScoreController().generateEndScore(gameController);
        
        // adds an amount of money to the players profile that equals the score
        // he got in this level
        PlayerProfileManager.getInstance().getCurrentPlayerProfile().addMoney(newScore.getTotalScore());
        
        // define some space between the labels and the values
        messageTable.columnDefaults(1).width(50f);
        
        Score recentBestScore = ScoreManager.getInstance().getCurrentLevelBestScore();
        if ((recentBestScore == null && newScore.getTotalScore() > 0) || newScore.getTotalScore() > recentBestScore.getTotalScore()) {
            
            // solution B for totalHighScoreOfall
            int score0 = (recentBestScore == null) ? 0 : recentBestScore.getTotalScore();
            int addScore = newScore.getTotalScore() - score0;
            PlayerProfileManager.getInstance().getCurrentPlayerProfile().addScore(addScore);
            
            newScore.setServerScoreId(recentBestScore == null ? -1 : recentBestScore.getServerScoreId());
            ScoreManager.getInstance().saveBestScore(newScore);
            
            messageTable.row();
            messageTable.add(new Label(I18n.t("newRecord") + ":", skin)).right();
            messageTable.add();
            messageTable.add(new Label(String.valueOf(newScore.getTotalScore()), skin)).left();
        }
        else {
            Label scoreName = new Label(I18n.t("newScore"), skin);
            
            messageTable.add(scoreName).right();
            messageTable.add();
            StringBuilder builder = new StringBuilder();
            builder.append(newScore.getTotalScore());
            builder.append(", ");
            builder.append(I18n.t("record"));
            builder.append(": ");
            builder.append(recentBestScore.getTotalScore());
            messageTable.add(new Label(builder.toString(), skin)).left();
        }
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
        messageTable.row();
    }
}
