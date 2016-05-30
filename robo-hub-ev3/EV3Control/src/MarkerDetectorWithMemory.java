/**
 * Created by robert on 17.05.16.
 */
public class MarkerDetectorWithMemory extends MarkerDetector {
  int detectionHistory[] = null;
  int histIndex = 0;
  int histLength = 0;
  int histSum = 0;
  int histColor = 0;

  public MarkerDetectorWithMemory(int colors[]) {
    super(colors);
    setHistoryLength(4);
  }

  public void setHistoryLength(int l) {
    detectionHistory = new int[l];
    histIndex = 0;
    histLength = l;
    histSum = 0;
  }

  public void updateSensorInput(int colors[]) {
    super.updateSensorInput(colors);
    if (detectMarker) histColor = detectedColor;

    histSum -= detectionHistory[histIndex];
    detectionHistory[histIndex] = detectMarker?1:0;
    histSum += detectionHistory[histIndex];
    histIndex = (histIndex+1)%histLength;
    detectMarker = (histSum >= (histLength/2));
    if (detectMarker) detectedColor = histColor;
  }

}
