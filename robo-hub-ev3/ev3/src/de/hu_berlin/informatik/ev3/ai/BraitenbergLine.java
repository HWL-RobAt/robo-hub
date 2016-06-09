package de.hu_berlin.informatik.ev3.ai;

import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.detector.LineDetector;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class BraitenbergLine extends Braitenberg {

  public BraitenbergLine() {
    super();
  }

  public void update(BlackBoard blackBoard) {
    super.update(blackBoard);

    switch (dataInput[0]) {
      case LineDetector.LINE_DETECT_BOTH:
      case LineDetector.LINE_DETECT_NONE:
        dataOutput[0] = BRAITENBERG_MOVE_NONE;
        break;
      case LineDetector.LINE_DETECT_RIGHT:
        dataOutput[0] = BRAITENBERG_MOVE_RIGHT;
        break;
      case LineDetector.LINE_DETECT_LEFT:
        dataOutput[0] = BRAITENBERG_MOVE_LEFT;
        break;
    }
  }

}
