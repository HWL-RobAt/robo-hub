package de.hu_berlin.informatik.ev3.control.motion;

import de.hu_berlin.informatik.ev3.ai.BraitenbergLine;
import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.detector.TwoLinesDetector;

import static de.hu_berlin.informatik.ev3.sensor.detector.LineDetector.LINE_DETECTED;
import static de.hu_berlin.informatik.ev3.sensor.detector.LineDetector.LINE_DETECT_RIGHT;
import static de.hu_berlin.informatik.ev3.sensor.detector.TwoLinesDetector.LINE_DETECT_LEFT_SIDE_OF_LINE;
import static de.hu_berlin.informatik.ev3.sensor.detector.TwoLinesDetector.LINE_DETECT_ON_LINE;
import static de.hu_berlin.informatik.ev3.sensor.detector.TwoLinesDetector.LINE_DETECT_RIGHT_SIDE_OF_LINE;

/**
 * Created by robert on 21.06.16.
 */
public class MotionControlHighway extends MotionControl {

  public enum HighwayMode {LINE_BORDER, TWO_LINE_BORDER};

  HighwayMode hwMode;
  String bbInputName;

  public MotionControlHighway(HighwayMode mode) {
    hwMode = mode;
    if (hwMode == HighwayMode.LINE_BORDER) bbInputName = "LineDetector";
    else bbInputName = "TwoLinesDetector";
    reset();
  }

  public MotionControlHighway() {
    this(HighwayMode.LINE_BORDER);
  }

  public void update(BlackBoard blackBoard) {

    int lastSensorInputRight[] = (int[]) blackBoard.getDataByName(bbInputName, 0);
    int lastSensorInputLeft[] = (int[]) blackBoard.getDataByName(bbInputName, 1);

    if (hwMode == HighwayMode.TWO_LINE_BORDER) {
      if ((lastSensorInputRight[0] == LINE_DETECT_ON_LINE) ||
          (lastSensorInputRight[0] == LINE_DETECT_RIGHT_SIDE_OF_LINE)) {
        speed[MOTOR_RIGHT] = maxSpeed / 2;
        speed[MOTOR_LEFT] = 0;
      } else {
        if ((lastSensorInputLeft[0] == LINE_DETECT_ON_LINE) ||
            (lastSensorInputLeft[0] == LINE_DETECT_LEFT_SIDE_OF_LINE)) {
          speed[MOTOR_RIGHT] = 0;
          speed[MOTOR_LEFT] = maxSpeed / 2;
        } else {
          if ((lastSensorInputRight[0] == LINE_DETECT_LEFT_SIDE_OF_LINE) &&
              (lastSensorInputLeft[0] == LINE_DETECT_RIGHT_SIDE_OF_LINE)) {
            speed[MOTOR_RIGHT] = speed[MOTOR_LEFT] = maxSpeed;
          } else {
            //undefined state
            speed[MOTOR_RIGHT] = speed[MOTOR_LEFT] = 0;
          }
        }
      }
    } else {
      if (lastSensorInputRight[0] == LINE_DETECTED) {
        speed[MOTOR_RIGHT] = maxSpeed / 2;
        speed[MOTOR_LEFT] = 0;
      } else {
        if (lastSensorInputLeft[0] == LINE_DETECTED) {
          speed[MOTOR_RIGHT] = 0;
          speed[MOTOR_LEFT] = maxSpeed / 2;
      } else {
        speed[MOTOR_RIGHT] = speed[MOTOR_LEFT] = maxSpeed;
      }}
    }
  }
}
