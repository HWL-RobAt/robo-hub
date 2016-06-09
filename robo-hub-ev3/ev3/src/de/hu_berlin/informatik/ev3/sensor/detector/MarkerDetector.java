package de.hu_berlin.informatik.ev3.sensor.detector;

import de.hu_berlin.informatik.ev3.sensor.EV3Sensor;
import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.color.ColorSensorMulti;
import lejos.robotics.Color;

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
  int detectMarkerInt[] = {0};

  int detectedColor;

  int roundsWithNoMarker = 0;
  int minRoundsToNextMarker = 0;

  public MarkerDetector(int colors[]) {
    blackBoardName = "MarkerDetector";

    markerColors = new HashSet<Integer>();
    markerColors.clear();

    for( int i = 0; i < colors.length; i++)
      markerColors.add(colors[i]);
  }

  public void setSensorInput(int colors[]) {
    detectMarker = false;
    detectedColor = Color.NONE;
    int i = 0;
    for (;i < colors.length; i++) {
      if (markerColors.contains(colors[i])) {
        if (roundsWithNoMarker >= minRoundsToNextMarker) {
          detectedColor = colors[i];
          detectMarker = true;
          roundsWithNoMarker = 0;
        }
        break;
      }
    }

    if (i == colors.length) roundsWithNoMarker++;

    detectMarkerInt[0] = detectMarker?1:0;
  }

  public int[] getSensorOutput() {
    detectMarkerInt[0] = detectMarker?1:0;
    return detectMarkerInt;
  }

  public void update(BlackBoard blackBoard) {
    if (blackBoardID == -1)
      blackBoardID = blackBoard.setData(blackBoardName, detectMarkerInt);

    int cSenMul[] = (int[])blackBoard.getDataByName("ColorSensorMulti",0);

    setSensorInput(cSenMul);
  }

  public boolean hasMarkerDetected() {
    return detectMarker;
  }

  public int getDetectedMarker() {
    return detectedColor;
  }

  public int getNextColor() {
    return markerColors.iterator().next();
  }


}

