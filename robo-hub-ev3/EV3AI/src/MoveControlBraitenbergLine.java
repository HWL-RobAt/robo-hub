/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class MoveControlBraitenbergLine extends MoveControlBraitenberg {

  int lastSensorInput = LineDetector.LINE_DETECT_NONE;

  int currentSpeed[] = null;

  public MoveControlBraitenbergLine() {
    currentSpeed = new int[2];
    currentSpeed[0] = currentSpeed[1] = 0;
  }

  public void updateSensorInputs(int input[]) {
    lastSensorInput = input[0];
  }

  public int getNextAction() {
    if ( (lastSensorInput == LineDetector.LINE_DETECT_BOTH) ||
         (lastSensorInput == LineDetector.LINE_DETECT_NONE )) return BRAITENBERG_MOVE_NONE;

    if ( lastSensorInput == LineDetector.LINE_DETECT_RIGHT ) return BRAITENBERG_MOVE_RIGHT;
    if ( lastSensorInput == LineDetector.LINE_DETECT_LEFT ) return BRAITENBERG_MOVE_LEFT;

    return BRAITENBERG_MOVE_NONE;
  }

  public int[] getNextSpeed() {
    switch (lastSensorInput) {
      case LineDetector.LINE_DETECT_BOTH:
      case LineDetector.LINE_DETECT_NONE: {
        currentSpeed[0] = currentSpeed[1] = defaultSpeed;
        break;
      }
      case LineDetector.LINE_DETECT_LEFT: {
        currentSpeed[LineDetector.RIGHT] = defaultSpeed;
        currentSpeed[LineDetector.LEFT] = 0;
        break;
      }
      case LineDetector.LINE_DETECT_RIGHT: {
        currentSpeed[LineDetector.RIGHT] = 0;
        currentSpeed[LineDetector.LEFT] = defaultSpeed;
        break;
      }
    }

    return currentSpeed;
  }
}
