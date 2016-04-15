import lejos.hardware.ev3.EV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;


/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 06.04.16
 * Time: 08:42
 * To change this template use File | Settings | File Templates.
 */
public class ColorSensorControl {

  Port colorPort = null;
  EV3ColorSensor colorSensor = null;
  SensorMode colorSensorMode = null;
  SampleProvider colorSensorSP = null;
  float colorSensorSample[] = null;

  float frequency = 1;

  public ColorSensorControl(EV3 ev3, String port) {
    colorPort = ev3.getPort(port);
    try {
      colorSensor = new EV3ColorSensor(colorPort);
    } catch(Exception e) {
      System.out.println("Port " + port +" is not an ColorSensor");
      colorSensor = null;
    }

    colorSensor.setFloodlight(true);
    colorSensorMode = colorSensor.getRGBMode();
    colorSensorSP = (SampleProvider)colorSensorMode;

    colorSensorSample = new float[colorSensorSP.sampleSize()];
  }

  public int getNextSample() {
    return getNextSample(null);
  }
  public int getNextSample(float[] nextSample) {
    int color = colorSensor.getColorID ();

    if ( nextSample != null ) {
      colorSensorSP.fetchSample(colorSensorSample, 0);
      nextSample[0] = colorSensorSample[0];
      nextSample[1] = colorSensorSample[1];
      nextSample[2] = colorSensorSample[2];
    }

    return color;
  }
}
