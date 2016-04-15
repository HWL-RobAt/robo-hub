/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class BraitenbergLine implements Braitenberg {
  public static final int SENSOR_RIGHT = 0;
  public static final int SENSOR_LEFT  = 1;

  public static final int LINE_NOT_VISIBLE = 0;
  public static final int LINE_VISIBLE = 1;

  int lineSensors[] = {LINE_NOT_VISIBLE, LINE_NOT_VISIBLE};

  public BraitenbergLine() {
  }

  public void updateSensors(int[] sensoreData) {
    lineSensors[0] = sensoreData[0];
    lineSensors[1] = sensoreData[1];
  }

  public int nextAction() {
    if ( lineSensors[SENSOR_RIGHT] == LINE_VISIBLE ) return BRAITENBERG_MOVE_RIGHT;
    if ( lineSensors[SENSOR_LEFT] == LINE_VISIBLE ) return BRAITENBERG_MOVE_LEFT;

    return BRAITENBERG_MOVE_NONE;
  }

  public int nextAction(int lineDetectorValue) {
    if ( (lineDetectorValue == LineDetector.LINE_DETECT_BOTH) ||
         (lineDetectorValue == LineDetector.LINE_DETECT_NONE )) return BRAITENBERG_MOVE_NONE;

    if ( lineDetectorValue == LineDetector.LINE_DETECT_RIGHT ) return BRAITENBERG_MOVE_RIGHT;
    if ( lineDetectorValue == LineDetector.LINE_DETECT_LEFT ) return BRAITENBERG_MOVE_LEFT;

    return BRAITENBERG_MOVE_NONE;
  }

  /**
   *
   * @param params
   * @return
   *
   * TODO: Learning mode
   */
  public int nextAction(double[] params) {
    if ( lineSensors[SENSOR_RIGHT] == LINE_VISIBLE ) {
      params[0] = 5; //degree
      return BRAITENBERG_MOVE_RIGHT;
    }
    if ( lineSensors[SENSOR_LEFT] == LINE_VISIBLE ) {
      params[0] = 5; //degree
      return BRAITENBERG_MOVE_LEFT;
    }

    return BRAITENBERG_MOVE_NONE;
  }

}
