package de.fau.cs.mad.fly.sound;

import com.badlogic.gdx.audio.Sound;

public interface Playback {
  public void pause();
  public void resume();
  public void stop();
  public void setVolume(float volume);
  public void setLooping(boolean looping);
  public void setPan(float pan, float volume);
}

class SoundPlayback implements Playback {
  private final Sound target;
  private final long id;

  public SoundPlayback(Sound target, float volume) {
    this.target = target;
    this.id = target.play(volume);
  }

  @Override
  public void pause() {
    target.pause(id);
  }

  @Override
  public void resume() {
    target.resume(id);
  }

  @Override
  public void stop() {
    target.stop(id);
  }

  @Override
  public void setVolume(float volume) {
    target.setVolume(id, volume);
  }

  @Override
  public void setLooping(boolean looping) {
    target.setLooping(id, looping);
  }

  @Override
  public void setPan(float pan, float volume) {
    target.setPan(id, pan, volume);
  }
}