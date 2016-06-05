package de.hu_berlin.informatik.ev3.ai;

import java.io.PrintWriter;

/**
 * Created by robert on 14.05.16.
 */
public abstract class MoveControl {

  public int maxSpeed = 0;

  int currentSpeed[] = {0,0};

  PrintWriter debugWriter = null;

  public void setMaxSpeed(int speed) {
    this.maxSpeed = speed;
  }

  public void reset() {
    currentSpeed[0] = currentSpeed[1] = 0;
  }

  public void updateSensorInputs(int[] sensoreData) {

  }

  public int[] getNextSpeed() {
    return null;
  }

  public void setDebugWriter(PrintWriter debugWriter) {
    this.debugWriter = debugWriter;
  }

}
