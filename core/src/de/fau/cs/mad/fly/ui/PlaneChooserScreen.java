package de.fau.cs.mad.fly.ui;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.help.HelpFrameTextWithArrow;
import de.fau.cs.mad.fly.ui.help.HelpOverlay;
import de.fau.cs.mad.fly.ui.help.WithHelpOverlay;

/**
 * Shows all available Spaceships and gives the user the ability to choose the
 * one he wants to use
 * 
 * @author Sebastian, Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class PlaneChooserScreen extends PlaneBasicScreen implements InputProcessor, WithHelpOverlay {
    
    /** A list of all planes */
    private Map<Integer, IPlane.Head> allPlanes;
    
    // variables for rotation with touchScreen
    /** Indicates whether the touchScreen is currently touched or not */
    private boolean touched;
    /**
     * Difference between the last and the current position touched on the
     * tochScreen
     */
    private int xDif, yDif;
    /** Factors to indicate the strength of the rotation */
    private float xFactor = 0.f, yFactor = 0.f, touchDistance;
    /** Last position of the touchEvent */
    private int lastX = 0, lastY = 0;
    
    private Vector3 xAxis = new Vector3(1.f, 0.f, 0.f);
    private Vector3 yAxis = new Vector3(0.f, 1.f, 0.f);
    
    /** Vector to help with the calculation of the new cameraPosition */
    private Vector3 camVec;
    
    /** The current scaling of the shown plane */
    private float absScale = 1;
    
    private PlaneUpgradeScreen planeUpgradeScreen;
    
    /** flag to indicate weather to render the help or not */
    private boolean showHelpScreen;
    
    /** help overlay to indicate that some spaceships may not be available */
    private HelpOverlay helpOverlay;
    
    public PlaneChooserScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
        allPlanes = PlaneManager.getInstance().getSpaceshipList();
        camVec = camera.position.cpy();
        // adding the table containing the buttons with preview of every plane
        initPlaneListTable();
        initUpgradeButton();
        
        helpOverlay = new HelpOverlay(this);
        
        // initialize the InputProcessor
        inputProcessor = new InputMultiplexer(stage, this, this.backProcessor);
        generateBackButton();
    }
    
    private void initUpgradeButton() {
        // adding the button that opens the UpgradeScreen
        ImageButton openButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
        
        Table table = new Table(skin);
        table.setFillParent(true);
        table.top().right().pad(UI.Window.BORDER_SPACE);
        table.add(openButton).width(UI.Buttons.IMAGE_BUTTON_WIDTH).height(UI.Buttons.IMAGE_BUTTON_HEIGHT);
        stage.addActor(table);
        
        openButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setPlaneUpgradeScreen();
            }
        });
    }
    
    public void setPlaneUpgradeScreen() {
        if (planeUpgradeScreen == null) {
            planeUpgradeScreen = new PlaneUpgradeScreen(this);
        }
        planeUpgradeScreen.set();
    }
    
    private void resetVectors() {
        xRotationAxis.set(1.f, 0.f, 0.f);
        yRotationAxis.set(0.f, 1.f, 0.f);
        xAxis.set(1.f, 0.f, 0.f);
        yAxis.set(0.f, 1.f, 0.f);
    }
    
    @Override
    public void render(float delta) {
        super.render(delta);
        
        // Steady rotation if the Player doesn't touch the Touch screen
        if (!touched) {
            currentSpaceship.transform.rotate(yRotationAxis, 0.2f);
            xRotationAxis.rotate(yRotationAxis, -0.2f);
        }
        
        if (showHelpScreen) {
            helpOverlay.render();
        }
    }
    
    private void initPlaneListTable() {
        // adding the table containing the buttons with preview of every plane
        Table planeListTable = new Table(skin);
        planeListTable.setFillParent(true);
        
        int size = allPlanes.size();
        int passedLevelGroupId = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getPassedLevelgroupID();
        
        // add some space to avoid that back button and ship selection button
        // overlapp
        planeListTable.add().width(300).bottom().expand();
        for (int i = 1; i <= size; i++) {
            Texture texture1 = new Texture(Gdx.files.internal("spaceships/previews/" + allPlanes.get(i).modelRef + ".png"));
            TextureRegion image = new TextureRegion(texture1);
            ImageButtonStyle style = new ImageButtonStyle(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
            style.imageUp = new TextureRegionDrawable(image);
            style.imageDown = new TextureRegionDrawable(image);
            
            final ImageButton button = new ImageButton(style);
            if (!Fly.DEBUG_MODE && allPlanes.get(i).levelGroupDependency > passedLevelGroupId) {
                button.setDisabled(true);
                final int levelId = i;
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Skin skin = SkinManager.getInstance().getSkin();
                        helpOverlay.clearListOfFrames();
                        
                        StringBuilder message = new StringBuilder();
                        message.append(I18n.t("minLevelGroupForShip1"));
                        message.append(" ");
                        message.append(LevelGroupManager.getInstance().getLevelGroups().get(levelId-1).name);
                        message.append(" ");
                        message.append(I18n.t("minLevelGroupForShip2"));
                        message.append(".");
                        helpOverlay.addHelpFrame(new HelpFrameTextWithArrow(skin, message.toString(), button));
                        showHelpScreen = true;
                        helpOverlay.clicked(null, 0, 0);
                    }
                });
                
                // TODO: add listener to show message
            } else {
                final int index = i;
                
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentPlane = allPlanes.get(index);
                        PlaneManager.getInstance().setChosenPlane(currentPlane);
                        resetVectors();
                        loadCurrentPlane();
                        updateChosenPlaneDetail();
                    }
                });
            }
            
            planeListTable.add(button).bottom().pad(UI.Window.BORDER_SPACE).expand();
        }
        
        stage.addActor(planeListTable);
    }
    
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) {
            touched = true;
            lastX = screenX;
            lastY = screenY;
        } else if (pointer == 1) {
            float xDif = lastX - screenX;
            float yDif = lastY - screenY;
            touchDistance = (float) Math.sqrt(xDif * xDif + yDif * yDif);
        }
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) {
            touched = false;
            xFactor = 0;
            yFactor = 0;
        } else if (pointer == 1) {
            touchDistance = 0;
        }
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == 0) {
            xDif = lastX - screenX;
            yDif = lastY - screenY;
            
            xFactor = -xDif / screenWidth;
            yFactor = yDif / screenHeight;
            
            currentSpaceship.transform.rotate(yRotationAxis, xFactor * 360);
            currentSpaceship.transform.rotate(xRotationAxis, yFactor * 360);
            // rotate the rotationAxises so that the rotation stays correct
            yAxis = yRotationAxis;
            xAxis = xRotationAxis;
            xRotationAxis.rotate(yAxis, -xFactor * 360);
            yRotationAxis.rotate(xAxis, -yFactor * 360);
            
            lastX = screenX;
            lastY = screenY;
        }
        // second finger on touchScreen
        if (pointer == 1 && touched) {
            float xDif = lastX - screenX;
            float yDif = lastY - screenY;
            float newTouchDistance = (float) Math.sqrt(xDif * xDif + yDif * yDif);
            
            float scale = touchDistance / newTouchDistance;
            absScale += scale - 1;
            
            if (absScale < 0.5f) {
                absScale = 0.5f;
                scale = 1;
            } else if (absScale > 2.f) {
                absScale = 2.f;
                scale = 1;
            }
            
            touchDistance = newTouchDistance;
            
            camera.translate(camVec.cpy().scl(scale - 1));
            camera.update();
        }
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // nothing to do here
        return false;
    }
    
    @Override
    public boolean scrolled(int amount) {
        float scale = amount * 0.1f;
        absScale += scale;
        
        if (absScale < 0.5f) {
            absScale = 0.5f;
            scale = 0;
        } else if (absScale > 2.f) {
            absScale = 2.f;
            scale = 0;
        }
        
        camera.translate(camVec.cpy().scl(scale));
        camera.update();
        return false;
    }
    
    @Override
    public void show() {
        super.show();
        showHelpScreen = false;
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