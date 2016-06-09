package de.hu_berlin.informatik.ev3.sensor.color;

import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.EV3Sensor;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;



/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 06.04.16
 * Time: 08:42
 * To change this template use File | Settings | File Templates.
 */
public class ColorSensor extends EV3Sensor {

  public static final int COLOR_CLASSIFIER_MODE_NONE = -1;
  public static final int COLOR_CLASSIFIER_MODE_ID = 0;
  public static final int COLOR_CLASSIFIER_MODE_YUV = 1;
  public static final int COLOR_CLASSIFIER_MODE_RGB_CUBE = 2;

  private Port colorPort = null;
  private EV3ColorSensor colorSensor = null;

  private SensorMode colorSensorMode = null;

  private ColorClassifier colorClf = null;

  private float colorSensorSample[] = null;
  private int colorSensorSampleInt[] = null;

  private int colorDetectionMode = COLOR_CLASSIFIER_MODE_NONE;
  private int colorDetectionResult[] = null;

  private int sampleSize = 0;

  public ColorSensor() {};

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

    blackBoardName = "ColorSensor";
    blackBoardID = -1;

    colorPort = ev3.getPort(port);

    try {
      colorSensor = new EV3ColorSensor(colorPort);
    } catch (Exception e) {
      System.out.println("Port " + port + " is not a ColorSensor");
      colorSensor = null;
    }

    switch (colorDetectionMode) {
      case COLOR_CLASSIFIER_MODE_ID:
        colorSensor.setFloodlight(true);
        colorSensorMode = colorSensor.getColorIDMode();
        colorClf = new ColorClassifierMapID();
        sampleSize = 1; //colorSensorSP.sampleSize();
        break;
      case COLOR_CLASSIFIER_MODE_YUV:
        colorSensor.setCurrentMode("RGB");
        colorClf = new ColorClassifierYUV();
        sampleSize = 3; //colorSensorSP.sampleSize();
        break;
    }

    colorSensorSample = new float[sampleSize];
    colorSensorSampleInt = new int[sampleSize];
    colorDetectionResult = new int[1];
  }

  public void configureClassifier(String configfile) {
    if ( colorDetectionMode == COLOR_CLASSIFIER_MODE_YUV )
      colorClf.loadConfig(configfile);
  }

  public void setSensorInput(int rgb[]) {
  }

  public int[] getSensorOutput() {
    colorSensor.fetchSample(colorSensorSample, 0);

    switch (colorDetectionMode) {
      case COLOR_CLASSIFIER_MODE_ID:
        colorSensorSampleInt[0] = (int) (100.0f * colorSensorSample[0]);
        break;
      case COLOR_CLASSIFIER_MODE_YUV:
        colorSensorSampleInt[0] = (int) (colorSensorSample[0] * 255 + 0.5);
        colorSensorSampleInt[1] = (int) (colorSensorSample[1] * 255 + 0.5);
        colorSensorSampleInt[2] = (int) (colorSensorSample[2] * 255 + 0.5);
        break;
    }

    colorDetectionResult[0] = colorClf.getColorID(colorSensorSampleInt);

    return colorDetectionResult;
  }

  public void update(BlackBoard blackBoard) {
    if (blackBoardID == -1)
      blackBoardID = blackBoard.setData(blackBoardName, colorDetectionResult);

    getSensorOutput();
  }

  public void close() {
    colorSensor.setFloodlight(false);
    colorSensor.close();
  }
}
