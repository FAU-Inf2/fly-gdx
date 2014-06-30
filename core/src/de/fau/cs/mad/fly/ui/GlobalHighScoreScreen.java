/**
 * 
 */
package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.res.Level;

/**
 * @author Qufang Fan
 *
 */
public class GlobalHighScoreScreen extends BasicScreen {

	/* generate Content
	 * @see de.fau.cs.mad.fly.ui.BasicScreen#generateContent()
	 */
	@Override
	protected void generateContent() {
		generateContentDynamic();
	}
	
	protected void generateContentDynamic()
	{
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
		
		List<Level.Head> allLevels = LevelManager.getInstance().getLevelList();
		for (Level.Head level : allLevels) {
			infoTable.row().expand();
			infoTable.add(new Label(level.name, skin)).pad(6f).uniform();
			
			infoTable.row().expand();
			infoTable.add(new Label("Fly ID", skin)).pad(6f).uniform();
			infoTable.add(new Label("Score", skin)).pad(6f).uniform();
			infoTable.add(new Label("No.", skin)).pad(6f).uniform();
			infoTable.row().expand();
			infoTable.add(new Label("001", skin)).pad(6f).uniform();
			infoTable.add(new Label("300", skin)).pad(6f).uniform();
			infoTable.add(new Label("1", skin)).pad(6f).uniform();
			infoTable.row().expand();
			infoTable.add(new Label("002", skin)).pad(6f).uniform();
			infoTable.add(new Label("280", skin)).pad(6f).uniform();
			infoTable.add(new Label("2", skin)).pad(6f).uniform();
			infoTable.row().expand();
			infoTable.add(new Label("008(Fan)", skin)).pad(6f).uniform();
			infoTable.add(new Label("100", skin)).pad(6f).uniform();
			infoTable.add(new Label("89", skin)).pad(6f).uniform();
			infoTable.row().expand();
			
		}
		
		table.row().expand();
		table.add(statisticsPane);
	}
	
	@Override
	public void show() {
		super.show();
		generateContentDynamic();

	}

}
