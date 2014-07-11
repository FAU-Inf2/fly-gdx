package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.game.IntegerTimeListener;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display the used time.
 * 
 * @author Tobias Zangl
 */
public class TimeLeftOverlay implements IntegerTimeListener {

	private Label timeCounter;

	public TimeLeftOverlay(Skin skin, final Stage stage) {
		LabelStyle labelStyle = skin.get("default", LabelStyle.class);
	    
	    Table outerTable = new Table();
        outerTable.setFillParent(true);
        
        final Table innerTable = new Table();
        NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
        innerTable.setBackground(background);
        
        TextureRegion textureRegion = new TextureRegion(skin.getRegion("watch"));
        Image watchIcon = new Image(textureRegion);
        // sorry for those magic numbers, but the image is not properly placed, when added to the table
        watchIcon.setPosition(150, 180);
        
        timeCounter = new Label("", labelStyle);
        
        innerTable.add(timeCounter).pad(0, 200, 0, 0);
        innerTable.row().expand();
        
        outerTable.row().expand();
        outerTable.add(innerTable).pad(UI.Window.BORDER_SPACE).left().bottom();
        outerTable.row().expand();
        stage.addActor(outerTable);
        stage.addActor(watchIcon);
	}

	@Override
	public void integerTimeChanged(int newTime) {
		timeCounter.setText(String.valueOf(newTime));
	}
}