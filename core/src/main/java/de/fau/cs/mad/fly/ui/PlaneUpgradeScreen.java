package de.fau.cs.mad.fly.ui;

import java.util.Collection;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlaneUpgradeManager;
import de.fau.cs.mad.fly.res.PlaneUpgrade;

/**
 * The Screen in which the Player can upgrade his Planes
 * 
 * @author Sebastian
 * 
 */
public class PlaneUpgradeScreen extends PlaneBasicScreen {
    
    private Table upgradesListTable;
    
    private PlaneUpgradeDetailScreen planeUpgradeDetailScreen;
    private boolean shipMoved = false;
    
    public PlaneUpgradeScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
        upgradesListTable = new Table();
        
        initUpgradeButtons();
        initChosenPlaneDetail();
        generateBackButton();
    }
    
    private void initUpgradeButtons() {
        Table outTable = new Table();
        outTable.setFillParent(true);
        outTable.pad(0f);
        
        ScrollPane scrollPane = new ScrollPane(outTable, skin);
        scrollPane.setFillParent(true);
        scrollPane.setFadeScrollBars(false);
        
        final Collection<PlaneUpgrade> upgrades = PlaneUpgradeManager.getInstance().getUpgradeList().values();
        
        // Creates one Button for each Upgrade
        for (final PlaneUpgrade upgrade : upgrades) {
            final TextButton button = new TextButton(I18n.t(upgrade.name), skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    openUpgradeDetailScreen(upgrade);
                }
            });
            
            upgradesListTable.add(button).width(UI.Buttons.TEXT_BUTTON_WIDTH).pad(15f);
            upgradesListTable.row();
        }
        
        outTable.add(upgradesListTable).right().top().pad(UI.Window.BORDER_SPACE).expand();
        stage.addActor(scrollPane);
    }
    
    private void openUpgradeDetailScreen(PlaneUpgrade upgrade) {
        if (planeUpgradeDetailScreen == null) {
            planeUpgradeDetailScreen = new PlaneUpgradeDetailScreen(this, upgrade);
        }
        planeUpgradeDetailScreen.setChosenUpgrade(upgrade);
        planeUpgradeDetailScreen.set();
    }
    
    @Override
    public void show() {
        super.show();
        updateChosenPlaneDetail();
        // place spaceship a little left of the middle a little down
        if (!shipMoved) {
            currentSpaceship.transform.translate(-0.8f, -0.5f, 0f);
            shipMoved = true;
        }
    }
}
