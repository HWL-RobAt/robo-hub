package de.hu_berlin.informatik.ev3.control.motion;

import de.hu_berlin.informatik.ev3.data.BlackBoard;

/**
 * Created by robert on 17.05.16.
 */
public class MotionControlRemote extends MotionControl {

  int speedMapping = 1;

  public MotionControlRemote() {
    reset();
  }

  public void update(BlackBoard blackBoard) {
    int input[] = (int[])blackBoard.getDataByName("RemoteMotionSensor2D",0);

    if (debugWriter != null) debugWriter.println(input[0] + "," + input[1]);

    if ((input[0] == 0) && (input[1] == 0)) {
      speed[0] = speed[1] = 0;
      return;
    }

    switch (speedMapping) {
      case 0: {
        int absSpeed = (int) Math.round(Math.sqrt(input[0] * input[0] + input[1] * input[1]));
        absSpeed = Math.min(absSpeed, 100);

        int fac = (input[1] < 0) ? 1 : -1;

        if (input[0] > 0) {
          speed[0] = fac * 4 * (absSpeed + input[0]);
          speed[1] = fac * 4 * (absSpeed);
        } else {
          speed[0] = fac * 4 * (absSpeed);
          speed[1] = fac * 4 * (absSpeed - input[0]);
        }

        break;
      }
      case 1: {
        // Y is forward/backward - Speed
        // X gives distribution among both motors

        speed[0] = (-1 * maxSpeed * (input[1] * Math.min(50, 50 - input[0]))) / (2500);
        speed[1] = (-1 * maxSpeed * (input[1] * Math.min(50, 50 + input[0]))) / (2500);

        break;
      }
    }
  }
}
