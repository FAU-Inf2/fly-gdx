package de.fau.cs.mad.fly.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.ISetting;
import de.fau.cs.mad.fly.settings.SettingManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * Created by tschaei on 29.09.14.
 */
public class AudioManager implements Disposable {

    public static enum Sounds { CRASH, GATE_PASSED, PICKUP };

    private final String CRASH_SOUND = "sounds/crash.wav";
    private final String GATE_PASSED_SOUND = "sounds/gatepassed.wav";
    private final String PICKUP = "sounds/pickup.wav";

    private Map<Sounds, Playable> soundMap = new HashMap<Sounds, Playable>();

    float volume = 1.0f;

    public AudioManager() {
        soundMap.put(Sounds.CRASH,       create(CRASH_SOUND,       1.0f, false, Types.Sound));
        soundMap.put(Sounds.GATE_PASSED, create(GATE_PASSED_SOUND, 1.0f, false, Types.Sound));
        soundMap.put(Sounds.PICKUP,      create(PICKUP,            1.0f, false, Types.Sound));
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

    private Playable create(FileHandle fileHandle, float defaultVolume, boolean defaultLooping, Types type) {
        switch( type ) {
            case Music:
                return new MusicPlayable(Gdx.audio.newMusic(fileHandle), defaultVolume, defaultLooping, this);
            default:
                return new SoundPlayable(Gdx.audio.newSound(fileHandle), defaultVolume, defaultLooping, this);
        }
    }

    private Playable create(String file, float defaultVolume, boolean defaultLooping, Types type) {
        return create(Gdx.files.internal(file), defaultVolume, defaultLooping, type);
    }

	public void use(final SettingManager settings) {
		volume = settings.getFloat(SettingManager.MASTER_VOLUME);
		SettingManager.SettingListener listener = new SettingManager.SettingListener() {
			@Override
			public void settingChanged(String id, Object value) {
				volume = settings.getFloat(SettingManager.MASTER_VOLUME);
			}
		};
		settings.addListener(SettingManager.MASTER_VOLUME, listener);
	}

    public void mute() {
        volume = 0.0f;
    }

    public void unmute() {
        volume = 1.0f;
    }
}
