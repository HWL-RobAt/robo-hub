package de.hu_berlin.informatik.ev3.sensorcontrol;

import de.hu_berlin.informatik.app.connection.SocketConnection;
import de.hu_berlin.informatik.app.connection.protocol.EV3Command;


import java.io.PrintWriter;

/**
 * Created by robert on 17.05.16.
 */
public class RemoteSensor extends EV3Sensor {

  SocketConnection inS = null;

  int speed[] = null;

  private PrintWriter debugWriter = null;

  public RemoteSensor(SocketConnection inS) {
    this.inS = inS;
    speed = new int[2];
    speed[0] = 0;
    speed[1] = 0;
  }

  public void setDataInputStream(SocketConnection inS) {
    this.inS = inS;
  }

  public void updateSensorInput(int command[]) {
    if ( debugWriter != null) {
      debugWriter.println("Move: " + command[0] + "," + command[1] + "," + command[2]);
    }
    if ( command[2] == 1 ) {
      speed[0] = Math.max(Math.min(100,command[0]),0) - 50;
      speed[1] = Math.max(Math.min(100,command[1]),0) - 50;
    } else {
      speed[0] = 0;
      speed[1] = 0;
    }
  }

  public int[] getSensorOutputs() {
    return speed;
  }

  public void updateSensorDB(SensorDB sensorDB) {
    if ( null == (int[])sensorDB.getSensorData(sensorDBName)) {
      super.initSensorDB(sensorDB);
      sensorDB.setSensorData(this.getClass(), sensorDBName, speed);
    }

    while (inS.rxAvailable()) {
      int rxInt = inS.receiveInt();
      int commandParams[] = new int[3];
      int command = EV3Command.decode(rxInt, commandParams);

      if (command == EV3Command.COMMAND_MOVE) {
        updateSensorInput(commandParams);
      } else {
        inS.returnReceivedInt(rxInt);
        break;
      }
    }
  }

  public void clear() {
    speed[0] = speed[1] = 0;
  }

  public void setDebugWriter(PrintWriter debugWriter) {
    this.debugWriter = debugWriter;
  }

}
