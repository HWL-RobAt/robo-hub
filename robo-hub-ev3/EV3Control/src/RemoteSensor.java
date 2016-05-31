import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by robert on 17.05.16.
 */
public class RemoteSensor extends EV3Sensor {

  SocketConnector inS = null;

  int speed[] = null;

  int speedMapping = 1;

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
      int x = Math.max(Math.min(100,command[0]),0) - 50;
      int y = Math.max(Math.min(100,command[1]),0) - 50;

      switch (speedMapping) {
        case 0: {
          int absSpeed = (int)Math.round(Math.sqrt(x*x + y*y));
          absSpeed = Math.min(absSpeed,100);

          int fac = (y < 0)?1:-1;

          if ( x > 0 ){
            speed[0] = fac * 4 * (absSpeed + x);
            speed[1] = fac * 4 * (absSpeed);
          } else {
            speed[0] = fac * 4 * (absSpeed);
            speed[1] = fac * 4 * (absSpeed - x);
          }

          break;
        }
        case 1: {
          // Y is forward/backward - Speed
          // X gives distribution among both motors

          speed[0] = (-16 * (y * Math.min(50,Math.abs(100-command[0])))) / 100;
          speed[1] = (-16 * (y * Math.min(50,Math.abs(command[0]    )))) / 100;

          break;
        }
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
