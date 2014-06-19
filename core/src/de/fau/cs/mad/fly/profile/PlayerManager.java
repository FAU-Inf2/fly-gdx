package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.List;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.settings.AppSettingsManager;

/*
 * Manage players.
 * 
 * @author Qufang Fan
 */
public class PlayerManager {
	private Player currentPlayer;

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	private int getMaxPlayerID()
	{
		int id = 0;
		for(Player player: getAllPlayer())
		{
			if(player.getId()>id)
				id = player.getId();
		}
		return id;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/*
	 * init the default player, create the default player when there is no player exist.
	 */
	private PlayerManager() {
		int userID = AppSettingsManager.Instance.getIntegerSetting(AppSettingsManager.CHOSEN_USER, 0);
		Player player = getPlayerfromDB( userID );
		if( player == null )
		{
			player = new Player();
			player.setName(I18n.t("default.username"));
			
			player.setId(getMaxPlayerID() + 1);
			
			savePlayer( player);
			
		}
		setCurrentPlayer( player );
		AppSettingsManager.Instance.setIntegerSetting(AppSettingsManager.CHOSEN_USER, player.getId());
	}
	
	public static PlayerManager Instance = new PlayerManager();
	
	
	public Player getPlayerfromDB( int userID)
	{
		Player player = new Player();
		player.setId(userID);
		player.setName("default");
		return player;
	}
	
	public List<Player> getAllPlayer()
	{
		return new ArrayList<Player>();
	}
	
	public void savePlayer(Player player)
	{
		
	}
	
	
}
