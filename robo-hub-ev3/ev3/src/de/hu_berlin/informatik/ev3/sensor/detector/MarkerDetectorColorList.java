package de.hu_berlin.informatik.ev3.sensor.detector;

/**
 * Created by robert on 02.06.16.
 */
public class MarkerDetectorColorList extends MarkerDetectorWithMemory  {

  int markerColorList[] = null;
  int markerColorListIndex = 0;

  public MarkerDetectorColorList(int colors[]) {
    super(colors);
    markerColorList = colors;

    reset();
  }

  private void setNextColor() {
    markerColors.clear();
    markerColors.add(markerColorList[markerColorListIndex]);
    markerColorListIndex = (markerColorListIndex+1) % markerColorList.length;
  }

  public void reset() {
    markerColorListIndex = 0;
    setNextColor();
  }

  public void setSensorInput(int colors[]) {
    detectMarker = false;
    super.setSensorInput(colors);

    if (detectMarker) {
      setNextColor();
      super.reset();  //TODO: reset to avoid re-detection of marker. Refactor and rm usage of reset!!
    }
  }
}
