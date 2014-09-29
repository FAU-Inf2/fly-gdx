package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

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
public class GlobalHighscoreScreen extends BasicScreenWithBackButton {
    
    private Table infoTable;
    
    private ScrollPane scrollPane;
    
    /** space between the columns */
    private final float padding = 220;
    
    private LevelGroup levelGroup;
    
    public GlobalHighscoreScreen(BasicScreen screenToGoBack) {
        super(screenToGoBack);
    }
    
    public void setLevelGroup(LevelGroup group) {
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
                    Skin skin = SkinManager.getInstance().getSkin();
                    infoTable.clear();
                    infoTable = new Table();
                    
                    scrollPane = new ScrollPane(infoTable, skin, "semiTransparentBackground");
                    scrollPane.setFadeScrollBars(false);
                    scrollPane.setScrollingDisabled(true, false);
                    
                    if (results != null && results.records.size() > 0) {
                        for (LevelRecords level : results.records) {
                            String levelname = levelGroup.getLevelName(level.levelID);
                            infoTable.row();
                            infoTable.add(new Label(I18n.t("level") + " " + levelname, skin)).left().height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                            infoTable.row();
                            infoTable.add(new Label(I18n.t("player"), skin)).left().height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                            infoTable.add(new Label(I18n.t("score"), skin)).left().height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(0, padding, 0, padding);
                            infoTable.add(new Label(I18n.t("rank"), skin)).left().height(UI.Buttons.TEXT_BUTTON_HEIGHT).pad(0, 0, 0, padding);
                            Gdx.app.log("GlobalHighscoreScreen", "level id:" + level.levelID + " count:" + level.records.size());
                            
                            for (RecordItem user : level.records) {
                                infoTable.row();
                                infoTable.add(new Label(user.username + " (" + user.flyID + ")", skin, "darkGrey")).left().height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                                infoTable.add(new Label(user.score + "", skin, "darkGrey")).right().pad(0, padding, 0, padding);
                                infoTable.add(new Label(user.rank + "", skin, "darkGrey")).right().pad(0, 0, 0, padding);
                            }
                            infoTable.row();
                        }
                    } else {
                        infoTable.row();
                        infoTable.add(new Label(levelGroup.name, skin)).pad(0, 0, 0, padding).left().height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                        infoTable.row();
                        infoTable.add(new Label(I18n.t("noScore"), skin, "darkGrey")).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
                    }
                    Table outerTable = new Table();
                    outerTable.setFillParent(true);
                    outerTable.add(scrollPane).pad(UI.Window.BORDER_SPACE);
                    stage.addActor(outerTable);
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
            infoTable.add(new Label(showmsg, skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
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
        Skin skin = SkinManager.getInstance().getSkin();
        stage.clear();
        infoTable = new Table();
        infoTable.setBackground(new NinePatchDrawable(skin.get("semiTransparentBackground", NinePatch.class)));
        infoTable.setFillParent(true);
        
        scrollPane = new ScrollPane(infoTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        
        infoTable.add(new Label(I18n.t("StatusLoading"), skin)).height(UI.Buttons.TEXT_BUTTON_HEIGHT);
        
        generateBackButton();
        contentTable.add(scrollPane);
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
