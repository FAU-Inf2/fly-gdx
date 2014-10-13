package de.fau.cs.mad.fly.HttpClient;

import java.util.ArrayList;
import java.util.List;

public class LevelGroupGlobalHighscores {
    
    public List<LevelRecords> records = new ArrayList<LevelRecords>();
    
    public void addRecord(int levelId, String levelName, RecordItem record) {
        for (LevelRecords levelRecords : records) {
            if (levelRecords.levelID == levelId) {
                levelRecords.records.add(record);
                return;
            }
        }
        
        LevelRecords levelRecords = new LevelRecords();
        levelRecords.levelID = levelId;
        levelRecords.levelName = levelName;
        levelRecords.records.add(record);
        records.add(levelRecords);
    }
}
