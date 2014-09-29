package de.fau.cs.mad.fly.communication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.PostHighscoreService;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.ui.DialogWithOneButton;

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
    private Stage stageToShowMessage;
    
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
    public PostScoreHttpRespListener(PostHighscoreService.RequestData requestData, Button uploadButton, Stage stageToShowMessage) {
        this.requestData = requestData;
        this.uploadButton = uploadButton;
        this.stageToShowMessage = stageToShowMessage;
    }
    
    @Override
    public void successful(Object obj) {
        uploadButton.setDisabled(true);
        
        Gdx.app.log("PostScoreHttpRespListener", ".successful: levelID: " + requestData.LevelID + ", group: " + requestData.LevelgroupID + ", id: " + requestData.Score.getServerScoreId());
        
        requestData.Score.setIsUploaded(true);
        int playerProfileId = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getId();
        ScoreManager.getInstance().updateIsUploaded(requestData.Score, playerProfileId, requestData.LevelgroupID, requestData.LevelID);
        if (requestData.Score.getServerScoreId() <= 0) {
            PostHighscoreService.ResponseData response = (PostHighscoreService.ResponseData) obj;
            if (response != null) {
                requestData.Score.setServerScoreId(response.scoreID);
            }
            ScoreManager.getInstance().updateServerScoreId(requestData.Score, playerProfileId, requestData.LevelgroupID, requestData.LevelID);
        }
        
        // show dialog with message for user
        Dialog uploadSuccessfullMessage = new DialogWithOneButton(I18n.t("ScoreUploaded"), I18n.t("ok"));
        uploadSuccessfullMessage.show(stageToShowMessage);
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
        // currently it is not possible to cancel the upload process
    }
};