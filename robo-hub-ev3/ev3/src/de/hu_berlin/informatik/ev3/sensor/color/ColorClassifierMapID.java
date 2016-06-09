package de.hu_berlin.informatik.ev3.sensor.color;

import lejos.robotics.Color;

/**
 * Created by robert on 08.06.16.
 */
public class ColorClassifierMapID extends ColorClassifier {

  protected static int[] colorMap = { Color.NONE, Color.BLACK,
                                      Color.BLUE, Color.GREEN,
                                      Color.YELLOW, Color.RED,
                                      Color.WHITE, Color.BROWN};

  public int getColorID(int c[]) {
    return colorMap[c[0]];
  };

  public Color getColor(int rgb[]) {
    return new Color(rgb[0], rgb[1], rgb[2], getColorID(rgb));
  }

  public void loadConfig( String cfg) {};
}
