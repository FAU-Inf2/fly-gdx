package de.fau.cs.mad.fly.communication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.PostHighscoreService;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.ui.DialogWithOneButton;

/**
 * Waits for a new Fly-ID from the server to upload the first score for a
 * player.
 * <p>
 * 
 * When the a new Fly-ID is successfully generated, the
 * {@link #postHighscoreService} is executed with the new Fly-ID. Otherwise a
 * {@link DialogWithOneButton} is shown with a corresponding message. Therefore
 * the {@link #stageToShowMessage} is necessary.
 * 
 * @author Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class PostUserHttpRespListener implements FlyHttpResponseListener {
    
    private final PostHighscoreService.RequestData requestData;
    private final PostHighscoreService postHighscoreService;
    private final Stage stageToShowMessage;
    
    public PostUserHttpRespListener(PostHighscoreService.RequestData requestData, PostHighscoreService postHighscoreScreenService, Stage stageToShowMessage) {
        this.requestData = requestData;
        this.postHighscoreService = postHighscoreScreenService;
        this.stageToShowMessage = stageToShowMessage;
    }
    
    @Override
    public void successful(Object obj) {
        int flyID = Integer.valueOf(obj.toString());
        PlayerProfileManager profileManager = PlayerProfileManager.getInstance();
        profileManager.getCurrentPlayerProfile().setFlyID(flyID);
        profileManager.saveFlyID(profileManager.getCurrentPlayerProfile());
        requestData.FlyID = flyID;
        postHighscoreService.execute();
    }
    
    @Override
    public void failed(String msg) {
        // debug output
        Gdx.app.log("PostScoreHttpRespListener", ".failed:" + msg);
        
        // show dialog with message for user
        Dialog uploadFailedMessage = new DialogWithOneButton(I18n.t("ConnectServerError"), I18n.t("ok"));
        uploadFailedMessage.show(stageToShowMessage);
    }
    
    @Override
    public void cancelled() {
    }
}
