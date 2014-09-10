package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService;
import de.fau.cs.mad.fly.HttpClient.GetLevelHighScoreService.ResponseItem;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.LevelProfile;

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
                    infoTable.row();
                    infoTable.add(new Label(I18n.t("level"), skin)).left();
                    infoTable.add(new Label(I18n.t("player"), skin)).left();
                    infoTable.add(new Label(I18n.t("score"), skin)).left();
                    infoTable.row();
                    if (results != null && results.size() > 0) {
                        infoTable.row();
                        infoTable.add(new Label(level.name, skin, "darkGrey")).pad(0, 0, 0, padding).left();
                        
                        for (ResponseItem item : results) {
                            infoTable.row();
                            infoTable.add(new Label(item.FlyID + "", skin, "darkGrey")).pad(0, 0, 0, padding);
                            infoTable.add(new Label(item.Username, skin, "darkGrey")).pad(0, 0, 0, padding).left();
                            infoTable.add(new Label(item.Score + "", skin, "darkGrey")).pad(0, 0, 0, padding).right();
                        }
                    } else {
                        infoTable.row();
                        infoTable.add(new Label(level.name, skin, "darkGrey")).pad(0, 0, 0, padding).left();
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
            infoTable.row().expand();
            infoTable.add(new Label(msg, skin)).pad(6f).uniform();
        }
        
        @Override
        public void cancelled() {
            
        }
    }
    
    /**
     * generate Content
     * 
     * @see de.fau.cs.mad.fly.ui.BasicScreen#generateContent()
     */
    @Override
    protected void generateContent() {
        stage.clear();
        final Table table = new Table();
        table.pad(UI.Window.BORDER_SPACE);
        table.setFillParent(true);
        stage.addActor(table);
        infoTable = new Table();
        
        final ScrollPane statisticsPane = new ScrollPane(infoTable, skin);
        statisticsPane.setFadeScrollBars(false);
        statisticsPane.setScrollingDisabled(true, false);
        table.row().expand();
        table.add(statisticsPane);
    }
    
    protected void generateContentDynamic() {
        infoTable.clear();
        
        // todo
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
