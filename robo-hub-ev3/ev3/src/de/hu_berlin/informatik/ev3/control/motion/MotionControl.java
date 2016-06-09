package de.hu_berlin.informatik.ev3.control.motion;

import de.hu_berlin.informatik.ev3.data.BlackBoardUser;

import java.io.PrintWriter;

/**
 * Created by robert on 14.05.16.
 */
public abstract class MotionControl implements BlackBoardUser {

  public static final int MOTOR_RIGHT = 0;
  public static final int MOTOR_LEFT = 1;

  protected int maxSpeed = 0;

  protected int speed[] = {0,0};

  public void setMaxSpeed(int speed) {
    this.maxSpeed = speed;
  }

  public int[] getSpeed() {
    return speed;
  }

  public void reset() {
    speed[0] = speed[1] = 0;
  }

  PrintWriter debugWriter = null;

  public void setDebugWriter(PrintWriter debugWriter) {
    this.debugWriter = debugWriter;
  }

}
