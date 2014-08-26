package de.fau.cs.mad.fly.features.overlay;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.profile.PlaneUpgradeManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.PlaneUpgrade;
import de.fau.cs.mad.fly.ui.PlaneChooserScreen;
import de.fau.cs.mad.fly.ui.PlaneUpgradeScreen;
import de.fau.cs.mad.fly.ui.UI;

public class PlaneUpgradesOverlay {
	private final Skin skin;
    private final Stage stage;
    private final PlaneUpgradeScreen screen;
    
    private Table scrollableTable;
    
    private Button openButton;
	
	public PlaneUpgradesOverlay(final Skin skin, final Stage stage, PlaneUpgradeScreen screen) {
		this.skin = skin;
		this.stage = stage;
		this.screen = screen;
	}
	
	public void init() {
		scrollableTable = new Table(skin);
		scrollableTable.top().right();
		ScrollPane scrollPane = new ScrollPane(scrollableTable, skin);
		scrollPane.setFillParent(true);
		//scrollPane.setScrollingDisabled(true, false);
		//scrollPane.setBounds(UI.Window.REFERENCE_WIDTH/3*2, 0, UI.Window.REFERENCE_WIDTH/3, UI.Window.REFERENCE_HEIGHT);
		scrollPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));
		
		createButtons(PlaneManager.getInstance().getChosenPlane());
		
		stage.addActor(scrollPane);
	}
	
	public void createButtons(IPlane.Head plane) {
		int[] upgradeTypes = plane.upgradeTypes;
		int size = upgradeTypes.length;
		final Collection<PlaneUpgrade> upgrades = PlaneUpgradeManager.getInstance().getUpgradeList().values();
		
		//scrollableTable.center();
		
		int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
		
		LabelStyle labelStyle = skin.get("red", LabelStyle.class);
		final Label nameLabel = new Label("current money: " + money, labelStyle);
        scrollableTable.addActor(nameLabel);
		
		for(final PlaneUpgrade upgrade : upgrades) {
			for(int i = 0; i < size; i++) {
				if(upgrade.type == upgradeTypes[i]) {
					final TextButton minus = new TextButton("  -  ", skin, UI.Buttons.DEFAULT_STYLE);
					final TextButton plus = new TextButton(" + ", skin, UI.Buttons.DEFAULT_STYLE);
					minus.pad(10);
					
					int equiped = PlaneManager.getInstance().getChosenPlane().upgradesEquiped.get(upgrade.name);
					int bought = PlaneManager.getInstance().getChosenPlane().upgradesBought.get(upgrade.name);
					
					final String name = upgrade.name;
					
					if(equiped == 0) {
						minus.setText("min");
						minus.setColor(Color.GRAY);
					}
					
					minus.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							int equiped = PlaneManager.getInstance().getChosenPlane().upgradesEquiped.get(upgrade.name);
							
							if(equiped != 0) {
								if(equiped == upgrade.timesAvailable) {
									plus.setText(" + ");
									plus.setColor(Color.WHITE);
								}
								
								PlaneManager.getInstance().upgradePlane(name, -1);
								screen.update();
								
								if(equiped - 1 == 0) {
									minus.setText("min");
									minus.setColor(Color.GRAY);
								}
							}
						}
					});
					
					final TextButton button = new TextButton(name, skin, UI.Buttons.DEFAULT_STYLE);
					button.pad(10);
					
					if(!PlaneManager.getInstance().upgradeCanBeBought(upgrade)) {
						button.setText("can't buy");
						button.setColor(Color.GRAY);
					}
					
					button.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							boolean canBeBought = PlaneManager.getInstance().upgradeCanBeBought(upgrade);
							
							if(canBeBought) {
								PlaneManager.getInstance().buyUpgradeForPlane(name);
								int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
								nameLabel.setText("current money: " + money);
								
								plus.setText(" + ");
								plus.setColor(Color.WHITE);
								
								if(!PlaneManager.getInstance().upgradeCanBeBought(upgrade)) {
									button.setText("can't buy");
									button.setColor(Color.GRAY);
								}
							}
						}
					});

					
					plus.pad(10);
					
					if(equiped == bought) {
						plus.setText("max");
						plus.setColor(Color.GRAY);
					}

					plus.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							int equiped = PlaneManager.getInstance().getChosenPlane().upgradesEquiped.get(upgrade.name);
							int bought = PlaneManager.getInstance().getChosenPlane().upgradesBought.get(upgrade.name);
							if(equiped != bought) {
								if(equiped == 0) {
									minus.setText(" - ");
									minus.setColor(Color.WHITE);
								}
								
								PlaneManager.getInstance().upgradePlane(name, 1);
								screen.update();
								
								if(equiped + 1 == bought) {
									plus.setText("max");
									plus.setColor(Color.GRAY);
								}
							}
						}
					});
					
					scrollableTable.add(minus).space(20);
					scrollableTable.add(button).space(20);
					scrollableTable.add(plus).space(20);
					scrollableTable.row();
					break;
				}
			}
		}
		scrollableTable.add();
		Button button = new TextButton("cancel", skin, UI.Buttons.DEFAULT_STYLE);
		
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				/*scrollableTable.clear();
				scrollableTable.top().right();
				scrollableTable.add(openButton);*/
				((Fly) Gdx.app.getApplicationListener()).setPlaneChoosingScreen();
			}
		});
		
		scrollableTable.add(button.pad(1));
		
	}
	
	public void show() {
		scrollableTable.clear();
		scrollableTable.top().right();
		scrollableTable.add(openButton);
	}
	
	public void update() {
		
	}
}
