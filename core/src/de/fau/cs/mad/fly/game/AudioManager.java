package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tschaei on 29.09.14.
 */
public class AudioManager implements Disposable{

    public static enum Sounds { CRASH, GATE_PASSED, PICKUP };
    private final String CRASH_SOUND = "sounds/crash.wav";
    private final String GATE_PASSED_SOUND = "sounds/gatepassed.wav";
    private final String PICKUP = "sounds/pickup.wav";
    private Map<Sounds, Sound> soundMap;
    public AudioManager() {
        soundMap = new HashMap<Sounds, Sound>();
        soundMap.put(Sounds.CRASH, Gdx.audio.newSound(Gdx.files.internal(CRASH_SOUND)));
        soundMap.put(Sounds.GATE_PASSED, Gdx.audio.newSound(Gdx.files.internal(GATE_PASSED_SOUND)));
        soundMap.put(Sounds.PICKUP, Gdx.audio.newSound(Gdx.files.internal(PICKUP)));
    }

    public void playSound(Sounds sound) {
        soundMap.get(sound).play();
    }

    @Override
    public void dispose() {
        for(Map.Entry<Sounds, Sound> entry : soundMap.entrySet()) {
            entry.getValue().dispose();
        }
    }

}
