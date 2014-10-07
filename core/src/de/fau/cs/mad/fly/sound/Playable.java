package de.fau.cs.mad.fly.sound;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

public interface Playable extends Disposable {
  public Playback play();

  public void resume();

  public void stop();

  public void pause();

  public boolean isSoundEffect();

  public boolean isMusic();

  public AudioManager manager();
}

class SoundPlayable implements Playable {
  final Sound target;
  final float defaultVolume;
  final boolean defaultLooping;
  private final AudioManager manager;

  public SoundPlayable(Sound target, float defaultVolume, boolean defaultLooping, AudioManager manager) {
    this.target = target;
    this.defaultVolume = defaultVolume;
    this.defaultLooping = defaultLooping;
    this.manager = manager;
  }

  @Override
  public AudioManager manager() {
    return manager;
  }

  @Override
  public Playback play() {
    return new SoundPlayback(this);
  }

  @Override
  public boolean isSoundEffect() {
    return true;
  }

  @Override
  public boolean isMusic() {
    return false;
  }

  @Override
  public void resume() {
    target.resume();
  }

  @Override
  public void stop() {
    target.stop();
  }

  @Override
  public void pause() {
    target.pause();
  }

  @Override
  public void dispose() {
    target.dispose();
  }
}

class MusicPlayable implements Playable, Playback {
  private final Music target;
  private final float defaultVolume;
  private final AudioManager manager;

  public MusicPlayable(Music target, float defaultVolume, boolean defaultLooping, AudioManager manager) {
    this.target = target;
    this.manager = manager;
    this.defaultVolume = defaultVolume;
    setLooping(defaultLooping);
  }

  @Override
  public AudioManager manager() {
    return manager;
  }

  public boolean isMusic() {
    return true;
  }

  @Override
  public boolean isSoundEffect() {
    return false;
  }

  @Override
  public Playable source() {
    return this;
  }

  @Override
  public Playback play() {
      if(!target.isPlaying()) {
          setVolume(1.0f);
          target.play();
      }
      return this;
  }

  @Override
  public void pause() {
    target.pause();
  }

  @Override
  public void resume() {
    play();
  }

  @Override
  public void stop() {
    target.stop();
  }

  @Override
  public void setVolume(float volume) {
    target.setVolume(volume * defaultVolume * manager.volume);
  }

  @Override
  public void setLooping(boolean looping) {
    target.setLooping(looping);
  }

  @Override
  public void setPan(float pan, float volume) {
    target.setPan(pan, volume);
  }

  @Override
  public void dispose() {
    target.dispose();
  }
}