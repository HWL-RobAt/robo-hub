/**
 * Created by robert on 17.05.16.
 */
public class MoveControlRemote extends MoveControl {

  int speedMapping = 1;

  public MoveControlRemote() {
    reset();
  }

  public void updateSensorInputs(int input[]) {

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

        currentSpeed[0] = (-16 * (input[1] * Math.min(50, 50 - input[0]))) / 100;
        currentSpeed[1] = (-16 * (input[1] * Math.min(50, 50 + input[0]))) / 100;

        break;
      }
    }
  }

  public int[] getNextSpeed() {
    return currentSpeed;
  }


}
