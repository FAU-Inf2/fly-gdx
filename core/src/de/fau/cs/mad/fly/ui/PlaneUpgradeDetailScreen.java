package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        upgradeDetailLeftTable = new Table();
        
        initUpgradeDetailTable();
        generateBackButton();
    }
    
    private void initUpgradeDetailTable() {
        Table outTable = new Table();
        outTable.setFillParent(true);
        outTable.pad(0f);
        ScrollPane scrollPane = new ScrollPane(outTable, skin);
        scrollPane.setFillParent(true);
        scrollPane.setFadeScrollBars(false);
        
        updateUpgradeDetailTable();
        
        outTable.add(upgradeDetailLeftTable).left().top().pad(UI.Window.BORDER_SPACE).expand();
        outTable.row();
        outTable.add(upgradeDetailRightTable).right().top().pad(UI.Window.BORDER_SPACE).expand();
        stage.addActor(scrollPane);
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
        currentMoneyLabel.setText(money + "");
        upgradeBoughtLabel.setText(bought + "");
        upgradeEquipedLabel.setText(equiped + "");
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
        
        upgradeDetailLeftTable.add(new Label(I18n.t("upgrade") + ": ", skin)).right();
        upgradeNameLabel.setText(I18n.t(chosenUpgrade.name));
        upgradeDetailLeftTable.add(upgradeNameLabel).left();
        upgradeDetailLeftTable.row();
        
        List<String> changes = getUpdateChanges();
        for (int i = 0; i < changes.size(); i++) {
            if (i == 0) {
                upgradeDetailLeftTable.add(new Label(I18n.t("changes") + ": ", skin)).right();
                upgradeDetailLeftTable.add(new Label(changes.get(i), skin)).left();
                upgradeDetailLeftTable.row();
            } else {
                upgradeDetailLeftTable.add();
                upgradeDetailLeftTable.add(new Label(changes.get(i), skin)).left();
                upgradeDetailLeftTable.row();
            }
        }
        
        upgradeDetailLeftTable.add(new Label(I18n.t("cost") + ": ", skin)).right();
        upgradeCostLabel.setText(chosenUpgrade.price + "");
        upgradeDetailLeftTable.add(upgradeCostLabel).left();
        upgradeDetailLeftTable.row();
        
        upgradeDetailLeftTable.add(new Label(I18n.t("gainMoney") + ": ", skin)).right();
        currentMoneyLabel.setText("" + money);
        upgradeDetailLeftTable.add(currentMoneyLabel).left();
        upgradeDetailLeftTable.row();
        
        upgradeDetailRightTable.add(new Label(I18n.t("bought") + ": ", skin)).right();
        upgradeBoughtLabel.setText(bought + "/" + chosenUpgrade.timesAvailable);
        upgradeDetailRightTable.add(upgradeBoughtLabel).left();
        upgradeDetailRightTable.row();
        
        upgradeDetailRightTable.add(buyButton).padBottom(100f).center().colspan(2);
        upgradeDetailRightTable.row();
        
        upgradeDetailRightTable.add(new Label(I18n.t("equiped") + ": ", skin)).right();
        upgradeEquipedLabel.setText(equiped + "");
        upgradeDetailRightTable.add(upgradeEquipedLabel).left();
        upgradeDetailRightTable.row();
        
        upgradeDetailRightTable.add(downgradeButton).space(20).left();
        upgradeDetailRightTable.add(upgradeButton).space(20).left();
        upgradeDetailRightTable.row();
        
        upgradeDetailRightTable.add().space(20).left();
        upgradeDetailRightTable.add().space(20).left();
        upgradeDetailRightTable.row();
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
        currentSpaceship.transform.translate(-0.8f, -0.4f, 0f);
    }
}
