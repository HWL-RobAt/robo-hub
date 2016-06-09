package de.hu_berlin.informatik.ev3.sim;

import de.hu_berlin.informatik.ev3.control.motor.MotorControl;

/**
 * Created by robert on 08.06.16.
 */
public class DualMotorControlSim implements MotorControl {

  public DualMotorControlSim(String rMotor, String lMotor) {
  }

  public void setSpeed(int[] speed) {
    setSpeed(speed[0], speed[1]);
  }

  public void setSpeed(int rSpeed, int lSpeed) {
  }

  public void stop() {
  }

  public void backward(int speed) {
  }

  public void forward(int speed) {
  }

}
