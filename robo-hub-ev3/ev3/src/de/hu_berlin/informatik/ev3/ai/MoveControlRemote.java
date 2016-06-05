package de.hu_berlin.informatik.ev3.ai;

/**
 * Created by robert on 17.05.16.
 */
public class MoveControlRemote extends MoveControl {

  int speedMapping = 1;

  public MoveControlRemote() {
    reset();
  }

  public void updateSensorInputs(int input[]) {

    if (debugWriter != null) {
      debugWriter.println(input[0] + "," + input[1]);
    }

    if ((input[0] == 0) && (input[1] == 0)) {
      currentSpeed[0] = currentSpeed[1] = 0;
      return;
    }

    switch (speedMapping) {
      case 0: {
        int absSpeed = (int) Math.round(Math.sqrt(input[0] * input[0] + input[1] * input[1]));
        absSpeed = Math.min(absSpeed, 100);

        int fac = (input[1] < 0) ? 1 : -1;

        if (input[0] > 0) {
          currentSpeed[0] = fac * 4 * (absSpeed + input[0]);
          currentSpeed[1] = fac * 4 * (absSpeed);
        } else {
          currentSpeed[0] = fac * 4 * (absSpeed);
          currentSpeed[1] = fac * 4 * (absSpeed - input[0]);
        }

        break;
      }
      case 1: {
        // Y is forward/backward - Speed
        // X gives distribution among both motors

        currentSpeed[0] = (-1 * maxSpeed * (input[1] * Math.min(50, 50 - input[0]))) / (2500);
        currentSpeed[1] = (-1 * maxSpeed * (input[1] * Math.min(50, 50 + input[0]))) / (2500);

        break;
      }
    }
  }

  public int[] getNextSpeed() {
    return currentSpeed;
  }

}
