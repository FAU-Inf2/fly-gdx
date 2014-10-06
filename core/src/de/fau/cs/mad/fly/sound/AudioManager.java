package de.fau.cs.mad.fly.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Created by tschaei on 29.09.14.
 */
public class AudioManager implements Disposable {

    public static enum Sounds { CRASH, GATE_PASSED, PICKUP, TRIPLE, OMFG, CAMERA, HITMARKER, OH_YEAH, SONIC, SAD_VIOLIN, DAMN_SON };

    private final String CRASH_SOUND = "sounds/crash.wav";
    private final String GATE_PASSED_SOUND = "sounds/gatepassed.wav";
    private final String PICKUP = "sounds/pickup.wav";
    private final String TRIPLE = "sounds/triple.wav";
    private final String OH_YEAH = "sounds/ohyeah.wav";
    private final String CAMERA = "sounds/camera.wav";
    private final String HITMARKER = "sounds/hitmarker.wav";
    private final String DAMN_SON = "sounds/damn_son.mp3";
    private final String SONIC = "sounds/sonic.mp3";
    private final String SAD_VIOLIN = "sounds/sadviolin.mp3";

    private Map<Sounds, Playable> soundMap = new HashMap<Sounds, Playable>();

    public AudioManager() {
        soundMap.put(Sounds.CRASH, create(CRASH_SOUND));
        soundMap.put(Sounds.GATE_PASSED, create(GATE_PASSED_SOUND));
        soundMap.put(Sounds.PICKUP, create(PICKUP));
        soundMap.put(Sounds.TRIPLE, create(TRIPLE));
        soundMap.put(Sounds.CAMERA, create(CAMERA));
        soundMap.put(Sounds.HITMARKER, create(HITMARKER));
        soundMap.put(Sounds.OH_YEAH, create(OH_YEAH));
        soundMap.put(Sounds.DAMN_SON, create(DAMN_SON, Types.Music));
        soundMap.put(Sounds.SONIC, create(SONIC, Types.Music));
        soundMap.put(Sounds.SAD_VIOLIN, create(SAD_VIOLIN, Types.Music));
    }

    public Playable get(Sounds sound) {
        return soundMap.get(sound);
    }

    public Playback play(Sounds sound) {
        return get(sound).play();
    }

    public Playback playSound(Sounds sound) {
        return play(sound);
    }

    public Collection<Playable> allSounds() {
        return soundMap.values();
    }

    @Override
    public void dispose() {
        for(Map.Entry<Sounds, Playable> entry : soundMap.entrySet())
            entry.getValue().dispose();
    }

    private static enum Types { Sound, Music };

    private Playable create(FileHandle fileHandle, Types type) {
        switch( type ) {
            case Music:
                return new MusicPlayable(Gdx.audio.newMusic(fileHandle));
            default:
                return new SoundPlayable(Gdx.audio.newSound(fileHandle));
        }
    }

    private Playable create(String file, Types type) {
        return create(Gdx.files.internal(file), type);
    }

    private Playable create(String file) {
        return create(file, Types.Sound);
    }
}
