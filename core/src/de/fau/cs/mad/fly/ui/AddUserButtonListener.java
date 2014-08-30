package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

/**
 * Listener that is called when a new user is created.
 * <p>
 * It needs connection to the {@link TextField} {@link #newUserField} to get the
 * name of the new user. Furthermore, it has to update the list of all users
 * that is managed in the {@link StatisticsScreen}
 * 
 * @author Lukas Hahmann
 * 
 */
public class AddUserButtonListener extends ChangeListener {
    
    /** {@link Skin} to get the style of the {@link Dialog}s. */
    private Skin skin;
    /** {@link TextField} to get the name of the new user. */
    private TextField newUserField;
    /** {@link StatisticsScreen} that contains the list of new users. */
    private StatisticsScreen statisticsScreen;
    
    public AddUserButtonListener(Skin skin, TextField newUserField, StatisticsScreen statisticsScreen) {
        this.skin = skin;
        this.newUserField = newUserField;
        this.statisticsScreen = statisticsScreen;
    }
    
    /**
     * Method that is called when the button is pressed to create a new user.
     * <p>
     * Before a new user can be created, two things have to be checked:
     * <p>
     * 1) Does the name contains any characters? - Otherwise display a
     * corresponding {@link Dialog}.
     * <p>
     * 2) Does the player already exists? - If so, display a corresponding
     * {@link Dialog}.
     * <p>
     * Other checks are done in {@link UserNameTextFieldFilter}
     * <p>
     * If a new player is created, display a corresponding {@link Dialog}.
     */
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        String name = newUserField.getText();
        Stage stage = event.getStage();
        Dialog dialog = new Dialog("", skin, "dialog");
        boolean userExists = false;
        
        if (!"".equals(name)) {
            List<PlayerProfile> allPlayerProfiles = PlayerProfileManager.getInstance().getAllPlayerProfiles();
            int numberOfAllPlayerProfiles = allPlayerProfiles.size();
            PlayerProfile playerProfile;
            for (int i = 0; i < numberOfAllPlayerProfiles; i++) {
                playerProfile = allPlayerProfiles.get(i);
                if (playerProfile.getName().equals(name)) {
                    userExists = true;
                    i = numberOfAllPlayerProfiles;
                }
            }
            if (userExists) {
                dialog.text(I18n.t("UserExists"));
            } else {
                // update player profile
                playerProfile = new PlayerProfile();
                playerProfile.setName(name);
                PlayerProfileManager.getInstance().savePlayer(playerProfile);
                statisticsScreen.updateUserTable();
                // reset user input field
                newUserField.setText("");
                newUserField.setMessageText(I18n.t("TipsUserName"));
                // set dialog text
                dialog.text(I18n.t("UserAdded"));
            }
        } else {
            dialog.text(I18n.t("NullUserName"));
        }
        TextButton button = new TextButton(I18n.t("ok"), skin, "rounded");
        dialog.button(button);
        dialog.show(stage);
    }
    
}
