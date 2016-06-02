import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;


/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 06.04.16
 * Time: 08:42
 * To change this template use File | Settings | File Templates.
 */
public class ColorSensor extends EV3Sensor{

  public static final int COLOR_CLASSIFIER_MODE_NONE = -1;
  public static final int COLOR_CLASSIFIER_MODE_ID = 0;
  public static final int COLOR_CLASSIFIER_MODE_YUV = 1;
  public static final int COLOR_CLASSIFIER_MODE_RGB_CUBE = 2;

  Port colorPort = null;
  EV3ColorSensor colorSensor = null;

  ColorClassifier colorClf = null;

  SensorMode colorSensorMode = null;
  SampleProvider colorSensorSP = null;


  float colorSensorSample[] = null;
  int colorSensorSampleInt[] = null;

  int colorDetectionMode = COLOR_CLASSIFIER_MODE_NONE;

  int sampleSize = 0;

  public ColorSensor(String port) {
    EV3 ev3 = (EV3) BrickFinder.getLocal();
    init(ev3, port, COLOR_CLASSIFIER_MODE_YUV);
  }

  public ColorSensor(EV3 ev3, String port) {
    init(ev3, port, COLOR_CLASSIFIER_MODE_YUV);
  }

  public ColorSensor(EV3 ev3, String port, int colorDetectionMode) {
    init(ev3, port, colorDetectionMode);
  }

  private void init(EV3 ev3, String port, int colorDetectionMode) {
    this.colorDetectionMode = colorDetectionMode;

    colorPort = ev3.getPort(port);
    try {
      colorSensor = new EV3ColorSensor(colorPort);
    } catch (Exception e) {
      System.out.println("Port " + port + " is not an ColorSensor");
      System.exit(0);
      colorSensor = null;
    }

    switch (colorDetectionMode) {
      case COLOR_CLASSIFIER_MODE_ID:
        colorSensor.setFloodlight(true);
        colorSensorMode = colorSensor.getColorIDMode();
        sampleSize = 1; //colorSensorSP.sampleSize();
        break;
      case COLOR_CLASSIFIER_MODE_YUV:
        colorSensor.setCurrentMode("RGB");
        colorClf = new ColorClassifierYUV();
        colorClf.loadConfig("/home/robo-hub/color_clf.properties");
        sampleSize = 3; //colorSensorSP.sampleSize();
        break;
    }

    colorSensorSP = (SampleProvider)colorSensorMode;

    colorSensorSample = new float[sampleSize];
    colorSensorSampleInt = new int[sampleSize];
  }

  public int getNextSample() {
    return getNextSample(null);
  }

  public int getNextSample(int[] rgb) {
    colorSensor.fetchSample(colorSensorSample, 0);

    if ( colorDetectionMode == COLOR_CLASSIFIER_MODE_ID ) {
      if ( rgb != null) rgb[0] = (int)colorSensorSample[0];
      return colorSensor.getColorID();
    }

    colorSensorSampleInt[0] = (int)(colorSensorSample[0]*255+0.5);
    colorSensorSampleInt[1] = (int)(colorSensorSample[1]*255+0.5);
    colorSensorSampleInt[2] = (int)(colorSensorSample[2]*255+0.5);

    if ( rgb != null )
      System.arraycopy(colorSensorSampleInt,0,rgb,0,colorSensorSampleInt.length);

    return colorClf.getColorID(colorSensorSampleInt);
  }


}
