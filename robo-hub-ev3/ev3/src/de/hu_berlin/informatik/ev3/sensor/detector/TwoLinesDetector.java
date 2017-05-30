package de.hu_berlin.informatik.ev3.sensor.detector;

import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.EV3Sensor;
import lejos.robotics.Color;

import java.util.HashSet;

/**
 * Created by robert on 20.06.16.
 */
public class TwoLinesDetector extends LineDetector {

  public static final int LINE_DETECT_RIGHT_SIDE_OF_LINE = 5;
  public static final int LINE_DETECT_LEFT_SIDE_OF_LINE  = 6;
  public static final int LINE_DETECT_ON_LINE            = 7;

  int lastDetectionArray[] = null;
  int lastColor;

  HashSet<Integer> rightLineColorSet = null;
  HashSet<Integer> leftLineColorSet = null;

  public TwoLinesDetector(int lineColorR, int lineColorL ) {
    blackBoardName = "TwoLinesDetector";
    blackBoardID = -1;

    rightLineColorSet = new HashSet<Integer>();
    leftLineColorSet = new HashSet<Integer>();

    rightLineColorSet.add(lineColorR);
    leftLineColorSet.add(lineColorL);

    lastDetectionArray = new int[1];
    lastColor = Color.NONE;
  }

  public TwoLinesDetector(int lineColorsR[], int lineColorsL[]) {
    this(lineColorsR[0], lineColorsL[0]);

    for ( int i = 1; i < lineColorsR.length; i++)  rightLineColorSet.add(lineColorsR[i]);
    for ( int i = 1; i < lineColorsL.length; i++)  leftLineColorSet.add(lineColorsL[i]);
  }

  public void setSensorInput(int sensorData[]) {
    int currentColor = sensorData[0];
    int lastDetection = LINE_DETECT_NONE;   //Default

    if ( rightLineColorSet.contains(currentColor) ) {
       lastDetection = LINE_DETECT_ON_LINE;
       lastColor = LINE_DETECT_RIGHT;
    } else if ( leftLineColorSet.contains(currentColor) ) {
      lastDetection = LINE_DETECT_ON_LINE;
      lastColor = LINE_DETECT_LEFT;
    } else {
      switch (lastColor) {
        case LINE_DETECT_RIGHT:
          lastDetection = LINE_DETECT_RIGHT_SIDE_OF_LINE;
          break;
        case LINE_DETECT_LEFT:
          lastDetection = LINE_DETECT_LEFT_SIDE_OF_LINE;
          break;
      }
    }

    lastDetectionArray[0] = lastDetection;
  }

  public int[] getSensorOutput() {
    return lastDetectionArray;
  }

  public void update(BlackBoard blackBoard) {
    if (blackBoardID == -1) {
      blackBoardID = blackBoard.setData(blackBoardName, lastDetectionArray);
    }

    int cSenMul[] = (int[])blackBoard.getDataByName("ColorSensor",0);

    setSensorInput(cSenMul);
  }
}
