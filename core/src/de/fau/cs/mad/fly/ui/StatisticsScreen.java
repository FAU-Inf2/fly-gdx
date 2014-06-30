package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.settings.AppSettingsManager;

/*
 * UI for checking scores of user, also for add and change user
 * 
 * 
 * @author Qufang Fan
 */
public class StatisticsScreen extends BasicScreen {

	TextButton changeUserButton;
	TextButton addUserButton;
	TextButton uploadScoreButton;
	TextButtonStyle textButtonStyle;

	public void initButtons() {
		textButtonStyle = skin.get(UI.Buttons.STYLE, TextButtonStyle.class);

		changeUserButton = new TextButton(I18n.t("changeUserButtonText"),
				textButtonStyle);
		addUserButton = new TextButton(I18n.t("addUserButtonText"),
				textButtonStyle);

		changeUserButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				new Dialog(I18n.t("dialogTitle.changeUser"), skin) {
					{
						text(I18n.t("text.chooseUser"));
						for (Player player : PlayerManager.getInstance()
								.getAllPlayer()) {
							if (player.getId() != PlayerManager.getInstance()
									.getCurrentPlayer().getId()) {
								button(player.getName(), player);
							}
						}
						button(I18n.t("buttenText.cancel"), "cancel");
					}

					@Override
					protected void result(Object o) {
						if (o instanceof Player) {
							Player player = (Player) o;
							player.createSettings();
							PlayerManager.getInstance()
									.setCurrentPlayer(player);
							
							AppSettingsManager.Instance.setIntegerSetting(
									AppSettingsManager.CHOSEN_USER,
									player.getId());
							generateContentDynamic();
						}
					}

				}.show(stage);

			}
		});

		addUserButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				new Dialog(I18n.t("dialogTitle.addUser"), skin) {
					{
						TextField username = new TextField("", skin);
						text(I18n.t("text.inputUsername"));
						this.addActor(username);

						text("");
						button(I18n.t("buttenText.ok"), "ok");
						button(I18n.t("buttenText.cancel"), "cancel");
						usernameTF = username;
					}

					TextField usernameTF;

					@Override
					protected void result(Object o) {
						if (o.toString() == "ok") {
							String name = usernameTF.getText();
							if (name != "")// todo more check
							{
								Player player = new Player(name,
										PlayerManager.getInstance()
										.getMaxPlayerID() + 1);
								PlayerManager.getInstance().savePlayer(player);
								generateContentDynamic();
							}
						}
					}
				}.show(stage);

			}
		});

	}

	@Override
	protected void generateContent() {
		initButtons();
		generateContentDynamic();
	}

	private void generateContentDynamic() {
		stage.clear();
		final Table table = new Table();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);

		final Table infoTable = new Table();

		final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
		statisticsPane.setFadeScrollBars(false);
		statisticsPane.setScrollingDisabled(true, false);
		statisticsPane.setStyle(skin.get(
				UI.Window.TRANSPARENT_SCROLL_PANE_STYLE,
				ScrollPane.ScrollPaneStyle.class));

		String userName = PlayerManager.getInstance().getCurrentPlayer()
				.getName();
		infoTable.row().expand();
		infoTable.add(new Label(I18n.t("usernameLableText"), skin)).pad(6f)
				.uniform();
		infoTable.add(new Label(userName, skin)).pad(6f).uniform();

		infoTable.row().expand();
		if (PlayerManager.getInstance().getAllPlayer().size() > 1) {
			infoTable.add(changeUserButton).pad(6f).uniform();
		}
		infoTable.add(addUserButton).pad(6f).uniform();		

		List<Level.Head> allLevels = LevelManager.getInstance().getLevelList();
		boolean haveScore = false;
		for (Level.Head level : allLevels) {
			Score score = ScoreManager.getInstance().getLevelBestScore(
					PlayerManager.getInstance().getCurrentPlayer(), level);
			if (score != null && score.getTotalScore() > 0) {
				infoTable.row().expand();
				infoTable.add(new Label("", skin)).pad(6f).uniform();

				haveScore = true;
				infoTable.row().expand();
				infoTable.add(new Label(level.name, skin)).pad(6f).uniform();

				infoTable.add(new Label(score.getTotalScore() + "", skin))
						.pad(6f).uniform();
				for (ScoreDetail detail : score.getScoreDetails()) {
					infoTable.row().expand();
					infoTable
							.add(new Label(I18n.t(detail.getDetailName()), skin))
							.pad(6f).uniform();
					infoTable.add(new Label(detail.getValue(), skin)).pad(6f)
							.uniform();
				}

				uploadScoreButton = new TextButton(
						I18n.t("uploadScoreButtonText"), textButtonStyle);
				uploadScoreButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// todo
					}
				});
				infoTable.row().expand();
				infoTable.add(uploadScoreButton).pad(7f).uniform();
			}
		}
		if (!haveScore) {
			infoTable.row().expand();
			infoTable.add(new Label(I18n.t("noScore"), skin)).pad(6f).uniform();
		}
		
		infoTable.row().expand();

		TextButton globalHighScoreButton = new TextButton("Global high scores",textButtonStyle);
		globalHighScoreButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setGlobalHighScoreScreen();
			}
		});
		infoTable.add(globalHighScoreButton);

		table.row().expand();
		table.add(statisticsPane);
	}

	@Override
	public void show() {
		super.show();
		generateContentDynamic();

	}
}
