import lejos.hardware.BrickFinder;
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
public class ColorSensor extends EV3Sensor{

  Port colorPort = null;
  EV3ColorSensor colorSensor = null;
  SensorMode colorSensorMode = null;
  SampleProvider colorSensorSP = null;
  float colorSensorSample[] = null;



  public ColorSensor(String port) {
    EV3 ev3 = (EV3) BrickFinder.getLocal();
    init(ev3, port);
  }

  public ColorSensor(EV3 ev3, String port) {
    init(ev3, port);
  }

  private void init(EV3 ev3, String port) {
    colorPort = ev3.getPort(port);
    try {
      colorSensor = new EV3ColorSensor(colorPort);
    } catch(Exception e) {
      System.out.println("Port " + port +" is not an ColorSensor");
      System.exit(0);
      colorSensor = null;
    }

    colorSensor.setFloodlight(true);
    //colorSensorMode = colorSensor.getRGBMode();
    colorSensorMode = colorSensor.getColorIDMode();
    colorSensorSP = (SampleProvider)colorSensorMode;

    colorSensorSample = new float[colorSensorSP.sampleSize()];
  }

  public int getNextSample() {
    return getNextSample(null);
  }

  public int getNextSample(float[] nextSample) {
    if ( nextSample != null ) {
      colorSensorSP.fetchSample(colorSensorSample, 0);

      for ( int i = 0; i < colorSensorSP.sampleSize(); i++)
         nextSample[i] = colorSensorSample[i];
    }

    //TODO: get color using rbg

    return (colorSensorSP.sampleSize() > 1)?0:colorSensor.getColorID();
  }
}
