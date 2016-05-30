import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by robert on 17.05.16.
 */
public class RemoteSensor extends EV3Sensor {

  SocketConnector inS = null;

  int speed[] = null;

  public RemoteSensor(SocketConnector inS) {
    this.inS = inS;
    speed = new int[2];
    speed[0] = 0;
    speed[1] = 1;
  }

  public void setDataInputStream(SocketConnector inS) {
    this.inS = inS;
  }

  public void updateSensorInput(int command[]) {
    if ( command[2] == 1 ) {
      int x = command[0] - 50;
      int y = command[1] - 50;

      int absSpeed = (int)Math.round(Math.sqrt(x*x + y*y));

      if ( y < 0 ) {
        speed[0] = 4 * (absSpeed - x);
        speed[1] = 4 * (absSpeed);
      } else {
        speed[0] = -4 * (absSpeed - x);
        speed[1] = -4 * (absSpeed);
      }

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

    int avBytes = 0;

    try {
      avBytes = inS.in.available();
      while (avBytes > 4) {
        int rxInt = inS.receiveInt();
        int commandParams[] = new int[3];
        int command = AppCommand.decode(rxInt, commandParams);

        if (command == AppCommand.COMMAND_MOVE) {
          updateSensorInput(commandParams);
        } else {
          inS.returnReceivedInt(rxInt);
          break;
        }
        avBytes = inS.in.available();
      }
      //inS.reset();
    } catch (IOException ioe) {
      System.out.println("Error while getting availbable Bytes.");
    }
  }

}
