package de.hu_berlin.informatik.ev3.sensorcontrol;

/**
 * Created by robert on 14.05.16.
 */
public abstract class EV3Sensor {

  protected String sensorDBName = null;

  public void initSensorDB(SensorDB sensorDB) {
  }

  public void reset() {}
}
