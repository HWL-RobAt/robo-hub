package de.hu_berlin.informatik.ev3.sensorcontrol;

import lejos.robotics.Color;

/**
 * Created by robert on 01.06.16.
 */
public abstract class ColorClassifier {
  public abstract int getColorID(int rbg[]);
  public abstract Color getColor(int rbg[]);
  public abstract void loadConfig( String cfg);

  static String colorNames[] = {"NONE", //-1
                         "RED", "GREEN", "BLUE", "YELLOW", "MAGENTA", //0-4
                         "ORANGE", "WHITE", "BLACK", "PINK", "GRAY",  //5-9
                         "LIGHT_GRAY", "DARK_GRAY", "CYAN", "BROWN" };//10-13

  public static String colorToString(int c) {
    return colorNames[(c<(colorNames.length-1))?(c+1):0];
  }
}
