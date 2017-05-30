package de.hu_berlin.informatik.ev3.sensor.detector;

import de.hu_berlin.informatik.ev3.sensor.EV3Sensor;
import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.color.ColorSensorMulti;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 10.04.16
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class LineDetector extends EV3Sensor {

  public static final int LINE_DETECT_NONE  = 0;
  public static final int LINE_DETECTED     = 1;
  public static final int LINE_DETECT_RIGHT = 2;
  public static final int LINE_DETECT_LEFT  = 3;
  public static final int LINE_DETECT_BOTH  = 4;

  public static final int RIGHT = 0;
  public static final int LEFT = 1;

  int lastDetectionArray[] = null;

  HashSet<Integer> lineColorSet = null;

  protected LineDetector() {}

  public LineDetector(int lineColor) {
    blackBoardName = "LineDetector";
    blackBoardID = -1;

    lineColorSet = new HashSet<Integer>();
    lineColorSet.add(lineColor);

    lastDetectionArray = new int[1];
  }

  public LineDetector(int lineColors[]) {
    this(lineColors[0]);

    for ( int i = 1; i < lineColors.length; i++)
      lineColorSet.add(lineColors[i]);
  }

  public void setSensorInput(int senorData[]) {
    int lastDetection = 0;
    if ( lineColorSet.contains(senorData[RIGHT]) ) lastDetection = LINE_DETECT_RIGHT;
    if ( lineColorSet.contains(senorData[LEFT]) ) lastDetection += LINE_DETECT_LEFT;
    lastDetectionArray[0] = lastDetection;
  }

  public int[] getSensorOutput() {
    return lastDetectionArray;
  }

  public void update(BlackBoard blackBoard) {
    if (blackBoardID == -1) {
      blackBoardID = blackBoard.setData(blackBoardName, lastDetectionArray);
    }

    int cSenMul[] = (int[])blackBoard.getDataByName("ColorSensorMulti",0);

    setSensorInput(cSenMul);
  }

}
