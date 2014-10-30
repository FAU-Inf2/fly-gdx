package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.fau.cs.mad.fly.profile.Score;

public class LevelScoreEntry {
    private int levelGroupId;
    private int levelId;
    private Score score;
    private Button button;
    
    public LevelScoreEntry(int levelGroup, int levelId, Score score, TextButton button) {
        this.levelGroupId = levelGroup;
        this.levelId = levelId;
        this.score = score;
        this.button = button;
    }
    
    public int getLevelGroupId() {
        return levelGroupId;
    }

    public int getLevelId() {
        return levelId;
    }

    public Score getScore() {
        return score;
    }

    public Button getButton() {
        return button;
    }
}
