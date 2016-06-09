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
  public static final int LINE_DETECT_RIGHT = 1;
  public static final int LINE_DETECT_LEFT  = 2;
  public static final int LINE_DETECT_BOTH  = 3;

  public static final int RIGHT = 0;
  public static final int LEFT = 1;

  int lastDetectionArray[] = null;

  int lineColor;
  HashSet<Integer> lineColorSet = null;

  public LineDetector(int lineColor) {
    blackBoardName = "LineDetector";
    blackBoardID = -1;

    this.lineColor = lineColor;

    lastDetectionArray = new int[1];
  }

  public LineDetector(int lineColors[]) {
    this(lineColors[0]);

    lineColorSet = new HashSet<Integer>();
  }

  public void setSensorInput(int senorData[]) {
    int lastDetection = 0;
    if ( senorData[RIGHT] == lineColor ) lastDetection = LINE_DETECT_RIGHT;
    if ( senorData[LEFT] == lineColor ) lastDetection += LINE_DETECT_LEFT;
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
