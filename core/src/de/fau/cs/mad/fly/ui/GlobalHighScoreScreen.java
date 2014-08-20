/**
 * 
 */
package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService.ResponseItem;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.LevelProfile;

/**
 * @author Qufang Fan
 * 
 */
public class GlobalHighScoreScreen extends BasicScreen {

	private Table infoTable;

	public class GetLevelHighScoreListener implements FlyHttpResponseListener {

		final LevelProfile level;

		public GetLevelHighScoreListener(LevelProfile level) {
			this.level = level;
		}

		@Override
		public void successful(Object obj) {
			final List<ResponseItem> results = (List<ResponseItem>) obj;
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					if (results != null && results.size() > 0) {
						infoTable.row().expand();
						infoTable.add(new Label(level.name, skin)).pad(6f).uniform();

						for (ResponseItem item : results) {
							infoTable.row().expand();
							infoTable.add(new Label(item.FlyID + "", skin)).pad(6f).uniform();
							infoTable.add(new Label(item.Username, skin)).pad(6f).uniform();
							infoTable.add(new Label(item.Score + "", skin)).pad(6f).uniform();
						}
					} else {
						infoTable.row().expand();
						infoTable.add(new Label(level.name, skin)).pad(6f).uniform();
						infoTable.row().expand();
						infoTable.add(new Label("No record yet!", skin)).pad(8f).uniform();
					}
				}
			});

		}

		@Override
		public void failed(String msg) {
			infoTable.row().expand();
			infoTable.add(new Label(msg, skin)).pad(6f).uniform();
		}

		@Override
		public void cancelled() {

		}
	}

	/*
	 * generate Content
	 * 
	 * @see de.fau.cs.mad.fly.ui.BasicScreen#generateContent()
	 */
	@Override
	protected void generateContent() {
		stage.clear();
		final Table table = new Table();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);
		infoTable = new Table();

		final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
		statisticsPane.setFadeScrollBars(false);
		statisticsPane.setScrollingDisabled(true, false);
		statisticsPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE,
				ScrollPane.ScrollPaneStyle.class));
		table.row().expand();
		table.add(statisticsPane);
	}

	protected void generateContentDynamic() {
		infoTable.clear();

		//todo
		List<LevelProfile> allLevels = LevelGroupManager.getInstance().getLevelGroups().get(0).getLevels();
		for (LevelProfile level : allLevels) {
			final FlyHttpResponseListener listener = new GetLevelHighScoreListener(level);
			GetLevelHighScoreService getLevelHighScoreService = new GetLevelHighScoreService(listener);

			getLevelHighScoreService.execute(level.id);
		}
	}

	@Override
	public void show() {
		super.show();
		generateContentDynamic();
	}

}
