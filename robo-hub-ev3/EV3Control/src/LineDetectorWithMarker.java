import lejos.hardware.ev3.EV3;
import lejos.robotics.Color;
import sun.security.util.Resources_it;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 13.04.16
 * Time: 19:59
 * To change this template use File | Settings | File Templates.
 */
public class LineDetectorWithMarker extends LineDetector {

  HashSet<Integer> markerColors = null;

  boolean markerDetected[] = {false, false};
  int detectedMarkerColor[] = {Color.NONE, Color.NONE};

  public LineDetectorWithMarker(EV3 ev3, int lineColor, String rPort, String lPort) {
    super(ev3, lineColor, rPort, lPort);
    markerColors = new HashSet<Integer>();
  }

  public void setMarker(int colors[]) {
    markerColors.clear();
    for( int i = 0; i < colors.length; i++)
      markerColors.add(colors[i]);
  }

  public void updateSensorData() {
    super.updateSensorData();
    markerDetected[RIGHT] = markerColors.contains(lastSample[RIGHT]);
    markerDetected[LEFT] = markerColors.contains(lastSample[LEFT]);

    if (markerDetected[RIGHT]) detectedMarkerColor[RIGHT] = lastSample[RIGHT];
    if (markerDetected[LEFT]) detectedMarkerColor[LEFT] = lastSample[LEFT];
  }

  public boolean hasMarkerDetected() {
    return (markerDetected[RIGHT]||markerDetected[LEFT]);
  }

  public int getDetectedMarker() {
    if (markerDetected[RIGHT]) return detectedMarkerColor[RIGHT];
    if (markerDetected[LEFT]) return detectedMarkerColor[LEFT];
    return Color.NONE;
  }

}

