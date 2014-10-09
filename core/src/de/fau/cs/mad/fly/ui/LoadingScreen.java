package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.Loadable;
import de.fau.cs.mad.fly.ProgressListener;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Displays the splash screen.
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LoadingScreen<T> extends BasicScreenWithBackButton {
    
    protected ScalableProgressBar progressBar;
    protected final float progressBarWidth = 2000f;
    protected final TextButton button;
    
    protected final Table table;
    
    /** The object that should be loaded during this screen is shown */
    protected final Loadable<T> loadable;
    
    public LoadingScreen(Loadable<T> loadable, BasicScreen screenToReturn) {
        super(screenToReturn);
        this.loadable = loadable;
        table = new Table();
        Skin skin = SkinManager.getInstance().getSkin();
        progressBar = new ScalableProgressBar(skin);
        progressBar.setWidth(progressBarWidth);
        
        table.pad(UI.Window.BORDER_SPACE).setFillParent(true);
        stage.addActor(table);
        
        // add button
        button = new TextButton(I18n.t("button.start"), skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Fly app = ((Fly) Gdx.app.getApplicationListener());
                app.setGameScreen();
                app.getGameController().initGame();
                if(!PlayerProfileManager.getInstance().getCurrentPlayerProfile().getSettingManager().getBoolean(SettingManager.USE_TOUCH))
                    app.getGameController().getFlightController().init();
            }
        });
        table.add(button).bottom().width(UI.Buttons.TEXT_BUTTON_WIDTH).height(UI.Buttons.TEXT_BUTTON_HEIGHT).expand();
        button.setVisible(false);
        table.row();
        
        // add progress bar
        table.add(progressBar).bottom().expand();
        loadable.addProgressListener(new ProgressListener.ProgressAdapter<T>() {
            @Override
            public void progressUpdated(float percent) {
                progressBar.setProgress(percent / 100f);
            }
        });
    }
    
    @Override
    public void render(float delta) {
        loadable.update();
        super.render(delta);
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
