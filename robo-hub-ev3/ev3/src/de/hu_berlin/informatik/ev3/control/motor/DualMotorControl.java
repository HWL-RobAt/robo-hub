package de.hu_berlin.informatik.ev3.control.motor;

/**
 * Created by robert on 14.05.16.
 */
public class DualMotorControl implements MotorControl {
  protected SingleMotorControl motorR = null;
  protected SingleMotorControl motorL = null;

  public DualMotorControl() {}

  public DualMotorControl(String rMotor, String lMotor) {
    motorR = new SingleMotorControl(rMotor);
    motorL = new SingleMotorControl(lMotor);
  }

  public void setSpeed(int[] speed) {
    setSpeed(speed[0], speed[1]);
  }

  public void setSpeed(int rSpeed, int lSpeed) {
    if ( rSpeed != 0 ) motorR.setSpeed(Math.abs(rSpeed));
    if ( lSpeed != 0 ) motorL.setSpeed(Math.abs(lSpeed));

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
