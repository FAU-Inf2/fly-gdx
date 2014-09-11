package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Score
 * 
 * @author Qufang Fan
 */
public class Score {

	private int totalScore;
	private String compareScore;

	private int id;
	private int playTimes;
	private Date reachedDate;
	
	private boolean isUploaded;
	private int serverScoreId = -1;

	/**
	 * @return the serverScoreId
	 */
	public int getServerScoreId() {
		return serverScoreId;
	}

	/**
	 * @param serverScoreId the serverScoreId to set
	 */
	public void setServerScoreId(int serverScoreId) {
		this.serverScoreId = serverScoreId;
	}

	/**
	 * @return the isUploaded
	 */
	public boolean getIsUploaded() {
		return isUploaded;
	}

	/**
	 * @param isUploaded the isUploaded to set
	 */
	public void setIsUploaded(boolean isUploaded) {
		this.isUploaded = isUploaded;
	}

	private List<ScoreDetail> scoreDetails = new ArrayList<ScoreDetail>();

	public Score() {
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public String getCompareScore() {
		return compareScore;
	}

	public void setCompareScore(String compareScore) {
		this.compareScore = compareScore;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(int playTimes) {
		this.playTimes = playTimes;
	}

	public Date getReachedDate() {
		return reachedDate;
	}

	public void setReachedDate(Date reachedDate) {
		this.reachedDate = reachedDate;
	}

	public List<ScoreDetail> getScoreDetails() {
		return scoreDetails;
	}

	public void setScoreDetails(List<ScoreDetail> scoreDetails) {
		this.scoreDetails = scoreDetails;
	}
}
