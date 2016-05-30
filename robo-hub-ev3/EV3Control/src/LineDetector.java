import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 10.04.16
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class LineDetector extends EV3Sensor {

  public static final int LINE_DETECT_NONE  = 0;
  public static final int LINE_DETECT_RIGHT = 1;
  public static final int LINE_DETECT_LEFT  = 2;
  public static final int LINE_DETECT_BOTH  = 3;

  public static final int RIGHT = 0;
  public static final int LEFT = 1;

  int[] lastSample = null;
  int lastDetection = LINE_DETECT_NONE;
  int lastDetectionArray[] = null;

  int lineColor;

  public LineDetector(int lineColor) {
    Random rand = new Random();
    sensorDBName = "LineDetector_" + Math.abs(rand.nextInt());

    this.lineColor = lineColor;

    lastSample = new int[2];
    lastDetectionArray = new int[1];
  }

  public void updateSensorInput(int senorData[]) {
    lastSample[RIGHT] = senorData[RIGHT];
    lastSample[LEFT] = senorData[LEFT];

    lastDetection = 0;
    if ( senorData[RIGHT] == lineColor ) lastDetection = LINE_DETECT_RIGHT;
    if ( senorData[LEFT] == lineColor ) lastDetection += LINE_DETECT_LEFT;
    lastDetectionArray[0] = lastDetection;
  }

  public int getSensorOutput() {
    return lastDetection;
  }

  public int[] getSensorOutputs() {
    return lastDetectionArray;
  }

  public void updateSensorDB(SensorDB sensorDB) {
    int rawSensorDataArray[] = null;
    Object sensorData = sensorDB.getSensorData(sensorDBName);  //name of instance

    if ( sensorData == null ) {
      super.initSensorDB(sensorDB);
      rawSensorDataArray = new int[1];
      sensorDB.setSensorData(this.getClass(), sensorDBName, rawSensorDataArray);
    } else {
      rawSensorDataArray = (int[])sensorData;
    }

    int cSenMul[] = (int[])sensorDB.getSensorDataForClass(ColorSensorMulti.class);

    updateSensorInput(cSenMul);
    rawSensorDataArray[0] = getSensorOutput();
  }

}
