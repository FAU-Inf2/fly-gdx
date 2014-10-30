package de.fau.cs.mad.fly.HttpClient;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.ui.DialogWithOneButton;
import de.fau.cs.mad.fly.ui.screens.LevelGroupHighscoreScreen.UploadScoreClickListener;

/**
 * Service to upload a highscore to the server;
 * 
 * @author Fan, Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class PostScoreHttpRespListener implements FlyHttpResponseListener {
    
    /** {@link Button} to deactivate when upload was successful */
    private Button uploadButton;
    private PostHighscoreService.RequestData requestData;
    private UploadScoreClickListener uploadListener;
    
    /**
     * Creates a new {@link PostScoreHttpRespListener}, that listens to response
     * of the upload of a score ({@link #requestData}) to the server.
     * <p>
     * If the data is successfully uploaded, the {@link #uploadButton} is
     * disabled, to prevent uploading a score twice. Furthermore a
     * {@link DialogWithOneButton} is shown that displays weather the uploading
     * was successful or not. Therefore the {@link #stageToShowMessage} is
     * necessary.
     * 
     * @param requestData
     * @param uploadButton
     * @param stageToShowMessage
     */
    public PostScoreHttpRespListener(PostHighscoreService.RequestData requestData, Button uploadButton, UploadScoreClickListener uploadListener) {
        this.requestData = requestData;
        this.uploadButton = uploadButton;
        this.uploadListener = uploadListener;
    }
    
    @Override
    public synchronized void successful(Object obj) {
        uploadButton.setDisabled(true);
        
        requestData.Score.setIsUploaded(true);
        int playerProfileId = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getId();
        ScoreManager.getInstance().updateIsUploaded(requestData.Score, playerProfileId, requestData.LevelGroupID, requestData.LevelID);
        if (requestData.Score.getServerScoreId() <= 0) {
            PostHighscoreService.ResponseData response = (PostHighscoreService.ResponseData) obj;
            if (response != null) {
                requestData.Score.setServerScoreId(response.scoreID);
            }
            ScoreManager.getInstance().updateServerScoreId(requestData.Score, playerProfileId, requestData.LevelGroupID, requestData.LevelID);
        }
        
        uploadListener.uploadSuccessfull();
    }
    
    @Override
    public synchronized void failed(String msg) {
        uploadButton.setDisabled(false);
        uploadListener.uploadFailed();
    }
    
    @Override
    public void cancelled() {
        // currently it is not possible to cancel the upload process
    }
};