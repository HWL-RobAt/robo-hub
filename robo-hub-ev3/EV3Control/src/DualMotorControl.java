/**
 * Created by robert on 14.05.16.
 */
public class DualMotorControl implements EV3Controler {
  MotorControl motorR = null;
  MotorControl motorL = null;

  public DualMotorControl(String rMotor, String lMotor) {
    motorR = new MotorControl(rMotor);
    motorL = new MotorControl(lMotor);
  }

  public void setSpeed(int rSpeed, int lSpeed) {
    motorR.setSpeed(Math.abs(rSpeed));
    motorL.setSpeed(Math.abs(lSpeed));

    if (rSpeed == 0)  motorR.stop();
    else {
      if (rSpeed < 0) motorR.motor.backward();
      else            motorR.motor.forward();
    }

    if (lSpeed == 0)  motorL.stop();
    else {
      if (lSpeed < 0) motorL.motor.backward();
      else            motorL.motor.forward();
    }
  }

  public void stop() {
    motorR.stop();
    motorL.stop();
  }

  public void backward(int speed) {
    motorR.setSpeed(speed);
    motorL.setSpeed(speed);
    motorL.motor.backward();
    motorR.motor.backward();
  }

  public void forward(int speed) {
    motorR.setSpeed(speed);
    motorL.setSpeed(speed);
    motorL.motor.forward();
    motorR.motor.forward();
  }

}
