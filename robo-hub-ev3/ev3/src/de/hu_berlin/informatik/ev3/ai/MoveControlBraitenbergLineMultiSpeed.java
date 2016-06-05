package de.hu_berlin.informatik.ev3.ai;

import de.hu_berlin.informatik.ev3.sensorcontrol.LineDetector;

/**
 * Created by robert on 16.05.16.
 */
public class MoveControlBraitenbergLineMultiSpeed extends MoveControlBraitenbergLine {

  static final int[][] speedLookup = {{20, 40, 100, 160, 180}, {30, 50, 150, 250, 270}, {30, 50, 200, 350, 370}};

  int speedIndex;
  int curveIndexR;
  int curveIndexL;

  int speedUpdateCountdown;


  public MoveControlBraitenbergLineMultiSpeed() {
    speedIndex = 0;
    curveIndexR = 2;
    curveIndexL = 2;

    speedUpdateCountdown = 0;
  }

  public int[] getNextSpeed() {
    switch (lastSensorInput) {
      case LineDetector.LINE_DETECT_BOTH:
      case LineDetector.LINE_DETECT_NONE: {
        if ( lastSensorInput == MoveControlBraitenbergLine.BRAITENBERG_MOVE_NONE) {
          if ( speedUpdateCountdown == 10 ) {
            speedIndex = Math.min(speedIndex+1, speedLookup.length-1);
            speedUpdateCountdown = 0;
          } else {
            speedUpdateCountdown++;
          }

          curveIndexL = 2;
          curveIndexR = 2;
        } else {
          speedIndex = Math.max(speedIndex-1, 0);
          speedUpdateCountdown = 0;
        }
        break;
      }
      case LineDetector.LINE_DETECT_LEFT: {
        curveIndexL = Math.max( curveIndexL-1, 0);
        curveIndexR = Math.min( curveIndexR+1, speedLookup[0].length-1 );
        break;
      }
      case LineDetector.LINE_DETECT_RIGHT: {
        curveIndexR = Math.max( curveIndexR-1, 0);
        curveIndexL = Math.min( curveIndexL+1, speedLookup[0].length-1 );
        break;
      }
    }

    currentSpeed[LineDetector.RIGHT] = speedLookup[speedIndex][curveIndexR];
    currentSpeed[LineDetector.LEFT] = speedLookup[speedIndex][curveIndexL];

    return currentSpeed;
  }

  public void reset() {
    super.reset();
    speedIndex = 0;
    curveIndexL = 2;
    curveIndexR = 2;
  }

}
