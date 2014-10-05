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

    public static enum Sounds { CRASH, GATE_PASSED, PICKUP, TRIPLE, SONIC, OMFG, CAMERA, HITMARKER, SADVIOLIN, OH_YEAH, DAMN_SON };
    private final String CRASH_SOUND = "sounds/crash.wav";
    private final String GATE_PASSED_SOUND = "sounds/gatepassed.wav";
    private final String PICKUP = "sounds/pickup.wav";
    private final String TRIPLE = "sounds/triple.wav";
    private final String OH_YEAH = "sounds/ohyeah.wav";
    private final String SONIC = "sounds/sonictheme.wav";
    private final String OMFG = "sounds/omfg.wav";
    private final String CAMERA = "sounds/camera.wav";
    private final String HITMARKER = "sounds/hitmarker.wav";
    private final String SADVIOLIN = "sounds/sadviolin.wav";
    private final String DAMN_SON = "sounds/damn_son_full.mp3";
    private Map<Sounds, Sound> soundMap;
    public AudioManager() {
        soundMap = new HashMap<Sounds, Sound>();
        soundMap.put(Sounds.CRASH, Gdx.audio.newSound(Gdx.files.internal(CRASH_SOUND)));
        soundMap.put(Sounds.GATE_PASSED, Gdx.audio.newSound(Gdx.files.internal(GATE_PASSED_SOUND)));
        soundMap.put(Sounds.PICKUP, Gdx.audio.newSound(Gdx.files.internal(PICKUP)));
        soundMap.put(Sounds.TRIPLE, Gdx.audio.newSound(Gdx.files.internal(TRIPLE)));
        soundMap.put(Sounds.SONIC, Gdx.audio.newSound(Gdx.files.internal(SONIC)));
        soundMap.put(Sounds.OMFG, Gdx.audio.newSound(Gdx.files.internal(OMFG)));
        soundMap.put(Sounds.CAMERA, Gdx.audio.newSound(Gdx.files.internal(CAMERA)));
        soundMap.put(Sounds.HITMARKER, Gdx.audio.newSound(Gdx.files.internal(HITMARKER)));
        soundMap.put(Sounds.SADVIOLIN, Gdx.audio.newSound(Gdx.files.internal(SADVIOLIN)));
        soundMap.put(Sounds.OH_YEAH, Gdx.audio.newSound(Gdx.files.internal(OH_YEAH)));
        soundMap.put(Sounds.DAMN_SON, Gdx.audio.newSound(Gdx.files.internal(DAMN_SON)));
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
