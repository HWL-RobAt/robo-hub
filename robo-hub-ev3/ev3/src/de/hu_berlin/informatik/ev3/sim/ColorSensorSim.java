package de.hu_berlin.informatik.ev3.sim;

import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.color.ColorSensor;
import lejos.robotics.Color;

/**
 * Created by robert on 08.06.16.
 */
public class ColorSensorSim extends ColorSensor {

    int colorDetectionResult[] = null;

    public ColorSensorSim(String port) {
      colorDetectionResult = new int[1];
      colorDetectionResult[0] = Color.NONE;

      blackBoardName = "ColorSensor";
      blackBoardID = -1;
    }

    public void configureClassifier(String configfile) {
    }

    public void setSensorInput(int rgb[]) {
    }

    public int[] getSensorOutput() {
      return null;
    }

    public void update(BlackBoard blackBoard) {
      if (blackBoardID == -1) blackBoardID = blackBoard.setData(blackBoardName, colorDetectionResult);
    }

    public void close() {}
}
