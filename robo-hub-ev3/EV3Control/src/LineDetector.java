import lejos.hardware.ev3.EV3;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 10.04.16
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class LineDetector {

  public static final int LINE_DETECT_NONE  = 0;
  public static final int LINE_DETECT_RIGHT = 1;
  public static final int LINE_DETECT_LEFT  = 2;
  public static final int LINE_DETECT_BOTH  = 3;

  public static final int RIGHT = 0;
  public static final int LEFT = 1;


  ColorSensorControl rightCS = null;
  ColorSensorControl leftCS = null;

  int[] lastSample = null;
  int lastDetection = LINE_DETECT_NONE;

  int lineColor;

  public LineDetector(EV3 ev3, int lineColor, String rPort, String lPort) {
    this.lineColor = lineColor;

    rightCS = new ColorSensorControl(ev3, rPort);
    leftCS = new ColorSensorControl(ev3, lPort);

    lastSample = new int[2];
  }

  public void updateSensorData() {
    lastSample[RIGHT] = rightCS.getNextSample();
    lastSample[LEFT] = leftCS.getNextSample();

    lastDetection = 0;
    if ( lastSample[RIGHT] == lineColor ) lastDetection = LINE_DETECT_RIGHT;
    if ( lastSample[LEFT] == lineColor ) lastDetection += LINE_DETECT_LEFT;
  }

  public int getSensorData(int senorData[]) {
    if ( senorData != null ) {
      senorData[RIGHT] = lastSample[RIGHT];
      senorData[RIGHT] = lastSample[LEFT];
    }
    return lastDetection;
  }

  public int getSensorData() {
    return getSensorData(null);
  }
}
