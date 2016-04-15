/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class BraitenbergDistance implements Braitenberg {
  public static final int SENSOR_RIGHT = 0;
  public static final int SENSOR_LEFT  = 1;

  public static final int LINE_NOT_VISIBLE = 0;
  public static final int LINE_VISIBLE = 1;

  int lineSensors[] = {LINE_NOT_VISIBLE, LINE_NOT_VISIBLE};

  public BraitenbergDistance() {
  }

  public void updateSensors(int[] sensoreData) {
    lineSensors[SENSOR_RIGHT] = sensoreData[SENSOR_RIGHT];
    lineSensors[SENSOR_LEFT] = sensoreData[SENSOR_LEFT];
  }

  public int nextAction() {
    if ( lineSensors[SENSOR_RIGHT] == LINE_VISIBLE ) return BRAITENBERG_MOVE_RIGHT;
    if ( lineSensors[SENSOR_LEFT] == LINE_VISIBLE ) return BRAITENBERG_MOVE_LEFT;

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
