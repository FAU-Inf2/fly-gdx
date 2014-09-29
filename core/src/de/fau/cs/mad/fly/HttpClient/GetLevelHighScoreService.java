package de.fau.cs.mad.fly.HttpClient;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class GetLevelHighScoreService {
    
    public class ResponseData {
        public List<LevelRecords> records = new ArrayList<LevelRecords>();
        
        public void addRecord(int levelId, String levelName, RecordItem record) {
            for (LevelRecords levelRecords : records) {
                if (levelRecords.levelID == levelId) {
                    levelRecords.records.add(record);
                    return;
                }
            }
            
            LevelRecords levelRecords = new LevelRecords();
            levelRecords.levelID = levelId;
            levelRecords.levelName = levelName;
            levelRecords.records.add(record);
            records.add(levelRecords);
        }
    }
    
    public class LevelRecords {
        public int levelID;
        public String levelName;
        public List<RecordItem> records = new ArrayList<RecordItem>();
    }
    
    public class RecordItem {
        public int score;
        public String username;
        public int flyID;
        public int rank;
    }
    
    public GetLevelHighScoreService(FlyHttpResponseListener listener) {
        this.listener = listener;
    }
    
    private final FlyHttpResponseListener listener;
    
    /** These best scores are always shown, weather the own score is in or not */
    private static int TOP_SCORE_TO_SHOW = 3;
    
    /**
     * sent get level or level group highscores request to server
     * 
     * @param type
     *            1: get level high score; 2: get group high score
     * @param levelID
     *            or level group id
     */
    public void execute(int type, int levelID) {
        HttpRequest request = new HttpRequest(HttpMethods.GET);
        request.setTimeOut(RemoteServices.TIME_OUT);
        if (type == 1) {
        	String url = RemoteServices.getServerURL() + "/levels/" + levelID + "/highscores?top=" + TOP_SCORE_TO_SHOW;
            Gdx.app.log("GetLevelHighScoreService", "Call: " + url);
        	request.setUrl(url);
        } else {
        	String url = RemoteServices.getServerURL() + "/level_groups/" + levelID + "/highscores?top=" + TOP_SCORE_TO_SHOW + "&top_by=levels";
        	Gdx.app.log("GetLevelHighScoreService", "Call: " + url);
        	request.setUrl(url);
        }
        
        RemoteServices.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                HttpStatus status = httpResponse.getStatus();
                if (status.getStatusCode() == HttpStatus.SC_OK) {
                    JsonReader reader = new JsonReader();
                    String ress = httpResponse.getResultAsString();
                    Gdx.app.log("GetLevelHighScoreService", "Received:" + ress );
                    JsonValue json = reader.parse(ress);
                    ResponseData response = new ResponseData();
                    
                    for (JsonValue item : json) {
                        RecordItem res = new RecordItem();
                        res.score = item.getInt("points");
                        res.rank = item.getInt("rank");
                        res.flyID = item.get("user").getInt("id");
                        res.username = item.get("user").getString("name");
                        int levelID = RemoteServices.getClientLevelID(item.getInt("level_id"));
                        String levelName = item.getString("level_id");//wont be used
                        response.addRecord(levelID, levelName, res);
                    }
                    listener.successful(response);
                } else {
                	listener.failed(String.valueOf(status.getStatusCode()));
                }
                Gdx.app.log("GetLevelHighScoreService", "server return code: " + String.valueOf(status.getStatusCode()));
            }
            
            @Override
            public void failed(Throwable t) {
                Gdx.app.log("GetLevelHighScoreService", t.getMessage());
                listener.failed(t.getLocalizedMessage());
            }
            
            @Override
            public void cancelled() {
                listener.cancelled();
            }
        });
    }
}
