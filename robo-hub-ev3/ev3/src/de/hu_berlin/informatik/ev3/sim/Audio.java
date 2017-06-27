package de.hu_berlin.informatik.ev3.sim;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;

/**
 * Created by robert on 26.06.17.
 */
public class Audio {

  boolean sim = false;
  lejos.hardware.Audio audio = null;

  public Audio() {
    init(false);
  }
  public Audio(boolean sim) {
    this.sim = sim;
  }

  private void init(boolean sim) {
    this.sim = sim;
    if (!sim) {
      EV3 ev3 = (EV3) BrickFinder.getLocal();
      audio = ev3.getAudio();
    } else {
      audio = null;
    }
  }

  public void systemSound(int s) {
    if ( (audio == null) || sim) return;
    audio.systemSound(s);
  }

  public void  setVolume(int v) {
    if ( (audio == null) || sim) return;
    audio.setVolume(v);
  }
}
