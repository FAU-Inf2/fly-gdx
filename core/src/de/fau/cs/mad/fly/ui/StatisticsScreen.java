package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.res.Level;

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
	
	public void initButtons(){
		 changeUserButton = new TextButton(I18n.t("changeUserButtonText"), skin, "default");
		 addUserButton = new TextButton(I18n.t("addUserButtonText"), skin, "default");
		 uploadScoreButton = new TextButton(I18n.t("uploadScoreButtonText"), skin, "default");
		 
		 changeUserButton.addListener(new ClickListener() {
				@Override 
				public void clicked(InputEvent event, float x, float y) {
					//todo
				}
			});
			
			addUserButton.addListener(new ClickListener() {
				@Override 
				public void clicked(InputEvent event, float x, float y) {
					//todo
				}
			});
			
			uploadScoreButton.addListener(new ClickListener() {
				@Override 
				public void clicked(InputEvent event, float x, float y) {
					//todo
				}
			});
	}
	
	@Override
	protected void generateContent() {
		initButtons();
		generateContentInside();
	}
	
	private void generateContentInside(){
		stage.clear();
		final Table table = new Table();;
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);
		
		final Table infoTable = new Table();
		final ScrollPane settingPane = new ScrollPane(infoTable, skin);
		settingPane.setColor(UI.Window.BACKGROUND_COLOR);
		settingPane.setFadeScrollBars(false);
	 	settingPane.setScrollingDisabled(true, false);
	
	 	String userName = ((Fly) Gdx.app.getApplicationListener()).getPlayer().getName();
	 	infoTable.row().expand();
		infoTable.add(new Label(I18n.t("usernameLableText"), skin)).pad(6f).uniform();
		infoTable.add(new Label(userName,skin)).pad(6f).uniform();
		
		infoTable.row().expand();	
		changeUserButton.setDisabled(PlayerManager.Instance.getAllPlayer().size()>1);
		
		infoTable.add( changeUserButton ).pad(6f).uniform();
		infoTable.add( addUserButton ).pad(6f).uniform();
		
		infoTable.row().expand();
		infoTable.add( new Label("", skin) ).pad(6f).uniform();
		infoTable.row().expand();
		
		//infoTable.add(new Label(I18n.t("ScoresLableText"), skin)).pad(6f).uniform();
		
		List<Level.Head> allLevels = LevelChooserScreen.getLevelList();	
		boolean haveScore = false;
		for( Level.Head level : allLevels)
		{			
			 Score score = ScoreManager.Instance.getLevelBestScore(PlayerManager.Instance.getCurrentPlayer(), level);
			 if( score != null && score.getTotalScore()>0)
			 {
				 infoTable.row().expand();
				 infoTable.add( new Label("", skin) ).pad(6f).uniform();
					
				 haveScore = true;
				 infoTable.row().expand();
				 infoTable.add( new Label(level.name, skin)).pad(6f).uniform();			 	
			 
				 infoTable.add( new Label(score.getTotalScore()+"", skin)).pad(6f).uniform();	
				 for(ScoreDetail detail : score.getScoreDetails() )
				 {
					 infoTable.row().expand();
					 infoTable.add( new Label(I18n.t(detail.getDetailName()),skin)).pad(6f).uniform();
					 infoTable.add( new Label(detail.getValue(), skin)).pad(6f).uniform();
				 }
			 }
		}
		if( haveScore )
		{
			infoTable.row().expand();					
			infoTable.add(uploadScoreButton).pad(7f).uniform();		
		}
		else
		{
			infoTable.row().expand();
			infoTable.add(new Label(I18n.t("noScore"),skin)).pad(6f).uniform();
		}
		 
		table.row().expand();
		table.add(settingPane);	
	}

	@Override
	public void show() {
		generateContentInside();
	}
}
