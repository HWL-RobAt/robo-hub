package de.hu_berlin.informatik.ev3.sensor;

import de.hu_berlin.informatik.ev3.data.BlackBoardUser;

import java.io.PrintWriter;

/**
 * Created by robert on 14.05.16.
 */
public abstract class EV3Sensor implements BlackBoardUser {

  protected String blackBoardName = null;
  protected int blackBoardID = -1;

  public abstract void setSensorInput(int senorData[]);
  public abstract int[] getSensorOutput();

  public void reset() {}

  public PrintWriter debugWriter = null;

  public void setDebugWriter(PrintWriter debugWriter) {
      this.debugWriter = debugWriter;
    }

}
