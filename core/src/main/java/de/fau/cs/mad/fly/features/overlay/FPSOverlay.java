package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display the frames per second.
 * 
 * @author Tobias Zangl
 */
public class FPSOverlay implements IFeatureDraw {
    private Label fpsCounter;
    
    public FPSOverlay(final Stage stage) {
        LabelStyle labelStyle = SkinManager.getInstance().getSkin().get("red", LabelStyle.class);
        fpsCounter = new Label("", labelStyle);
        fpsCounter.setPosition(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE * 6);
        stage.addActor(fpsCounter);
    }
    
    @Override
    public void draw(float delta) {
        fpsCounter.setText(I18n.t("fps") + " " + String.valueOf((int) (1.0 / delta)));
    }
}
