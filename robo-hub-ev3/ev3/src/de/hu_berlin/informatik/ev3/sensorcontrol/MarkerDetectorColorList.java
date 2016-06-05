package de.hu_berlin.informatik.ev3.sensorcontrol;

/**
 * Created by robert on 02.06.16.
 */
public class MarkerDetectorColorList extends MarkerDetectorWithMemory  {

  int markerColorList[] = null;
  int markerColorListIndex = 0;

  public MarkerDetectorColorList(int colors[]) {
    super(colors);
    markerColorList = colors;
    markerColorListIndex = 0;
    setNextColor();
  }

  private void setNextColor() {
    markerColors.clear();
    markerColors.add(markerColorList[markerColorListIndex]);
    markerColorListIndex = (markerColorListIndex+1) % markerColorList.length;
  }

  public void reset() {
    markerColorListIndex = 0;
  }

  public void updateSensorInput(int colors[]) {
    detectMarker = false;
    super.updateSensorInput(colors);

    if (detectMarker) {
      setNextColor();
      super.reset();
    }
  }
}
