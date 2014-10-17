package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameController.GameState;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

public class BackButtonOverlay implements IFeatureInit {
    
    private final Stage stage;
    private final Table table;
    
    private final Button backButton;
    
    public BackButtonOverlay(final Stage stage) {
        Skin skin = SkinManager.getInstance().getSkin();
        this.stage = stage;
        table = new Table();
        table.setFillParent(true);
        backButton = new ImageButton(skin, "backArrow");
        table.add(backButton).left().bottom().expand().pad(UI.Window.BORDER_SPACE).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT);
    }
    
    @Override
    public void init(final GameController gameController) {
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameController.setGameState(GameState.PAUSED);
                ((Fly) Gdx.app.getApplicationListener()).getMainMenuScreen().set();
            }
        });
        stage.addActor(table);
    }
    
}
