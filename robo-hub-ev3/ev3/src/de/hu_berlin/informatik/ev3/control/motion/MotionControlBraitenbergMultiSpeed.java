package de.hu_berlin.informatik.ev3.control.motion;

import de.hu_berlin.informatik.ev3.ai.BraitenbergLine;
import de.hu_berlin.informatik.ev3.data.BlackBoard;

/**
 * Created by robert on 16.05.16.
 */
public class MotionControlBraitenbergMultiSpeed extends MotionControlBraitenberg {

  static final int[][] speedLookup = {{20, 40, 100, 160, 180}, {30, 50, 150, 250, 270}, {30, 50, 200, 350, 370}};

  int speedIndex;
  int curveIndexR;
  int curveIndexL;

  int speedUpdateCountdown;


  public MotionControlBraitenbergMultiSpeed() {
    speedIndex = 0;
    curveIndexR = 2;
    curveIndexL = 2;

    speedUpdateCountdown = 0;
  }

  public void update(BlackBoard blackBoard) {

    int lastSensorInput[] = (int[])blackBoard.getDataByName("Braitenberg",0);

    switch (lastSensorInput[0]) {
      case BraitenbergLine.BRAITENBERG_MOVE_NONE:
        if ( speedUpdateCountdown == 10 ) {
          speedIndex = Math.min(speedIndex+1, speedLookup.length-1);
          speedUpdateCountdown = 0;
        } else {
          speedUpdateCountdown++;
        }

        curveIndexL = 2;
        curveIndexR = 2;
        break;
      case BraitenbergLine.BRAITENBERG_MOVE_LEFT: {
        curveIndexL = Math.max( curveIndexL-1, 0);
        curveIndexR = Math.min( curveIndexR+1, speedLookup[0].length-1 );

        speedIndex = Math.max(speedIndex-1, 0);
        speedUpdateCountdown = 0;
        break;
      }
      case BraitenbergLine.BRAITENBERG_MOVE_RIGHT: {
        curveIndexR = Math.max( curveIndexR-1, 0);
        curveIndexL = Math.min( curveIndexL+1, speedLookup[0].length-1 );

        speedIndex = Math.max(speedIndex-1, 0);
        speedUpdateCountdown = 0;
        break;
      }
    }

    speed[MOTOR_RIGHT] = speedLookup[speedIndex][curveIndexR];
    speed[MOTOR_LEFT] = speedLookup[speedIndex][curveIndexL];

  }

  public void reset() {
    super.reset();

    speedIndex = 0;
    speedUpdateCountdown = 0;

    curveIndexL = 2;
    curveIndexR = 2;
  }

}
