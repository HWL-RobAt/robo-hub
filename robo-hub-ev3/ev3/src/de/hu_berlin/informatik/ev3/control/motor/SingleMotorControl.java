package de.hu_berlin.informatik.ev3.control.motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 10.04.16
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class SingleMotorControl {
  Port motorPort;
  public RegulatedMotor motor = null;
  int iMaxSpeed = 0;
  double maxSpeed = 0.0;

  int lastSetSpeed = 0;

  public SingleMotorControl() {}

  public SingleMotorControl(Port motorPort) {
    init(motorPort);
  }

  public SingleMotorControl(String motorPort) {
    if (motorPort.equals("MotorPort.A")) {
      init(MotorPort.A);
    }
    if (motorPort.equals("MotorPort.B")) {
      init(MotorPort.B);
    }
    if (motorPort.equals("MotorPort.C")) {
      init(MotorPort.C);
    }
    if (motorPort.equals("MotorPort.D")) {
      init(MotorPort.D);
    }
  }

  private void init(Port motorPort) {
    this.motorPort = motorPort;
    this.motor = new EV3LargeRegulatedMotor(motorPort);
    maxSpeed = motor.getMaxSpeed();
    iMaxSpeed = (int) Math.round(maxSpeed);
  }

  public void setSpeed(int speed) {
    lastSetSpeed = speed;
    motor.setSpeed(Math.abs(speed));
    if ( speed >= 0) motor.forward();
    else motor.backward();
  }

  public void stop() {
    motor.stop();
  }

  public void close() {
    motor.stop();
    motor.close();
  }




}
