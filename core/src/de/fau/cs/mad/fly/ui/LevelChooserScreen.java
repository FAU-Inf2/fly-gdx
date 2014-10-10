package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.SettingManager;
import de.fau.cs.mad.fly.ui.help.*;

/**
 * Offers the levels of one {@link LevelGroup }to start.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class LevelChooserScreen extends BasicScreenWithBackButton implements WithHelpOverlay {
    
    private LevelGroup levelGroup;
    private Button helpButton;
    private HelpOverlay helpOverlay;
    private boolean showHelpScreen;
    private Button firstTutorialButton;
    
    public LevelChooserScreen(BasicScreen screenToReturn) {
        super(screenToReturn);
        generateBackButton();
    }
    
    /**
     * Shows a list of all available levels. This list is always created when
     * the {@link LevelChooserScreen} is shown because either the level group or
     * the progress may have changed since the last time it has been generated.
     */
    public void generateDynamicContent() {
        Skin skin = SkinManager.getInstance().getSkin();
        
        contentTable.clear();
        // let some space for back button on the buttom border
        contentTable.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, 2 * UI.Window.BORDER_SPACE + UI.Buttons.IMAGE_BUTTON_HEIGHT, UI.Window.BORDER_SPACE);
        firstTutorialButton = null;
        
        helpButton = new ImageButton(skin.get(UI.Buttons.HELP_BUTTON_STYLE, ImageButtonStyle.class));
        contentTable.add(helpButton).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).top().left();
        contentTable.row();
        
        final PlayerProfile currentProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        
        List<LevelProfile> allLevels = levelGroup.getLevels();
        
        // create a button for each level. The amount of buttons in a row can be
        // adjusted
        int buttonsInARow;
        int buttonWidth;
        if (allLevels.size() > 2) {
            buttonsInARow = 9;
            buttonWidth = UI.Buttons.IMAGE_BUTTON_WIDTH;
        } else {
            buttonsInARow = UI.Buttons.BUTTONS_IN_A_ROW;
            buttonWidth = UI.Buttons.TEXT_BUTTON_WIDTH;
        }
        int maxRows = (int) Math.ceil((float) allLevels.size() / (float) buttonsInARow);
        Button button;
        
        currentProfile.checkPassedLevelForTutorials();
        boolean disableTutorials = currentProfile.getSettingManager().getBoolean(SettingManager.DISABLE_TUTORIALS);
        int size = allLevels.size();
        int sizeAllLevels = size;
        if(disableTutorials) {
        	int sizeWithoutTutorials = 0;
        	for(int i = 0; i < size; i++) {
        		if(!allLevels.get(i).isTutorial()) {
        			sizeWithoutTutorials++;
        		}
        	}
        	size = sizeWithoutTutorials;
        }
        
        
        int currentLevel = 0;
        for (int row = 0; row < maxRows; row++) {
            int maxColumns = Math.min(size - (row * buttonsInARow), buttonsInARow);
            // fill a row with buttons
            for (int column = 0; column < maxColumns && currentLevel < sizeAllLevels; column++) {           	
                final LevelProfile level = allLevels.get(currentLevel);
                if(level.isTutorial() && disableTutorials) {
                	currentLevel++;
                	column--;
                	continue;
                }
                
                button = new TextButton(level.name, skin);
                
                // set first tutotrial level
                if (level.isTutorial() && firstTutorialButton == null) {
                    firstTutorialButton = button;
                }
                
                if (Fly.DEBUG_MODE || level.isEndless() || level.isEndlessRails() ||level.id <= currentProfile.getPassedLevelID()) {
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            currentProfile.setCurrentLevelGroup(levelGroup);
                            currentProfile.saveCurrentLevelGroup();
                            currentProfile.setCurrentLevelProfile(level);
                            currentProfile.saveCurrentLevelProfile();
                            Loader.getInstance().loadLevel(level);
                        }
                    });
                } else {
                    button.setDisabled(true);
                }
                contentTable.add(button).width(buttonWidth).height(UI.Buttons.IMAGE_BUTTON_HEIGHT).expand();
                currentLevel++;
            }
            contentTable.row();
        }
        createHelp();
    }
    
    /**
     * Creates the help content for each Actor that is useful for the user.
     */
    private void createHelp() {
        Skin skin = SkinManager.getInstance().getSkin();
        helpOverlay = new HelpOverlay(this);
        helpOverlay.addHelpFrame(new HelpFrameText(skin, I18n.t("chooseLevel")));
        if (firstTutorialButton != null) {
            helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, I18n.t("tutorials"), firstTutorialButton));
            helpOverlay.addHelpFrame(new HelpFrameText(skin, I18n.t("tutorialSetting")));
        }
        helpButton.addListener(helpOverlay);
        showHelpScreen = false;
    }
    
    /**
     * Sets the current group for which the level chooser screen should display
     * the levels.
     * 
     * @param group
     *            The group to display.
     */
    public void setGroup(LevelGroup group) {
        levelGroup = group;
    }
    
    @Override
    public void render(float delta) {
        super.render(delta);
        if (showHelpScreen) {
            helpOverlay.render();
        }
    }
    
    @Override
    public void show() {
        super.show();
        generateDynamicContent();
    }
    
    @Override
    public void startHelp() {
        showHelpScreen = true;
        Gdx.input.setInputProcessor(helpOverlay);
    }

	@Override
	public void step(OverlayFrame frame) {}

	@Override
    public void endHelp() {
        showHelpScreen = false;
        Gdx.input.setInputProcessor(inputProcessor);
    }
    
}
