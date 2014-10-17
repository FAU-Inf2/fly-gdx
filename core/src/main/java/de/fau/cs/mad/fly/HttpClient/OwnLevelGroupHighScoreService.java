package de.fau.cs.mad.fly.HttpClient;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.fau.cs.mad.fly.profile.LevelGroup;

/**
 * Service to recieve the best own highscores of one {@link LevelGroup}.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class OwnLevelGroupHighScoreService {
    
    public OwnLevelGroupHighScoreService(FlyHttpResponseListener listener) {
        this.listener = listener;
    }
    
    private final FlyHttpResponseListener listener;
    
    /**
     * Send a request to the server to get all the best highscores (max. 1 per
     * level) of one user in one {@link LevelGroup}.
     */
    public void execute(int userId, int levelGroupId) {
        HttpRequest request = new HttpRequest(HttpMethods.GET);
        request.setTimeOut(RemoteServices.TIME_OUT);
        
        String url = RemoteServices.getServerURL() + "/users/" + userId + "/highscores?level_group_id=" + levelGroupId;
        Gdx.app.log("OwnLevelGroupHighScoreService", "Call: " + url);
        request.setUrl(url);
        
        RemoteServices.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                HttpStatus status = httpResponse.getStatus();
                if (status.getStatusCode() == HttpStatus.SC_OK) {
                    JsonReader reader = new JsonReader();
                    String ress = httpResponse.getResultAsString();
                    Gdx.app.log("GetLevelHighScoreService", "Received:" + ress);
                    JsonValue json = reader.parse(ress);
                    LevelGroupGlobalHighscores response = new LevelGroupGlobalHighscores();
                    
                    for (JsonValue jsonScore : json) {
                        RecordItem score = new RecordItem();
                        score.score = jsonScore.getInt("points");
                        score.rank = jsonScore.getInt("rank");
                        score.flyID = jsonScore.get("user").getInt("id");
                        score.username = jsonScore.get("user").getString("name");
                        int levelID = RemoteServices.getClientLevelID(jsonScore.getInt("level_id"));
                        String levelName = jsonScore.getString("level_id");
                        response.addRecord(levelID, levelName, score);
                    }
                    listener.successful(response);
                } else {
                    listener.failed(String.valueOf(status.getStatusCode()));
                }
                Gdx.app.log("GetLevelHighScoreService", "server return code: " + String.valueOf(status.getStatusCode()));
            }
            
            @Override
            public void failed(Throwable t) {
                Gdx.app.log("OwnLevelGroupHighScoreService", t.getMessage());
                listener.failed(t.getLocalizedMessage());
            }
            
            @Override
            public void cancelled() {
                listener.cancelled();
            }
        });
    }
}
