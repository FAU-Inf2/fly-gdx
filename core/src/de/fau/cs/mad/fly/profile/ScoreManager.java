package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.sql.DatabaseCursor;
import de.fau.cs.mad.fly.db.FlyDBManager;

/**
 * Manage scores of all player. todo: save and get scores from Database
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
        int levelgroupID = playerProfile.getCurrentLevelGroup().id;
        
        // String deleteDetail = "delete from score_detail where player_id=" +
        // playerProfile.getId() + " and level_id=" + level.id +
        // " and level_group_id=" + levelgroupID;
        
        String deleteScore = "delete from score where player_id=" + playerProfile.getId() + " and level_id=" + level.id + " and level_group_id=" + levelgroupID;
        
        String insertScore = "insert into score(player_id, level_group_id, level_id, score, compare_score,is_uploaded,server_score_id) values ("
        // todo+ ", " + FlyDBManager.SCORE_COLUMN_REACHEDDATE
                + playerProfile.getId() + ", " + levelgroupID + ", " + level.id + "," + score.getTotalScore() + ", '" + score.getCompareScore() + "'," + (score.getIsUploaded() ? "1" : "0") + "," + score.getServerScoreId() + ") ";
        
        // FlyDBManager.getInstance().openDatabase();
        // FlyDBManager.getInstance().execSQL(deleteDetail);
        FlyDBManager.getInstance().execSQL(deleteScore);
        FlyDBManager.getInstance().execSQL(insertScore);
        // for (ScoreDetail detail : score.getScoreDetails()) {
        // String insertDetail =
        // "insert into score_detail(player_id,level_id,score_detail,_value,level_group_id) values ("
        // + playerProfile.getId() + ", "
        // + level.id + ", '" + detail.getDetailName() + "', '"
        // + detail.getValue() + "'," +levelgroupID + ")";
        // FlyDBManager.getInstance().execSQL(insertDetail);
        // }
        
    }
    
    public void saveBestScore(PlayerProfile playerProfile, Score score) {
        saveBestScore(playerProfile, playerProfile.getCurrentLevelProfile(), score);
    }
    
    public void saveBestScore(Score score) {
        saveBestScore(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile(), score);
    }
    
    /**
     * return the best scores of all levels in one level group
     * 
     * @param playerProfile
     * @return
     */
    public Map<String, Score> getPlayerBestScores(PlayerProfile playerProfile, LevelGroup levelGroup) {
        String selectScore = "select level_id, score, compare_score, reached_date,is_uploaded,server_score_id from score where player_id =" + playerProfile.getId() + " and level_group_id=" + levelGroup.id;
        Map<String, Score> map = new HashMap<String, Score>();
        
        DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectScore);
        if (cursor != null && cursor.getCount() > 0) {
            
            while (cursor.next()) {
                int levelId = cursor.getInt(0);
                Score score = new Score();
                score.setTotalScore(cursor.getInt(1));
                score.setCompareScore(cursor.getString(2));
                // todo score.setReachedDate(cursor.getString(3));
                score.setIsUploaded(cursor.getInt(4) > 0);
                score.setServerScoreId(cursor.getInt(5));
                
                // String selectDetail =
                // "select score_detail,_value from score_detail where player_id="
                // + playerProfile.getId()
                // + " and level_id=" + levelId
                // + " and level_group_id=" + levelGroup.id;
                //
                // List<ScoreDetail> details = new ArrayList<ScoreDetail>();
                // DatabaseCursor subCursor =
                // FlyDBManager.getInstance().selectData(selectDetail);
                // if (subCursor != null && subCursor.getCount() > 0) {
                // while (subCursor.next()) {
                // ScoreDetail detail = new ScoreDetail();
                // detail.setDetailName(subCursor.getString(0));
                // detail.setValue(subCursor.getString(1));
                // details.add(detail);
                // }
                // subCursor.close();
                // }
                // score.setScoreDetails(details);
                map.put(levelId + "", score);
            }
            cursor.close();
        }
        
        return map;
    }
    
    public Map<String, Score> getPlayerBestScores(PlayerProfile playerProfile) {
        return getPlayerBestScores(playerProfile, playerProfile.getCurrentLevelGroup());
    }
    
    public Map<String, Score> getcurrentBestScores() {
        return getPlayerBestScores(PlayerProfileManager.getInstance().getCurrentPlayerProfile());
    }
    
    public Score getLevelBestScore(PlayerProfile playerProfile, LevelProfile level) {
        int levelgroupID = playerProfile.getCurrentLevelGroup().id;
        String selectScore = "select score, compare_score, reached_date, is_uploaded, server_score_id from score where player_id =" + playerProfile.getId() + " and level_id=" + level.id + " and level_group_id=" + levelgroupID;
        
        // String selectDetail =
        // "select score_detail,_value from score_detail where player_id=" +
        // playerProfile.getId() + " and level_id=" + level.id +
        // " and level_group_id=" + levelgroupID;
        Score score = null;
        
        DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectScore);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.next();
            score = new Score();
            score.setTotalScore(cursor.getInt(0));
            score.setCompareScore(cursor.getString(1));
            // todo score.setReachedDate(cursor.getString(2));
            score.setIsUploaded(cursor.getInt(3) > 0);
            score.setServerScoreId(cursor.getInt(4));
            cursor.close();
            // List<ScoreDetail> details = new ArrayList<ScoreDetail>();
            // DatabaseCursor subCursor =
            // FlyDBManager.getInstance().selectData(selectDetail);
            //
            // if (subCursor != null && subCursor.getCount() > 0) {
            // while (subCursor.next()) {
            // ScoreDetail detail = new ScoreDetail();
            // detail.setDetailName(subCursor.getString(0));
            // detail.setValue(subCursor.getString(1));
            // details.add(detail);
            // }
            // subCursor.close();
            // }
            // score.setScoreDetails(details);
            
        }
        
        return score;
    }
    
    public Score getLevelBestScore(PlayerProfile playerProfile) {
        return getLevelBestScore(playerProfile, playerProfile.getCurrentLevelProfile());
    }
    
    public Score getCurrentLevelBestScore() {
        return getLevelBestScore(PlayerProfileManager.getInstance().getCurrentPlayerProfile(), PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile());
    }
    
    public void updateIsUploaded(Score score, int playerId, int groupId, int levelId) {
        String sql = "update score set is_uploaded = " + (score.getIsUploaded() ? "1" : "0") + ",server_score_id = " + score.getScoreDetails() + " where player_id=" + playerId + " and level_id=" + levelId + " and level_group_id=" + groupId;
        FlyDBManager.getInstance().execSQL(sql);
        
    }
    
    public void updateServerScoreId(Score score, int playerId, int groupId, int levelId) {
        String sql = "update score set server_score_id = " + score.getScoreDetails() + " where player_id=" + playerId + " and level_id=" + levelId + " and level_group_id=" + groupId;
        FlyDBManager.getInstance().execSQL(sql);
    }
}
