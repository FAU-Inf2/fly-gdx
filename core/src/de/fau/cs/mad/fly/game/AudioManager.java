package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Created by tschaei on 29.09.14.
 */
public class AudioManager implements Disposable {

    public static enum Sounds { CRASH, GATE_PASSED, PICKUP, TRIPLE, OMFG, CAMERA, HITMARKER, OH_YEAH };
    public static enum Musics { SONIC, SAD_VIOLIN, DAMN_SON };
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

    private Map<Sounds, Sound> soundMap;
    private Map<Musics, Music> musicMap;

    public AudioManager() {
        soundMap = new HashMap<Sounds, Sound>();
        musicMap = new HashMap<Musics, Music>();
        soundMap.put(Sounds.CRASH, Gdx.audio.newSound(Gdx.files.internal(CRASH_SOUND)));
        soundMap.put(Sounds.GATE_PASSED, Gdx.audio.newSound(Gdx.files.internal(GATE_PASSED_SOUND)));
        soundMap.put(Sounds.PICKUP, Gdx.audio.newSound(Gdx.files.internal(PICKUP)));
        soundMap.put(Sounds.TRIPLE, Gdx.audio.newSound(Gdx.files.internal(TRIPLE)));
        soundMap.put(Sounds.CAMERA, Gdx.audio.newSound(Gdx.files.internal(CAMERA)));
        soundMap.put(Sounds.HITMARKER, Gdx.audio.newSound(Gdx.files.internal(HITMARKER)));
        soundMap.put(Sounds.OH_YEAH, Gdx.audio.newSound(Gdx.files.internal(OH_YEAH)));

        // MUSIC
        musicMap.put(Musics.DAMN_SON, Gdx.audio.newMusic(Gdx.files.internal(DAMN_SON)));
        musicMap.put(Musics.SONIC, Gdx.audio.newMusic(Gdx.files.internal(SONIC)));
        musicMap.put(Musics.SAD_VIOLIN, Gdx.audio.newMusic(Gdx.files.internal(SAD_VIOLIN)));
    }

    public Sound get(Sounds sound) {
        return soundMap.get(sound);
    }

    public Music get(Musics music) {
        return musicMap.get(music);
    }

    public Collection<Music> allMusic() {
        return musicMap.values();
    }

    public long play(Sounds sound) {
        return soundMap.get(sound).play();
    }

    public long playSound(Sounds sound) {
        return play(sound);
    }

    public void playMusic(Musics music) {
        play(music);
    }

    public void play(Musics music) {
        Music m = musicMap.get(music);
        if ( !m.isPlaying() )
            m.play();
    }

    @Override
    public void dispose() {
        for(Map.Entry<Sounds, Sound> entry : soundMap.entrySet())
            entry.getValue().dispose();
        for(Map.Entry<Musics, Music> entry : musicMap.entrySet())
            entry.getValue().dispose();
    }

}
