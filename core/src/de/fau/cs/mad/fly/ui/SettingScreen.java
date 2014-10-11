package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.ISetting;
import de.fau.cs.mad.fly.settings.SettingManager;
import de.fau.cs.mad.fly.ui.help.HelpFrameTextWithArrow;
import de.fau.cs.mad.fly.ui.help.HelpOverlay;
import de.fau.cs.mad.fly.ui.help.OverlayFrame;
import de.fau.cs.mad.fly.ui.help.WithHelpOverlay;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays and changes the options of the game.
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class SettingScreen extends BasicScreenWithBackButton implements WithHelpOverlay {

	private Map<ISetting.Groups, Table> settingsMap;
	private Map<ISetting.Groups, Button> groupButtons;
    private String displayPlayer = "";
    private boolean showHelpScreen = false;
    private HelpOverlay helpOverlay;
    private boolean helpScreenCreated = false;
    
    /**
     * Creates a setting screen and defines to which {@link BasicScreen} to
     * return when pressing back button.
     * 
     * @param screenToReturn
     */
    public SettingScreen(BasicScreen screenToReturn) {
        super(screenToReturn);
    }
    
    @Override
    protected void generateContent() {
        generateBackButton();
		settingsMap = new HashMap<ISetting.Groups, Table>();
		groupButtons = new HashMap<ISetting.Groups, Button>();

		final Table generalContent = new Table();
		final Table audioContent = new Table();
		final Table controlContent = new Table();
		settingsMap.put(ISetting.Groups.GENERAL, generalContent);
		settingsMap.put(ISetting.Groups.AUDIO, audioContent);
		settingsMap.put(ISetting.Groups.CONTROLS, controlContent);

        Skin skin = SkinManager.getInstance().getSkin();
        generalContent.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
		audioContent.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
		controlContent.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));

		HorizontalGroup top = new HorizontalGroup();
		Table tabs = new Table();
		final Button tab1 = new TextButton(I18n.t("generalSettings"), skin, "toggle");
		final Button tab2 = new TextButton(I18n.t("audioSettings"), skin, "toggle");
		final Button tab3 = new TextButton(I18n.t("controlSettings"), skin, "toggle");
		tabs.add(tab1).pad(50);
		tabs.add(tab2).pad(50);
		tabs.add(tab3).pad(50);
		top.addActor(tabs);

		groupButtons.put(ISetting.Groups.GENERAL, tab1);
		groupButtons.put(ISetting.Groups.AUDIO, tab2);
		groupButtons.put(ISetting.Groups.CONTROLS, tab3);

		ChangeListener tabListener = new ChangeListener(){
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				generalContent.setVisible(tab1.isChecked());
				audioContent.setVisible(tab2.isChecked());
				controlContent.setVisible(tab3.isChecked());
			}
		};
		tab1.addListener(tabListener);
		tab2.addListener(tabListener);
		tab3.addListener(tabListener);

		ButtonGroup group = new ButtonGroup();
		group.setMinCheckCount(1);
		group.setMaxCheckCount(1);
		group.add(tab1);
		group.add(tab2);
		group.add(tab3);

        Table outerTable = new Table();
        outerTable.setFillParent(true);
		outerTable.add(top);
		outerTable.row();

		Stack content = new Stack();
		content.add(generalContent);
		content.add(audioContent);
		content.add(controlContent);


        outerTable.add(content).center().expand();
        stage.addActor(outerTable);
        
        // setup help overlay
        final Button helpButton = new ImageButton(skin.get(UI.Buttons.HELP_BUTTON_STYLE, ImageButtonStyle.class));
        helpButton.setBounds(UI.Window.BORDER_SPACE, viewport.getWorldHeight() - UI.Window.BORDER_SPACE - UI.Buttons.TEXT_BUTTON_HEIGHT, UI.Buttons.TEXT_BUTTON_HEIGHT, UI.Buttons.TEXT_BUTTON_HEIGHT);
        
        this.helpOverlay = new HelpOverlay(this);
        helpButton.addListener(helpOverlay);
        showHelpScreen = false;
        stage.addActor(helpButton);
    }
    
    private void generateContentDynamic() {
        PlayerProfile playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        Skin skin = SkinManager.getInstance().getSkin();
        if (displayPlayer == null || (!displayPlayer.equals(playerProfile.getName()))) {
            for ( Table settingTable : settingsMap.values() )
            	settingTable.clear();
			SettingManager settingManager = playerProfile.getSettingManager();
            for (ISetting setting : settingManager.getSettings()) {
				if ( setting.isHidden() )
					continue;
				Table settingTable = settingsMap.get(setting.group());
                settingTable.row().expand();
                if (!helpScreenCreated) {
                    helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, I18n.t(setting.getHelpingText()), setting.getActor()));
                }
				float padding = 20;
				Label label = new Label(setting.getDescription(), skin);
				settingTable.add(label).right().pad(padding);
                settingTable.add(setting.getActor()).pad(padding, 3 * padding, padding, padding).width(500f);
            }
            displayPlayer = playerProfile.getName();
        }
        helpScreenCreated = true;
    }

	public void enableGroup(ISetting.Groups group) {
		groupButtons.get(group).setChecked(true);
	}

	@Override
	public void step(OverlayFrame frame) {
		HelpFrameTextWithArrow f = (HelpFrameTextWithArrow) frame;
		SettingManager manager = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getSettingManager();
		for ( ISetting setting : manager.getSettings() )
			if ( setting.getActor() == f.getActor() ) {
				enableGroup(setting.group());
				break;
			}
	}
    
    @Override
    public void show() {
        super.show();
        generateContentDynamic();
    }
    
    @Override
    public void render(float delta) {
        super.render(delta);
        if (showHelpScreen) {
            helpOverlay.render();
        }
    }
    
    @Override
    public void startHelp() {
        showHelpScreen = true;
        Gdx.input.setInputProcessor(helpOverlay);
    }

	@Override
    public void endHelp() {
        showHelpScreen = false;
        Gdx.input.setInputProcessor(inputProcessor);
    }
}