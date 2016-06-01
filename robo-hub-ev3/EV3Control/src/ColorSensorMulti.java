import java.util.Random;

/**
 * Created by robert on 16.05.16.
 */
public class ColorSensorMulti extends EV3Sensor {

  String ports[] = null;
  ColorSensor cSensorList[] = null;
  int lastColors[] = null;

  public ColorSensorMulti(String[] ports) {
    Random rand = new Random();
    sensorDBName = "ColorSensorMulti_" + Math.abs(rand.nextInt());

    this.ports = ports;
    cSensorList = new ColorSensor[ports.length];

    for( int i = 0; i < ports.length; i++) {
      cSensorList[i] = new ColorSensor(ports[i]);
    }
  }

  public void updateSensorDB(SensorDB sensorDB) {
    int iArray[] = (int[])sensorDB.getSensorData(sensorDBName); //name of instance

    if ( iArray == null ) {
      iArray = new int[cSensorList.length];
      sensorDB.setSensorData(this.getClass(), sensorDBName, iArray);
    }

    for( int i = 0; i < iArray.length; i++) {
      iArray[i] = cSensorList[i].getNextSample();
    }

    lastColors = iArray;
  }

  public void stop() {
    for( int i = 0; i < cSensorList.length; i++) {
      cSensorList[i].colorSensor.setFloodlight(false);
      cSensorList[i].colorSensor.close();
    }
  }

  /*
  public void readSensors(int colors[]) {
    for( int i = 0; i < cSensorList.length; i++) {
      colors[i] = cSensorList[i].getNextSample();
    }
  }
  */
  public int[] readLastSensorsValues() {
    return lastColors;
  }

}
