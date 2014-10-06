package de.fau.cs.mad.fly.sound;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

public interface Playable extends Disposable {
  public Playback play(float volume);

  public void resume();

  public void stop();

  public void pause();
}

class SoundPlayable implements Playable {
  private final Sound target;

  public SoundPlayable(Sound target) {
    this.target = target;
  }

  @Override
  public Playback play(float volume) {
    return new SoundPlayback(target, volume);
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
  private float volume;

  public MusicPlayable(Music target, float volume) {
    this.target = target;
    this.volume = volume;
  }

  @Override
  public Playback play(float volume) {
    this.volume = volume;
    return play();
  }

  public Playback play() {
      if(!target.isPlaying()) {
          setVolume(volume);
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
    this.volume = volume;
    target.setVolume(volume);
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