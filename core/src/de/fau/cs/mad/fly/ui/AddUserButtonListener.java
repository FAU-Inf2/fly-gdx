package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

public class AddUserButtonListener extends ChangeListener {
    
    private Skin skin;
    private TextField newUserField;
    private StatisticsScreen statisticsScreen;
    
    public AddUserButtonListener(Skin skin, TextField newUserField, StatisticsScreen statisticsScreen) {
        this.skin = skin;
        this.newUserField = newUserField;
        this.statisticsScreen = statisticsScreen;
    }
    
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        String name = newUserField.getText();
        Stage stage = event.getStage();
        
        if (!"".equals(name)) {
            for (PlayerProfile playerProfile : PlayerProfileManager.getInstance().getAllPlayerProfiles()) {
                if (playerProfile.getName().equals(name)) {
                    new Dialog("", skin) {
                        {
                            text(I18n.t("UserExists"));
                            button(I18n.t("ok"));
                        }
                    }.show(stage);
                    return;
                }
            }
            PlayerProfile playerProfile = new PlayerProfile();
            playerProfile.setName(name);
            
            PlayerProfileManager.getInstance().savePlayer(playerProfile);
            statisticsScreen.updateUserTable();
            new Dialog("", skin) {
                {
                    text(I18n.t("UserAdded"));
                    button(I18n.t("ok"));
                }
            }.show(stage);
        } else {
            new Dialog("", skin) {
                {
                    text(I18n.t("NullUserName"));
                    button("OK");
                }
            }.show(stage);
        }
    }
    
}
