package de.fau.cs.mad.fly.HttpClient;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import de.fau.cs.mad.fly.I18n;
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
public class PutUserHttpRespListener implements FlyHttpResponseListener {
    
    private final PlayerProfile player;
 
    
    public PutUserHttpRespListener(PlayerProfile player) {
        this.player = player;

    }
    
    @Override
    public void successful(Object obj) {
    	PlayerProfileManager profileManager = PlayerProfileManager.getInstance();		
		profileManager.updateIntColumn(player, "is_newname_uploaded", 1);	
		player.setNewnameUploaded(true);
    }
    
    @Override
    public void failed(String msg) {
       
    }
    
    @Override
    public void cancelled() {
    }
}
