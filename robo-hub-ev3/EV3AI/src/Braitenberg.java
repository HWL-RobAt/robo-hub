/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public interface Braitenberg {

  public static final int BRAITENBERG_MOVE_NONE = 0;
  public static final int BRAITENBERG_MOVE_RIGHT = 1;
  public static final int BRAITENBERG_MOVE_LEFT = 2;


  public void updateSensors(int[] sensoreData);

  public int nextAction();
  public int nextAction(double[] params);

}
