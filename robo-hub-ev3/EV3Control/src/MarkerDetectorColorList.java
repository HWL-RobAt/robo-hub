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
  }

  private void setNextColor() {
    markerColors.clear();
    markerColors.add(markerColorList[markerColorListIndex]);
    markerColorListIndex++;
  }

  public void reset() {
    markerColorListIndex = 0;
  }

  public boolean isLastMarker {

  }


  else {
          if ( colors[i] == markerColorList[markerColorListIndex] ) {
             markerColorListIndex++;
             detectedColor = colors[i];
             detectMarker = true;
          }

}
