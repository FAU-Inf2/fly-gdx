package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.ui.UI;

public class PauseGameOverlay implements IFeatureInit {
    private final Stage stage;
    private final Table table;
    
    private final TextButton pauseButton;
    
    public PauseGameOverlay(final Skin skin, final Stage stage) {
        this.stage = stage;
        
        table = new Table();
        table.setFillParent(true);
        pauseButton = new TextButton(I18n.t("pause"), skin);
        table.row().expand();
        table.add(pauseButton).right().pad(UI.Window.BORDER_SPACE);
        table.row().expand();
    }
    
    @Override
    public void init(final GameController gameController) {
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                
                if (gameController.isRunning()) {
                    gameController.pauseGame();
                    pauseButton.setText(I18n.t("run"));
                } else if (gameController.isPaused()) {
                    gameController.resumeGame();
                    pauseButton.setText(I18n.t("pause"));
                }
            }
        });
        
        stage.addActor(table);
    }
    
}
