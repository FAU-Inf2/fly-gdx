package de.fau.cs.mad.fly.features.overlay;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlaneManager;
import de.fau.cs.mad.fly.profile.PlaneUpgradeManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.PlaneUpgrade;
import de.fau.cs.mad.fly.ui.PlaneChooserScreen;
import de.fau.cs.mad.fly.ui.UI;

public class PlaneUpgradesOverlay {
	private final Skin skin;
    private final Stage stage;
    private final PlaneChooserScreen screen;
	private float screenHeight = Gdx.graphics.getHeight();
	private float screenWidth = Gdx.graphics.getWidth();
    
    private Table scrollableTable;
    
    private Button openButton;
	
	public PlaneUpgradesOverlay(final Skin skin, final Stage stage, PlaneChooserScreen screen) {
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
		//scrollPane.setBounds(UI.Window.REFERENCE_WIDTH/3*2, UI.Window.REFERENCE_HEIGHT/3, UI.Window.REFERENCE_WIDTH/3, UI.Window.REFERENCE_HEIGHT/3*2);
		scrollPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));
		
		openButton = new ImageButton(skin.get(UI.Buttons.SETTING_BUTTON_STYLE, ImageButtonStyle.class));
		
		scrollableTable.add(openButton);
		
		
		openButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				activate();
				createButtons(PlaneManager.getInstance().getChosenPlane());
			}
		});
		
		stage.addActor(scrollPane);
	}
	
	public void createButtons(IPlane.Head plane) {
		int[] upgradeTypes = plane.upgradeTypes;
		int size = upgradeTypes.length;
		Collection<PlaneUpgrade> upgrades = PlaneUpgradeManager.getInstance().getUpgradeList().values();
		
		//scrollableTable.center();
		
		int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
		
		LabelStyle labelStyle = skin.get("red", LabelStyle.class);
		final Label nameLabel = new Label("current money: " + money, labelStyle);
        scrollableTable.addActor(nameLabel);
		
		for(PlaneUpgrade upgrade : upgrades) {
			for(int i = 0; i < size; i++) {
				if(upgrade.type == upgradeTypes[i]) {
					Button minus = new TextButton("-", skin, UI.Buttons.DEFAULT_STYLE);
					
					final String name = upgrade.name;
					
					minus.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							PlaneManager.getInstance().upgradePlane(name, -1);
							screen.update();
						}
					});
					
					Button button = new TextButton(name, skin, UI.Buttons.DEFAULT_STYLE);
					
					button.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							PlaneManager.getInstance().buyUpgradeForPlane(name);
							int money = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getMoney();
							nameLabel.setText("current money: " + money);
						}
					});

					Button plus = new TextButton("+", skin, UI.Buttons.DEFAULT_STYLE);

					plus.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							PlaneManager.getInstance().upgradePlane(name, 1);
							screen.update();
						}
					});
					
					scrollableTable.add(minus.pad(1));
					scrollableTable.add(button.pad(1));
					scrollableTable.add(plus.pad(1));
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
				scrollableTable.clear();
				scrollableTable.top().right();
				scrollableTable.add(openButton);
			}
		});
		
		scrollableTable.add(button.pad(1));
		
	}
	
	public void show() {
		scrollableTable.clear();
		scrollableTable.top().right();
		scrollableTable.add(openButton);
	}
	
	public void activate() {
		scrollableTable.removeActor(openButton);
	}
	
	public void update() {
		
	}
}
