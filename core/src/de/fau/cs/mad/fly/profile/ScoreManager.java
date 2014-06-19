package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.res.Level;

/*
 * Manage scores of all player. 
 * todo: save and get scores from Database
 *   
 * @ Qufang Fan
 */
public class ScoreManager {

	private ScoreManager() {
		
	}
	
	private Map<String, Score> currentPlayerScores = new HashMap<String, Score>();
	
	public static ScoreManager Instance = new ScoreManager();
	
	public void saveBestScore(Player player, Level.Head level, Score score)
	{
		
	}
	
	public void saveBestScore(Player player, Score score)
	{
	
	}
	
	public void saveBestScore( Score score)
	{
		String level = PlayerManager.Instance.getCurrentPlayer().getLevel().head.name;
		if( currentPlayerScores.containsKey(level) )
		{
			if( currentPlayerScores.get(level).getTotalScore() < score.getTotalScore())
				currentPlayerScores.remove(level);
			else
				return;
		}
		currentPlayerScores.put(level, score);
	}
		
	public Collection<Score> getPlayerBestScores( Player player)
	{
		return currentPlayerScores.values();
	}
	
	public Score getLevelBestScore( Player player, Level.Head level)
	{
		return currentPlayerScores.get(level.name);
	}
	
	public  Collection<Score> getcurrentBestScores()
	{
		return currentPlayerScores.values();
	}

	public Score getCurrentBestScore( )
	{
		return currentPlayerScores.get(0);
	}
}
