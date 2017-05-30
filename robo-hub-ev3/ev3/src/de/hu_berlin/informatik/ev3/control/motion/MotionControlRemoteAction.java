package de.hu_berlin.informatik.ev3.control.motion;

import de.hu_berlin.informatik.app.connection.protocol.EV3Command;
import de.hu_berlin.informatik.ev3.data.BlackBoard;

import static de.hu_berlin.informatik.ev3.sensor.detector.LineDetector.LINE_DETECTED;
import static de.hu_berlin.informatik.ev3.sensor.detector.TwoLinesDetector.*;

/**
 * Created by robert on 21.06.16.
 */
public class MotionControlRemoteAction extends MotionControl {

  int lastActionInput[] = null;

  String bbInputName = null;

  boolean haveOngoingCommand;

  public MotionControlRemoteAction() {

    bbInputName = "rxRemoteAction";
    reset();
  }

  public void update(BlackBoard blackBoard) {

    int lastSensorInput[] = (int[]) blackBoard.getDataByName(bbInputName, 0);

    if  ( !haveOngoingCommand ) {
      switch (lastSensorInput[0]) {
        case EV3Command.SUBCOMMAND_REMOTE_ACTION_ROTATE:
          break;
        case EV3Command.SUBCOMMAND_REMOTE_ACTION_SWITCH_CTRL:
        case EV3Command.SUBCOMMAND_REMOTE_ACTION_STOP:
        case EV3Command.SUBCOMMAND_REMOTE_ACTION_NONE:
          break;
      }
    } else {
    }
  }
}
