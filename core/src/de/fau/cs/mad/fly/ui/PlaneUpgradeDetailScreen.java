package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.PlaneUpgrade;

/**
 * The Screen in which the Player can buy and equip upgrade for his Planes
 * 
 * @author Sebastian
 * 
 */
public class PlaneUpgradeDetailScreen extends PlaneBasicScreen {
    
    private PlaneUpgrade chosenUpgrade;
    
    private Table upgradeDetailRightTable;
    private Table upgradeDetailLeftTable;
    final TextButton buyButton, upgradeButton, downgradeButton;
    final LabelStyle labelStyle;
    final Label upgradeNameLabel, upgradeCostLabel, currentMoneyLabel;
    final Label upgradeMaximumLabel, upgradeBoughtLabel, upgradeEquipedLabel;
    private boolean shipMoved = false;
    
    final String[] names = { "speed", "pitch", "turnSpeed", "lives" };
    
    /**
     * @return the chosenUpgrade
     */
    public PlaneUpgrade getChosenUpgrade() {
        return chosenUpgrade;
    }
    
    /**
     * @param chosenUpgrade
     *            the chosenUpgrade to set
     */
    public void setChosenUpgrade(PlaneUpgrade chosenUpgrade) {
        this.chosenUpgrade = chosenUpgrade;
    }
    
    public PlaneUpgradeDetailScreen(BasicScreen screenToGoBack, PlaneUpgrade upgrade) {
        super(screenToGoBack);
        this.chosenUpgrade = upgrade;
        
        skin = SkinManager.getInstance().getSkin();
        
        buyButton = new TextButton(I18n.t("buy"), skin);
        upgradeButton = new TextButton(I18n.t("equip"), skin);
        downgradeButton = new TextButton(I18n.t("unequip"), skin);
        initButtonsListener();
        
        labelStyle = skin.get(LabelStyle.class);
        upgradeNameLabel = new Label("", labelStyle);
        upgradeCostLabel = new Label("", labelStyle);
        currentMoneyLabel = new Label("", labelStyle);
        upgradeMaximumLabel = new Label("", labelStyle);
        upgradeBoughtLabel = new Label("", labelStyle);
        upgradeEquipedLabel = new Label("", labelStyle);
        
        upgradeDetailRightTable = new Table();
        upgradeDetailRightTable.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
        upgradeDetailLeftTable = new Table();
        upgradeDetailLeftTable.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
        
        initUpgradeDetailTable();
        generateBackButton();
    }
    
    private void initUpgradeDetailTable() {
        Table outTable = new Table();
        outTable.setFillParent(true);
        
        updateUpgradeDetailTable();
        
        outTable.add(upgradeDetailLeftTable).left().top().pad(UI.Window.BORDER_SPACE).expand();
        outTable.add(upgradeDetailRightTable).right().bottom().pad(UI.Window.BORDER_SPACE).expand();
        stage.addActor(outTable);
    }
    
    /**
     * Creates the Buttons for the Upgrades
     * 
     * @param plane
     *            the current Plane the Player has chosen and should be upgraded
     */
    private void initButtonsListener() {
        // Creating the button to buy the Upgrades
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean canBeBought = PlaneManager.getInstance().upgradeCanBeBought(chosenUpgrade);
                
                if (canBeBought) {
                    PlaneManager.getInstance().buyUpgradeForPlane(chosenUpgrade.name);
                    updateUpdateDetails();
                    
                    upgradeButton.setText(I18n.t("equip"));
                    upgradeButton.setDisabled(false);
                    
                    if (!PlaneManager.getInstance().upgradeCanBeBought(chosenUpgrade)) {
                        int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
                        buyButton.setDisabled(money < chosenUpgrade.price);
                    }
                }
            }
        });
        
        // Creating the button to equip the Upgrades
        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = chosenUpgrade.name;
                int equiped = PlaneManager.getInstance().getChosenPlane().getUpgradesEquiped().get(name);
                int bought = PlaneManager.getInstance().getChosenPlane().getUpgradesBought().get(name);
                if (equiped != bought) {
                    if (equiped == 0) {
                        downgradeButton.setText(I18n.t("unequip"));
                        downgradeButton.setDisabled(false);
                    }
                    
                    PlaneManager.getInstance().upgradePlane(name, 1);
                    updateUpdateDetails();
                    
                    if (equiped + 1 == bought) {
                        upgradeButton.setDisabled(true);
                    }
                }
            }
        });
        
        // Creating the button to unequip the Upgrades
        downgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = chosenUpgrade.name;
                int equiped = PlaneManager.getInstance().getChosenPlane().getUpgradesEquiped().get(name);
                
                if (equiped != 0) {
                    if (equiped == chosenUpgrade.timesAvailable) {
                        upgradeButton.setText(I18n.t("equip"));
                        upgradeButton.setDisabled(false);
                    }
                    
                    PlaneManager.getInstance().upgradePlane(name, -1);
                    updateUpdateDetails();
                    
                    if (equiped - 1 == 0) {
                        downgradeButton.setDisabled(true);
                    }
                }
            }
        });
    }
    
    private void updateUpdateDetails() {
        int equiped = PlaneManager.getInstance().getChosenPlane().getEquipedUpgradeCount(chosenUpgrade.name);
        int bought = PlaneManager.getInstance().getChosenPlane().getBoughtUpgradeCount(chosenUpgrade.name);
        int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
        setOptionButtonsState(money, bought, equiped);
        currentMoneyLabel.setText(I18n.t("gainMoney") + ": " + money);
        upgradeBoughtLabel.setText(I18n.t("bought") + ": " + bought + "/" + chosenUpgrade.timesAvailable);
        upgradeEquipedLabel.setText(I18n.t("equiped") + ": " + equiped + "");
    }
    
    /**
     * checking if the Upgrade can be bought, equipped, unequipped and then set
     * the buttons status
     * 
     * @param money
     * @param bought
     * @param equiped
     */
    private void setOptionButtonsState(int money, int bought, int equiped) {
        
        if (!PlaneManager.getInstance().upgradeCanBeBought(chosenUpgrade)) {
            if (money < chosenUpgrade.price) {
                buyButton.setDisabled(true);
            } else {
                buyButton.setDisabled(true);
            }
        } else {
            buyButton.setText(I18n.t("buy"));
            buyButton.setDisabled(false);
        }
        
        if (equiped == bought) {
            upgradeButton.setDisabled(true);
        } else {
            upgradeButton.setDisabled(false);
        }
        
        if (equiped == 0) {
            downgradeButton.setDisabled(true);
        } else {
            downgradeButton.setText(I18n.t("unequip"));
            downgradeButton.setDisabled(false);
        }
    }
    
    /**
     * Adds all Buttons and Labels needed after selecting an Upgrade to
     * buy/equip
     */
    private void updateUpgradeDetailTable() {
        upgradeDetailRightTable.clear();
        upgradeDetailLeftTable.clear();
        if (chosenUpgrade == null)
            return;
        int equiped = PlaneManager.getInstance().getChosenPlane().getEquipedUpgradeCount(chosenUpgrade.name);
        int bought = PlaneManager.getInstance().getChosenPlane().getBoughtUpgradeCount(chosenUpgrade.name);
        int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
        
        // setting up the Lables and Buttons according to current Upgrade
        setOptionButtonsState(money, bought, equiped);
        
        upgradeNameLabel.setText(I18n.t(chosenUpgrade.name));
        upgradeDetailLeftTable.add(upgradeNameLabel).center().colspan(2).pad(UI.Tables.PADDING);
        upgradeDetailLeftTable.row();
        
        List<String> changes = getUpdateChanges();
        for (int i = 0; i < changes.size(); i++) {
            upgradeDetailLeftTable.add(new Label(changes.get(i), skin)).left().colspan(2);
            upgradeDetailLeftTable.row();
        }
        
        upgradeCostLabel.setText(I18n.t("cost") + ": " + chosenUpgrade.price + "");
        upgradeDetailLeftTable.add(upgradeCostLabel).left();
        upgradeDetailLeftTable.row();
        
        currentMoneyLabel.setText(I18n.t("gainMoney") + ": " + money);
        upgradeDetailRightTable.add(currentMoneyLabel);
        upgradeDetailRightTable.row();
        
        upgradeBoughtLabel.setText(I18n.t("bought") + ": " + bought + "/" + chosenUpgrade.timesAvailable);
        upgradeDetailRightTable.add(upgradeBoughtLabel);
        upgradeDetailRightTable.row();
        
        upgradeDetailRightTable.add(buyButton).pad(UI.Window.BORDER_SPACE);
        upgradeDetailRightTable.row();
        
        upgradeEquipedLabel.setText(I18n.t("equiped") + ": " + equiped + "");
        upgradeDetailRightTable.add(upgradeEquipedLabel);
        upgradeDetailRightTable.row();
        
        Table buttonTable = new Table();
        buttonTable.add(upgradeButton).pad(UI.Tables.PADDING);
        buttonTable.add(downgradeButton).pad(UI.Tables.PADDING);
        upgradeDetailRightTable.add(buttonTable);
        
    }
    
    /**
     * Returns a String that describes the changes by the current Upgrade
     * 
     * @return The String that describes the changes by the current Upgrade
     */
    private List<String> getUpdateChanges() {
        ArrayList<String> changes = new ArrayList<String>();
        int[] values = chosenUpgrade.upgradeValues;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                changes.add(I18n.t(names[i]) + " +" + values[i]);
            }
        }
        return changes;
    }
    
    @Override
    public void show() {
        super.show();
        updateUpgradeDetailTable();
        // place spaceship a little left of the middle a little down
        if (!shipMoved) {
            currentSpaceship.transform.translate(-0.8f, -0.5f, 0f);
            shipMoved = true;
        }
    }
}
