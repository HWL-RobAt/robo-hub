/**
 * Created by robert on 17.05.16.
 */
public class MoveControlRemote extends MoveControl {

  int speed[] = null;

  public MoveControlRemote() {
    speed = new int[2];
    speed[0] = speed[1] = 0;
  }

  public void updateSensorInputs(int input[]) {
    speed = input;
  }

  public int[] getNextSpeed() {
    return speed;
  }

  static public void translateSpeed(int speed[]) {
    int x = speed[0] - 50;
    int y = speed[1] - 50;

    int absSpeed = (int)Math.round(Math.abs(Math.sqrt(x*x + y*y)));

    x = absSpeed-x;
    y = absSpeed+x;

    speed[0] = x;
    speed[1] = y;

  }

  public void reset() {
    speed[0] = speed[1] = 0;
  }

}
