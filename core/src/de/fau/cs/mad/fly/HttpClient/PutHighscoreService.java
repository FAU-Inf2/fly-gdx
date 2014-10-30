package de.fau.cs.mad.fly.HttpClient;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpStatus;
import de.fau.cs.mad.fly.HttpClient.PostHighscoreService.RequestData;

public class PutHighscoreService {
    
    public PutHighscoreService(FlyHttpResponseListener listener, RequestData requestData) {
        this.listener = listener;
        this.requestData = requestData;
    }
    
    private final RequestData requestData;
    private final FlyHttpResponseListener listener;
    
    public void execute() {
        HttpRequest request = new HttpRequest(HttpMethods.PUT);
        request.setTimeOut(RemoteServices.TIME_OUT);
        request.setHeader("Content-Type", "application/json");
        String url = RemoteServices.getServerURL() + "/highscores/" + requestData.Score.getServerScoreId();
        Gdx.app.log("PutHighscoreService", "call:" + url);
        request.setUrl(url);
        String res = "{ \"highscore\": { \"points\":" + requestData.Score.getTotalScore() + " } }";
        request.setContent(res);
        Gdx.app.log("PutHighscoreService", "send:" + res);
        
        RemoteServices.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                HttpStatus status = httpResponse.getStatus();
                if (status.getStatusCode() == HttpStatus.SC_OK || status.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                	 String ress = httpResponse.getResultAsString();
                     Gdx.app.log("PutHighscoreService", "Received:" + ress );
                    listener.successful(new Object());
                } else {
                    listener.failed(String.valueOf(status.getStatusCode()));
                }
                Gdx.app.log("PutHighscoreService", "server return code: " + String.valueOf(status.getStatusCode()));
            }
            
            @Override
            public void failed(Throwable t) {
                listener.failed(t.getLocalizedMessage());
                Gdx.app.log("PutHighscoreService", "server return msg:" + t.getMessage());
            }
            
            @Override
            public void cancelled() {
                listener.cancelled();
            }
        });
    }
    
}
