package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService.LevelRecords;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService.RecordItem;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService.ResponseData;
import de.fau.cs.mad.fly.profile.LevelGroup;

/**
 * Screen to display the global highscores for a level group.
 * 
 * @author Qufang Fan
 * 
 */
public class GlobalHighScoreScreen extends BasicScreen {
    
    private Table infoTable;
    
    /** space between the columns */
    private final float padding = 320;
    
    private LevelGroup levelGroup;
    
    public GlobalHighScoreScreen(LevelGroup group) {
        levelGroup = group;
    }
    
    public class GetLevelHighScoreListener implements FlyHttpResponseListener {
        
        final LevelGroup levelGroup;
        
        public GetLevelHighScoreListener(LevelGroup levelGroup) {
            this.levelGroup = levelGroup;
        }
        
        @Override
        public void successful(Object obj) {
            final ResponseData results = (ResponseData) obj;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    infoTable.clear();
                    infoTable.row();
                    Skin skin = SkinManager.getInstance().getSkin();
                    if (results != null && results.records.size() > 0) {
                        for (LevelRecords item : results.records) {
                            String levelname = levelGroup.getLevelName(item.levelID);
                            infoTable.row();
                            infoTable.add(new Label(I18n.t("level"), skin)).left();
                            infoTable.add(new Label(levelname, skin, "darkGrey")).pad(0, 0, 0, padding).left();
                            infoTable.row();
                            infoTable.add(new Label(I18n.t("flyID"), skin)).left();
                            infoTable.add(new Label(I18n.t("player"), skin)).left();
                            infoTable.add(new Label(I18n.t("score"), skin)).left();
                            infoTable.add(new Label(I18n.t("rank"), skin)).left();
                            
                            for (RecordItem item0 : item.records) {
                                infoTable.row();
                                infoTable.add(new Label(item0.flyID + "", skin, "darkGrey")).pad(0, 0, 0, padding);
                                infoTable.add(new Label(item0.username, skin, "darkGrey")).pad(0, 0, 0, padding).left();
                                infoTable.add(new Label(item0.score + "", skin, "darkGrey")).pad(0, 0, 0, padding).right();
                                infoTable.add(new Label(item0.rank + "", skin, "darkGrey")).pad(0, 0, 0, padding).right();
                            }
                        }
                    } else {
                        infoTable.row();
                        infoTable.add(new Label(levelGroup.name, skin, "darkGrey")).pad(0, 0, 0, padding).left();
                        infoTable.row();
                        infoTable.add(new Label(I18n.t("noScore"), skin, "darkGrey"));
                    }
                    infoTable.row();
                    infoTable.add(new Label(" ", skin));
                    infoTable.row();
                }
            });
            
        }
        
        @Override
        public void failed(String msg) {
        	infoTable.clear();
        	infoTable.row().expand();
        	String showmsg;
        	if (msg != null && msg.length() > 21) {
        		showmsg = I18n.t("ConnectServerError") + msg.substring(0, 20) + "...";
            } else {
            	showmsg = I18n.t("ConnectServerError") + msg;
            }
        	Skin skin = SkinManager.getInstance().getSkin();
            infoTable.add(new Label(showmsg, skin)).pad(6f).uniform();
        }
        
        @Override
        public void cancelled() {
            
        }
    }
    
    /**
     * generate Content
     * 
     * @see de.fau.cs.mad.fly.ui.BasicScreen#generateBackButton()
     */
    @Override
    protected void generateBackButton() {
        stage.clear();    
        infoTable = new Table();
        infoTable.pad(UI.Window.BORDER_SPACE);
        infoTable.setFillParent(true);
        
        Skin skin = SkinManager.getInstance().getSkin();
        final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
        statisticsPane.setFadeScrollBars(false);
        statisticsPane.setScrollingDisabled(true, false);
        statisticsPane.setFillParent(true);
        
        infoTable.add(new Label(I18n.t("StatusLoading"), skin));
        
        stage.addActor(statisticsPane);
    }
    
    protected void generateContentDynamic() {
        final FlyHttpResponseListener listener = new GetLevelHighScoreListener(levelGroup);
        GetLevelHighScoreService getLevelHighScoreService = new GetLevelHighScoreService(listener);
        getLevelHighScoreService.execute(2, levelGroup.id);
    }
    
    @Override
    public void show() {
        super.show();
        generateContentDynamic();
    }
    
}
