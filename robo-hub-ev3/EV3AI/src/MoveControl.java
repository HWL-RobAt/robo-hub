/**
 * Created by robert on 14.05.16.
 */
public abstract class MoveControl {

  public int maxSpeed = 0;
  public int defaultSpeed = 0;

  public void setMaxSpeed(int speed) {
    this.maxSpeed = speed;
  }

  public void setDefaultSpeed(int defaultSpeed) {
    this.defaultSpeed = defaultSpeed;
  }

  public void reset() {

  }

  public void updateSensorInputs(int[] sensoreData) {

  }

  public int[] getNextSpeed() {
    return null;
  }
}
