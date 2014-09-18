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
import de.fau.cs.mad.fly.game.TimeIsUpListener;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.UI;

public class TimeUpOverlay implements TimeIsUpListener {
    
    private final Skin skin;
    private Stage stage;
    
    public TimeUpOverlay(final Skin skin, Stage stage) {
        this.skin = skin;
        this.stage = stage;
    }
    
    @Override
    public boolean timeIsUp() {
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        
        TextButton backToMainMenuButton = new TextButton(I18n.t("back.to.menu"), skin);
        backToMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Fly game = (Fly) Gdx.app.getApplicationListener();
                game.getGameController().endGame();
                game.setMainMenuScreen();
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
        
        final Table messageTable = new Table();
        NinePatchDrawable background = new NinePatchDrawable(skin.get("dialog-background", NinePatch.class));
        messageTable.setBackground(background);
        
        Label infoLabel = new Label(I18n.t("level.time.up"), skin);
        messageTable.add(infoLabel);
        
        messageTable.row().expand();
        messageTable.add(restartButton).pad(UI.Buttons.SPACE_WIDTH);
        messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH);
        messageTable.row().expand();
        
        outerTable.add(messageTable).center();
        
        stage.addActor(outerTable);
        Fly game = (Fly) Gdx.app.getApplicationListener();
        game.onMode3d2dChanged(Mode3d2dChangedEvent.MODE_2D);
        
        return false;
    }
    
}
