package de.fau.cs.mad.fly.game;

import java.util.Date;

import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.res.EventAdapter;
import de.fau.cs.mad.fly.res.Gate;

/**
 * Manages the score in the level while the level is playing.
 * 
 * @author Tobi
 *
 */
public class ScoreController extends EventAdapter {
	/**
	 * The total score.
	 */
	private int totalScore = 0;
	
	/**
	 * The score from the passed gates.
	 */
	private int gatePassedScore = 0;
	
	/**
	 * The score from collected bonus points.
	 */
	private int bonusPoints = 0;

	/**
	 * Creates a new score controller.
	 */
	public ScoreController() {	
	}
	
	/**
	 * Returns the end score with score details.
	 * 
	 * @param gameController		The game controller.
	 * @return the score of the player with score details.
	 */
    public Score getEndScore(GameController gameController) {
        if (gameController.getLevel().isGameOver()) {
            Score newScore = new Score();
            newScore.setReachedDate(new Date());
            
            int score = gatePassedScore;
            /*for (Gate gate : gateCircuit.allGates()) {
                score += gate.score * gate.passedTimes;
            }*/
            newScore.getScoreDetails().add(new ScoreDetail(("gates"), score + ""));
            
            int leftTimeScore = gameController.getTimeController().getIntegerTime() * 20;
            newScore.getScoreDetails().add(new ScoreDetail(("leftTime"), leftTimeScore + ""));
            score += leftTimeScore;
            
            /*int leftCollisionTimeScore = leftCollisionTime * 30;
            newScore.getScoreDetails().add(new ScoreDetail(("leftCollisionTime"), leftCollisionTimeScore + ""));
            score += leftCollisionTimeScore;*/

            newScore.getScoreDetails().add(new ScoreDetail(("bonusPoints"), bonusPoints + ""));
            score += bonusPoints;

            newScore.setTotalScore(score);
            return newScore;
        } else {
            return new Score();// todo
        }
    }
    
    /**
     * Adds or subtracts bonus points.
     * @param bonusPoints		The bonus points to add.
     */
    public void addBonusPoints(int bonusPoints) {
    	this.bonusPoints += bonusPoints;
    	this.totalScore += bonusPoints;
    }
    
    /**
     * Returns the current total score.
     * @return totalScore
     */
    public int getTotalScore() {
    	return totalScore;
    }
    
    @Override
    public void onGatePassed(Gate gate) {
    	this.gatePassedScore += gate.score;
    	this.totalScore += gate.score;
    }
}