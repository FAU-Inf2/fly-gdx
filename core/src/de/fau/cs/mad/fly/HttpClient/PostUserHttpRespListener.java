package de.fau.cs.mad.fly.HttpClient;

import de.fau.cs.mad.fly.profile.PlayerProfile;
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
    private final FlyHttpResponseListener listener;
    
    public PostUserHttpRespListener(PostHighscoreService.RequestData requestData, PostHighscoreService postHighscoreScreenService, FlyHttpResponseListener listener) {
        this.requestData = requestData;
        this.postHighscoreService = postHighscoreScreenService;
        this.listener = listener;
    }
    
    @Override
    public void successful(Object obj) {
        PostUserService.UserRecord record = (PostUserService.UserRecord) obj;
        PlayerProfileManager profileManager = PlayerProfileManager.getInstance();
		PlayerProfile profile = profileManager.getCurrentPlayerProfile();

        profile.setFlyID(record.id);
        profile.setSecretKey(record.secretKey);
        profileManager.saveFlyID(profile);
		profileManager.saveSecretKey(profile);
        requestData.FlyID = record.id;
        postHighscoreService.execute();
    }
    
    @Override
    public void failed(String msg) {
        listener.failed(msg);
    }
    
    @Override
    public void cancelled() {
        listener.cancelled();
    }
}
