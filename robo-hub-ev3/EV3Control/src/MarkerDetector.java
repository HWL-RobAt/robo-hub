import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.robotics.Color;
import sun.security.util.Resources_it;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 13.04.16
 * Time: 19:59
 * To change this template use File | Settings | File Templates.
 */
public class MarkerDetector extends EV3Sensor {

  HashSet<Integer> markerColors = null;

  boolean detectMarker = false;
  int detectedColor;

  int roundsWithNoMarker = 0;
  int minRoundsToNextMarker = 0;

  public MarkerDetector(int colors[]) {
    Random rand = new Random();
    sensorDBName = "MarkerDetector_" + Math.abs(rand.nextInt());

    markerColors = new HashSet<Integer>();
    markerColors.clear();

    for( int i = 0; i < colors.length; i++)
      markerColors.add(colors[i]);
  }

  public void updateSensorInput(int colors[]) {
    detectMarker = false;
    detectedColor = Color.NONE;
    for ( int i = 0; i < colors.length; i++) {
      if ( markerColors.contains(colors[i]) ) {
        //if ( roundsWithNoMarker <= minRoundsToNextMarker ) {
          detectedColor = colors[i];
          detectMarker = true;
          roundsWithNoMarker = 0;
          return;
        //}
      }
    }


  }

  public int getSensorOutput() {
     return detectMarker?1:0;
  }

  public void updateSensorDB(SensorDB sensorDB) {
    int rawSensorDataArray[] = (int[])sensorDB.getSensorData(sensorDBName); //name of instance
    int cSenMul[] = (int[])sensorDB.getSensorDataForClass(ColorSensorMulti.class);

    if ( rawSensorDataArray == null ) {
      super.initSensorDB(sensorDB);
      rawSensorDataArray = new int[1];
      sensorDB.setSensorData(this.getClass(), sensorDBName, rawSensorDataArray);
    }

    updateSensorInput(cSenMul);
    cSenMul[0] = getSensorOutput();
  }

  public boolean hasMarkerDetected() {
    return detectMarker;
  }

  public int getDetectedMarker() {
    return detectedColor;
  }

}

