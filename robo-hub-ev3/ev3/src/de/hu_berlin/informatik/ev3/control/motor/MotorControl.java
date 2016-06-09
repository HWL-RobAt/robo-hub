package de.hu_berlin.informatik.ev3.control.motor;

/**
 * Created by robert on 09.06.16.
 */
public interface MotorControl {

  void setSpeed(int[] speed);

  void stop();

  void backward(int speed);

  void forward(int speed);

}
