package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.res.Assets;

/**
 * Manages the skin.
 * 
 * @author Tobi
 *
 */
public class SkinManager implements Disposable {
	
	// TODO: make singleton?
	//private static SkinManager instance = null;
	
	private Skin skin;
	
	/**
	 * Creates the skin manager.
	 */
	public SkinManager(String skinFile) {
		createSkin(skinFile);
	}	
	
	/**
	 * Creates the skin for the UI.
	 */
	public void createSkin(String skinFile) {
		Assets.load(Assets.font);

		FreeTypeFontGenerator fontGenerator = Assets.manager.get(Assets.font);
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
		Assets.load(Assets.textureAtlas);
        skin = new Skin(Assets.manager.get(Assets.textureAtlas));
		
		fontParameter.size = UI.Buttons.DEFAULT_FONT_SIZE;
		BitmapFont buttonFont = fontGenerator.generateFont(fontParameter);
		skin.add("default-font", buttonFont);
		
		fontParameter.size = UI.Buttons.HELP_BUTTON_FONT_SIZE;
		buttonFont = fontGenerator.generateFont(fontParameter);
		skin.add("help-button-font", buttonFont);
		
		skin.load(Gdx.files.internal(skinFile));
	}
	
	/**
	 * Getter for the skin.
	 * @return skin
	 */
	public Skin getSkin() {
		return skin;
	}

	@Override
	public void dispose() {
		if(skin != null) {
			skin.dispose();
			skin = null;
		}
	}
}
