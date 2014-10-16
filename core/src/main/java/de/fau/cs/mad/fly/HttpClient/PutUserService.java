package de.fau.cs.mad.fly.HttpClient;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpStatus;
import de.fau.cs.mad.fly.profile.PlayerProfile;

public class PutUserService {
	
    
    public PutUserService(FlyHttpResponseListener listener) {
        this.listener = listener;
    }
    
    private final FlyHttpResponseListener listener;
    
    public void execute(PlayerProfile player) {
        String subURL = "/users/" + player.getFlyID();
        String userData = "{ \"user\": { \"name\": \"" + player.getName() + "\" } }";
        HttpRequest post = new HttpRequest(HttpMethods.PUT);
        post.setHeader("Content-Type", "application/json");
        post.setUrl(RemoteServices.getServerURL() + subURL);
        post.setContent(userData);
        
        Gdx.app.log("PutUserService", "sending:" + userData);
        RemoteServices.sendHttpRequest(post, new HttpResponseListener() {
            
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                
            	 HttpStatus status = httpResponse.getStatus();
                 if (status.getStatusCode() == HttpStatus.SC_OK || status.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                     Gdx.app.log("PutUserService", " successful with code:" + status.getStatusCode());
                     listener.successful(new Object());
                 } else {
                     Gdx.app.log("PutUserService", "update user name services return code: " + String.valueOf(status.getStatusCode()));
                     listener.failed(String.valueOf(status.getStatusCode()));
                 }
            }
            
            @Override
            public void failed(Throwable t) {
                Gdx.app.log("PutUserService", "update user name services failed:" + t.getMessage());
                listener.failed(t.getLocalizedMessage());
            }
            
            @Override
            public void cancelled() {
                listener.cancelled();
            }
        });
    }
}
