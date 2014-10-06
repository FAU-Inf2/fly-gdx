package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.profile.PlaneUpgradeManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.PlaneUpgrade;

/**
 * Overlay that contains all Buttons and Labels shown in the PlaneUpgradeScreen
 * that are used to upgrade the Planes
 * 
 * @author Sebastian
 * 
 */
public class PlaneUpgradesOverlay {
    private final Skin skin;
    private final Stage stage;
    private final PlaneUpgradeScreen screen;
    
    private float buttonWidth = 0;
    
    private Table scrollableTable;
    private ScrollPane scrollPane;
    
    final TextButton buyButton, upgradeButton, downgradeButton, backButton, cancelButton;
    final LabelStyle labelStyle;
    final Label upgradeNameLabel, upgradeCostLabel, currentMoneyLabel, upgradeStateLabel, changeLabel;
    
    final List<TextButton> upgradeButtons;
    
    private PlaneUpgrade currentUpgrade = null;
    
    final String[] names = { "speed", "pitch", "turnSpeed", "lives" };
    
    public PlaneUpgradesOverlay(final Skin skin, final Stage stage, PlaneUpgradeScreen screen) {
        this.skin = skin;
        this.stage = stage;
        this.screen = screen;
        
        buyButton = new TextButton(I18n.t("buy"), skin);
        upgradeButton = new TextButton(I18n.t("equip"), skin);
        downgradeButton = new TextButton(I18n.t("unequip"), skin);
        backButton = new TextButton(I18n.t("back"), skin);
        cancelButton = new TextButton(I18n.t("cancel"), skin);
        
        upgradeButtons = new ArrayList<TextButton>();
        
        labelStyle = skin.get("red", LabelStyle.class);
        upgradeNameLabel = new Label("", labelStyle);
        changeLabel = new Label("", labelStyle);
        upgradeCostLabel = new Label("", labelStyle);
        currentMoneyLabel = new Label("", labelStyle);
        upgradeStateLabel = new Label("", labelStyle);
    }
    
    /**
     * Sets the current Upgrade to null
     */
    public void resetCurrentUpgrade() {
        currentUpgrade = null;
    }
    
    /**
     * Getter for the current Upgrade
     * 
     * @return the current Upgrade
     */
    public PlaneUpgrade getCurrentUpgrade() {
        return currentUpgrade;
    }
    
    /**
     * Initiates the ScrollTable, ScrollPane and Buttons for the Overlay
     */
    public void init() {
        scrollableTable = new Table(skin);
        scrollableTable.top().left();
        scrollPane = new ScrollPane(scrollableTable, skin);
        scrollPane.setFillParent(true);
        scrollPane.setFadeScrollBars(false);
        
        createButtons(PlaneManager.getInstance().getChosenPlane());
        
        stage.addActor(scrollPane);
    }
    
    /**
     * Creates the Buttons for the Upgrades
     * 
     * @param plane
     *            the current Plane the Player has chosen and should be upgraded
     */
    public void createButtons(IPlane.Head plane) {
        final Collection<PlaneUpgrade> upgrades = PlaneUpgradeManager.getInstance().getUpgradeList().values();
        
        scrollableTable.add().space(300);
        scrollableTable.row();
        
        // Creates one Button for each Upgrade
        for (final PlaneUpgrade upgrade : upgrades) {
            final String name = I18n.t(upgrade.name);
            final TextButton button = new TextButton(name, skin);
            button.pad(10);
            
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    scrollableTable.clear();
                    currentUpgrade = upgrade;
                    stage.clear();
                    stage.addActor(scrollPane);
                    addButtons();
                }
            });
            
            scrollableTable.add(button).space(20).left();
            scrollableTable.row();
            
            upgradeButtons.add(button);
        }
        
        // Creates a Button to get back to the PlaneChooserScreen
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Fly) Gdx.app.getApplicationListener()).setPlaneChoosingScreen();
            }
        });
        
        scrollableTable.add(cancelButton.pad(1)).space(200).left();
        scrollableTable.row();
        
        setButtonSize();
        
        // Creating the button to buy the Upgrades
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean canBeBought = PlaneManager.getInstance().upgradeCanBeBought(currentUpgrade);
                
                if (canBeBought) {
                    // Gdx.app.log("upgrades", currentUpgrade.name +
                    // PlaneManager.getInstance().getChosenPlane().upgradesBought.get(currentUpgrade.name));
                    
                    PlaneManager.getInstance().buyUpgradeForPlane(currentUpgrade.name);
                    updateLabels();
                    
                    upgradeButton.setText(I18n.t("equip"));
                    upgradeButton.setColor(Color.WHITE);
                    
                    if (!PlaneManager.getInstance().upgradeCanBeBought(currentUpgrade)) {
                        int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
                        if (money < currentUpgrade.price) {
                            buyButton.setText(I18n.t("tooExpensive"));
                        } else {
                            buyButton.setText(I18n.t("allreadyMaximal"));
                        }
                        buyButton.setColor(Color.GRAY);
                    }
                }
            }
        });
        
        // Creating the button to equip the Upgrades
        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = currentUpgrade.name;
                int equiped = PlaneManager.getInstance().getChosenPlane().getUpgradesEquiped().get(name);
                int bought = PlaneManager.getInstance().getChosenPlane().getUpgradesBought().get(name);
                if (equiped != bought) {
                    if (equiped == 0) {
                        downgradeButton.setText(I18n.t("unequip"));
                        downgradeButton.setColor(Color.WHITE);
                    }
                    
                    PlaneManager.getInstance().upgradePlane(name, 1);
                    updateLabels();
                    screen.update();
                    
                    if (equiped + 1 == bought) {
                        upgradeButton.setText(I18n.t("allreadyMaximal"));
                        upgradeButton.setColor(Color.GRAY);
                    }
                }
            }
        });
        
        // Creating the button to unequip the Upgrades
        downgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = currentUpgrade.name;
                int equiped = PlaneManager.getInstance().getChosenPlane().getUpgradesEquiped().get(name);
                
                if (equiped != 0) {
                    if (equiped == currentUpgrade.timesAvailable) {
                        upgradeButton.setText(I18n.t("equip"));
                        upgradeButton.setColor(Color.WHITE);
                    }
                    
                    PlaneManager.getInstance().upgradePlane(name, -1);
                    updateLabels();
                    screen.update();
                    
                    if (equiped - 1 == 0) {
                        downgradeButton.setText(I18n.t("allreadyMinimal"));
                        downgradeButton.setColor(Color.GRAY);
                    }
                }
            }
        });
        
        // Creating a Buttons to get back to the Upgrade Selection
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.addOverlay();
                screen.update();
                addUpgradeButtons();
                setButtonSize();
            }
        });
        
    }
    
    public void show() {
        addUpgradeButtons();
        setButtonSize();
    }
    
    public void update() {
        
    }
    
    private void updateLabels() {
        int equiped = PlaneManager.getInstance().getChosenPlane().getUpgradesEquiped().get(currentUpgrade.name);
        int bought = PlaneManager.getInstance().getChosenPlane().getUpgradesBought().get(currentUpgrade.name);
        int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
        
    	upgradeNameLabel.setText(I18n.t("upgrade") + ": " + I18n.t(currentUpgrade.name));
        changeLabel.setText(I18n.t("changes") + ": " + getChanges());
        upgradeCostLabel.setText(I18n.t("cost") + ": " + currentUpgrade.price + "\n" + I18n.t("currentMoney") + ": " + money);
        upgradeStateLabel.setText(I18n.t("maximum") + ": " + currentUpgrade.timesAvailable + "\n" + I18n.t("bought") + ": " + bought + "\n" + I18n.t("equiped") + ": " + equiped);
    }
    
    /**
     * Adds all Buttons and Labels needed after selecting an Upgrade to
     * buy/equip
     */
    private void addButtons() {
        int equiped = PlaneManager.getInstance().getChosenPlane().getUpgradesEquiped().get(currentUpgrade.name);
        int bought = PlaneManager.getInstance().getChosenPlane().getUpgradesBought().get(currentUpgrade.name);
        int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
        
        // setting up the Lables and Buttons according to current Upgrade
        upgradeNameLabel.setText(I18n.t("upgrade") + ": " + I18n.t(currentUpgrade.name));
        changeLabel.setText(I18n.t("changes") + ": " + getChanges());
        upgradeCostLabel.setText(I18n.t("cost") + ": " + currentUpgrade.price + "\n" + I18n.t("currentMoney") + ": " + money);
        upgradeStateLabel.setText(I18n.t("maximum") + ": " + currentUpgrade.timesAvailable + "\n" + I18n.t("bought") + ": " + bought + "\n" + I18n.t("equiped") + ": " + equiped);
        
        // checking if the Upgrade can be bought, equipped, unequipped
        if (!PlaneManager.getInstance().upgradeCanBeBought(currentUpgrade)) {
            if (money < currentUpgrade.price) {
                buyButton.setText(I18n.t("tooExpensive"));
            } else {
                buyButton.setText(I18n.t("maximum"));
            }
            buyButton.setColor(Color.GRAY);
        } else {
            buyButton.setText(I18n.t("buy"));
        	buyButton.setColor(Color.WHITE);
        }
        
        if (equiped == bought) {
            upgradeButton.setText(I18n.t("allreadyMaximal"));
            upgradeButton.setColor(Color.GRAY);
        } else {
        	upgradeButton.setText(I18n.t("equip"));
        	upgradeButton.setColor(Color.WHITE);
        }
        
        if (equiped == 0) {
            downgradeButton.setText(I18n.t("allreadyMinimal"));
            downgradeButton.setColor(Color.GRAY);
        } else {
        	downgradeButton.setText(I18n.t("unequip"));
        	downgradeButton.setColor(Color.WHITE);
        }
        
        scrollableTable.add(upgradeNameLabel).left();
        scrollableTable.row();
        scrollableTable.add(changeLabel).left();
        scrollableTable.row();
        scrollableTable.add(buyButton).space(20).left();
        scrollableTable.add(upgradeCostLabel).left();
        scrollableTable.row();
        scrollableTable.row();
        scrollableTable.add(upgradeStateLabel).left();
        scrollableTable.row();
        scrollableTable.add(downgradeButton).space(20).left();
        scrollableTable.add(upgradeButton).space(20).left();
        scrollableTable.row();
        scrollableTable.add(backButton).space(200).left();
        scrollableTable.row();
    }
    
    /**
     * Adds all the Buttons to the Overlay with which you can choose an Upgrade
     * to buy/equip
     */
    private void addUpgradeButtons() {
        scrollableTable.clear();
        
        scrollableTable.add().space(300);
        scrollableTable.row();
        
        for (TextButton button : upgradeButtons) {
            scrollableTable.add(button).space(20).left();
            scrollableTable.row();
        }
        scrollableTable.add(cancelButton).space(200).left();
        scrollableTable.row();
    }
    
    /**
     * Sets the size of all Buttons to the size of the biggest one
     */
    private void setButtonSize() {
        float width = 0;
        float height = 0;
        
        float tempWidth = 0;
        float tempHeight = 0;
        for (TextButton button : upgradeButtons) {
            tempWidth = button.getWidth();
            tempHeight = button.getHeight();
            
            if (tempWidth > width) {
                width = tempWidth;
            }
            
            if (tempHeight > height) {
                height = tempHeight;
            }
        }
        
        // Adjusts the size of each Cell of the Table to make the Buttons
        // uniform
        Array<Cell> cells = scrollableTable.getCells();
        for (Cell cell : cells) {
            cell.width(width);
        }
        
        buttonWidth = width;
    }
    
    /**
     * Returns the size of the biggest Button
     * 
     * @return The size of the biggest Button
     */
    public float getButtonWidth() {
        return buttonWidth;
    }
    
    /**
     * Returns a String that describes the changes by the current Upgrade
     * 
     * @return The String that describes the changes by the current Upgrade
     */
    private String getChanges() {
        String changes = "";
        int[] values = currentUpgrade.upgradeValues;
        
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                changes += "\n    " + I18n.t(names[i]) + " " + I18n.t("by") + " " + values[i];
            }
        }
        
        return changes;
    }
}
