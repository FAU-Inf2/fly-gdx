package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.sql.DatabaseCursor;
import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.res.Level;

/**
 * Manage scores of all player. 
 * todo: save and get scores from Database
 *   
 * @ Qufang Fan
 */
public class ScoreManager {

	private ScoreManager() {
	}

	private static ScoreManager Instance = new ScoreManager();

	public static ScoreManager getInstance() {
		return Instance;
	}

	public void saveBestScore(PlayerProfile playerProfile, LevelProfile level, Score score) {
		Score compareScore = getLevelBestScore(playerProfile, level);
		if (compareScore != null && compareScore.getTotalScore() > score.getTotalScore())
			return;

		String deleteDetail = "delete from " + FlyDBManager.TABLE_SCORE_DETAIL + " where "
				+ FlyDBManager.SCORE_DETAIL_COLUMN_PLAYERID + "=" + playerProfile.getId() + " and "
				+ FlyDBManager.SCORE_DETAIL_COLUMN_LEVELID + "=" + level.id;

		String deleteScore = "delete from " + FlyDBManager.TABLE_SCORE + " where "
				+ FlyDBManager.SCORE_COLUMN_PLAYERID + "=" + playerProfile.getId() + " and "
				+ FlyDBManager.SCORE_COLUMN_LEVELID + "=" + level.id;

		String insertScore = "insert into " + FlyDBManager.TABLE_SCORE + "("
				+ FlyDBManager.SCORE_COLUMN_PLAYERID + ", " + FlyDBManager.SCORE_COLUMN_LEVELID
				+ ", " + FlyDBManager.SCORE_COLUMN_SCORE + ", "
				+ FlyDBManager.SCORE_COLUMN_COMPARESCORE
				// todo+ ", " + FlyDBManager.SCORE_COLUMN_REACHEDDATE
				+ ") values (" + playerProfile.getId() + ", " + level.id + ", " + score.getTotalScore()
				+ ", '" + score.getCompareScore() + "') ";

		// FlyDBManager.getInstance().openDatabase();
		FlyDBManager.getInstance().execSQL(deleteDetail);
		FlyDBManager.getInstance().execSQL(deleteScore);
		FlyDBManager.getInstance().execSQL(insertScore);
		for (ScoreDetail detail : score.getScoreDetails()) {
			String insertDetail = "insert into " + FlyDBManager.TABLE_SCORE_DETAIL + "("
					+ FlyDBManager.SCORE_DETAIL_COLUMN_PLAYERID + ", "
					+ FlyDBManager.SCORE_DETAIL_COLUMN_LEVELID + ", "
					+ FlyDBManager.SCORE_DETAIL_COLUMN_DETAIL + ", "
					+ FlyDBManager.SCORE_DETAIL_COLUMN_VALUE + ") values (" + playerProfile.getId() + ", "
					+ level.id + ", '" + detail.getDetailName() + "', '" + detail.getValue() + "')";
			FlyDBManager.getInstance().execSQL(insertDetail);
		}

	}

	public void saveBestScore(PlayerProfile playerProfile, Score score) {
		saveBestScore(playerProfile, playerProfile.getLevel().head, score);
	}

	public void saveBestScore(Score score) {
		saveBestScore(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), PlayerProfileManager.getInstance()
				.getCurrentPlayerProfile().getLevel().head, score);
	}

	public Map<String, Score> getPlayerBestScores(PlayerProfile playerProfile) {
		String selectScore = "select " + FlyDBManager.SCORE_COLUMN_LEVELID + ", "
				+ FlyDBManager.SCORE_COLUMN_SCORE + ", " + FlyDBManager.SCORE_COLUMN_COMPARESCORE
				+ ", " + FlyDBManager.SCORE_COLUMN_REACHEDDATE + " from "
				+ FlyDBManager.TABLE_SCORE + " where " + FlyDBManager.SCORE_COLUMN_PLAYERID + " ="
				+ playerProfile.getId();
		Map<String, Score> map = new HashMap<String, Score>();

		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectScore);
		if (cursor != null && cursor.getCount() > 0) {

			while (cursor.next()) {
				int levelId = cursor.getInt(0);
				Score score = new Score();
				score.setTotalScore(cursor.getInt(1));
				score.setCompareScore(cursor.getString(2));
				// todo score.setReachedDate(cursor.getString(3));

				String selectDetail = "select " + FlyDBManager.SCORE_DETAIL_COLUMN_DETAIL + ", "
						+ FlyDBManager.SCORE_DETAIL_COLUMN_VALUE + " from "
						+ FlyDBManager.TABLE_SCORE_DETAIL + " where "
						+ FlyDBManager.SCORE_DETAIL_COLUMN_PLAYERID + "=" + playerProfile.getId()
						+ " and " + FlyDBManager.SCORE_DETAIL_COLUMN_LEVELID + "=" + levelId;

				List<ScoreDetail> details = new ArrayList<ScoreDetail>();
				DatabaseCursor subCursor = FlyDBManager.getInstance().selectData(selectDetail);
				if (subCursor != null && subCursor.getCount() > 0) {
					while (subCursor.next()) {
						ScoreDetail detail = new ScoreDetail();
						detail.setDetailName(subCursor.getString(0));
						detail.setValue(subCursor.getString(1));
						details.add(detail);
					}
					subCursor.close();
				}
				score.setScoreDetails(details);
				map.put(levelId + "", score);
			}
			cursor.close();
		}

		return map;
	}

	public Map<String, Score> getcurrentBestScores() {
		return getPlayerBestScores(PlayerProfileManager.getInstance().getCurrentPlayerProfile());
	}

	public Score getLevelBestScore(PlayerProfile playerProfile, LevelProfile level) {
		String selectScore = "select " + FlyDBManager.SCORE_COLUMN_SCORE + ", "
				+ FlyDBManager.SCORE_COLUMN_COMPARESCORE + ", "
				+ FlyDBManager.SCORE_COLUMN_REACHEDDATE + " from " + FlyDBManager.TABLE_SCORE
				+ " where " + FlyDBManager.SCORE_COLUMN_PLAYERID + " =" + playerProfile.getId() + " and "
				+ FlyDBManager.SCORE_COLUMN_LEVELID + "=" + level.id;

		String selectDetail = "select " + FlyDBManager.SCORE_DETAIL_COLUMN_DETAIL + ", "
				+ FlyDBManager.SCORE_DETAIL_COLUMN_VALUE + " from "
				+ FlyDBManager.TABLE_SCORE_DETAIL + " where "
				+ FlyDBManager.SCORE_DETAIL_COLUMN_PLAYERID + "=" + playerProfile.getId() + " and "
				+ FlyDBManager.SCORE_DETAIL_COLUMN_LEVELID + "=" + level.id;
		Score score = null;

		DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectScore);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.next();
			score = new Score();
			score.setTotalScore(cursor.getInt(0));
			score.setCompareScore(cursor.getString(1));
			// todo score.setReachedDate(cursor.getString(2));
			cursor.close();
			List<ScoreDetail> details = new ArrayList<ScoreDetail>();
			DatabaseCursor subCursor = FlyDBManager.getInstance().selectData(selectDetail);

			if (subCursor != null && subCursor.getCount() > 0) {
				while (subCursor.next()) {
					ScoreDetail detail = new ScoreDetail();
					detail.setDetailName(subCursor.getString(0));
					detail.setValue(subCursor.getString(1));
					details.add(detail);
				}
				subCursor.close();
			}
			score.setScoreDetails(details);
			
		}

		return score;
	}

	public Score getLevelBestScore(PlayerProfile playerProfile) {
		return getLevelBestScore(playerProfile, playerProfile.getLevel().head);
	}

	public Score getCurrentLevelBestScore() {
		return getLevelBestScore(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), PlayerProfileManager
				.getInstance().getCurrentPlayerProfile().getLevel().head);
	}
}
