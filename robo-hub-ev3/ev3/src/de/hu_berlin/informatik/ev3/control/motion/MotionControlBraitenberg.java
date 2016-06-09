package de.hu_berlin.informatik.ev3.control.motion;

import de.hu_berlin.informatik.ev3.ai.BraitenbergLine;
import de.hu_berlin.informatik.ev3.data.BlackBoard;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class MotionControlBraitenberg extends MotionControl {

  public MotionControlBraitenberg() {
    reset();
  }

  public void update(BlackBoard blackBoard) {

    int lastSensorInput[] = (int[])blackBoard.getDataByName("Braitenberg",0);

    switch (lastSensorInput[0]) {
      case BraitenbergLine.BRAITENBERG_MOVE_NONE: {
        speed[MOTOR_RIGHT] = speed[MOTOR_LEFT] = maxSpeed;
        break;
      }
      case BraitenbergLine.BRAITENBERG_MOVE_LEFT: {
        speed[MOTOR_RIGHT] = maxSpeed/2;
        speed[MOTOR_LEFT] = 0;
        break;
      }
      case BraitenbergLine.BRAITENBERG_MOVE_RIGHT: {
        speed[MOTOR_RIGHT] = 0;
        speed[MOTOR_LEFT] = maxSpeed/2;
        break;
      }
    }
  }

}
