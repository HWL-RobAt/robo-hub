package de.hu_berlin.informatik.ev3.sensor.color;

import de.hu_berlin.informatik.ev3.sensor.EV3Sensor;
import de.hu_berlin.informatik.ev3.data.BlackBoard;

import java.util.Random;

/**
 * Created by robert on 16.05.16.
 */
public class ColorSensorMulti extends EV3Sensor {

  String ports[] = null;
  int lastColors[] = null;

  public ColorSensorMulti() {
    blackBoardName = "ColorSensorMulti";
    blackBoardID = -1;
  }

  public void setSensorInput(int colors[]) {}

  public int[] getSensorOutput() {
    return lastColors;
  }

  public void update(BlackBoard blackBoard) {
    if (blackBoardID == -1) {
      System.out.println("CS: " + blackBoard.getDataList("ColorSensor").size());
      lastColors = new int[blackBoard.getDataList("ColorSensor").size()];
      blackBoardID = blackBoard.setData(blackBoardName, lastColors);
    }

    for ( int i = 0; i < lastColors.length; i++)
      lastColors[i] = ((int[])blackBoard.getDataByName("ColorSensor",i))[0];
  }
}
