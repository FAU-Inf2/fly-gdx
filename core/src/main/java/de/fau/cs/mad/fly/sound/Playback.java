package de.fau.cs.mad.fly.sound;

import com.badlogic.gdx.audio.Sound;

public interface Playback {
  public Playable source();
  public void pause();
  public void resume();
  public void stop();
  public void setVolume(float volume);
  public void setLooping(boolean looping);
  public void setPan(float pan, float volume);
}

class SoundPlayback implements Playback {
  private final SoundPlayable source;
  private final long id;

  public SoundPlayback(SoundPlayable source) {
    this.source = source;
    this.id = source.target.play(1.0f * source.defaultVolume * source.manager().volume);
    setLooping(source.defaultLooping);
  }

  @Override
  public Playable source() {
    return source;
  }

  @Override
  public void pause() {
    source.target.pause(id);
  }

  @Override
  public void resume() {
    source.target.resume(id);
  }

  @Override
  public void stop() {
    source.target.stop(id);
  }

  @Override
  public void setVolume(float volume) {
    source.target.setVolume(id, source.defaultVolume * volume * source.manager().volume);
  }

  @Override
  public void setLooping(boolean looping) {
    source.target.setLooping(id, looping);
  }

  @Override
  public void setPan(float pan, float volume) {
    source.target.setPan(id, pan, volume);
  }
}