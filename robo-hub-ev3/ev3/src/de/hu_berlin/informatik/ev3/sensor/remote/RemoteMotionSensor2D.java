package de.hu_berlin.informatik.ev3.sensor.remote;

import de.hu_berlin.informatik.app.connection.Connection;
import de.hu_berlin.informatik.app.connection.SocketConnection;
import de.hu_berlin.informatik.app.connection.protocol.EV3Command;
import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.EV3Sensor;
import lejos.remote.nxt.SocketConnector;


import java.io.PrintWriter;

/**
 * Created by robert on 17.05.16.
 */
public class RemoteMotionSensor2D extends EV3Sensor {

  SocketConnection inS = null;

  int data[] = null;

  public RemoteMotionSensor2D() {
    this(null);
  }

  public RemoteMotionSensor2D(SocketConnection inS) {
    this.inS = inS;
    data = new int[2];
    data[0] = 0;
    data[1] = 0;

    blackBoardName = "RemoteMotionSensor2D";
    blackBoardID = -1;
  }

  public void setDataInputStream(SocketConnection inS) {
    this.inS = inS;
  }

  public void setSensorInput(int command[]) {
    if ( debugWriter != null)
      debugWriter.println("Move: " + command[0] + "," + command[1] + "," + command[2]);

    if ( command[2] == 1 ) {
      data[0] = Math.max(Math.min(100,command[0]),0) - 50;
      data[1] = Math.max(Math.min(100,command[1]),0) - 50;
    } else {
      data[0] = 0;
      data[1] = 0;
    }
  }

  public int[] getSensorOutput() {
    return data;
  }

  public void update(BlackBoard blackBoard) {
    if (blackBoardID == -1)
      blackBoardID = blackBoard.setData(blackBoardName, data);

    while (inS.rxAvailable()) {
      int rxInt = inS.receiveInt();
      int commandParams[] = new int[3];
      int command = EV3Command.decode(rxInt, commandParams);

      if (command == EV3Command.COMMAND_MOVE) {
        setSensorInput(commandParams);
      } else {
        inS.returnReceivedInt(rxInt);
        break;
      }
    }
  }

}
