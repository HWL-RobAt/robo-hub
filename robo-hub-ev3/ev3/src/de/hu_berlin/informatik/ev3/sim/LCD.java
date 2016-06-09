package de.hu_berlin.informatik.ev3.sim;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;

/**
 * Created by robert on 08.06.16.
 */
public class LCD {

  boolean sim = false;
  TextLCD lcd = null;

  String display[] = null;

  public LCD() {
    init(false);
  }

  public LCD(boolean sim) {
    init(sim);
  }

  private void init(boolean sim) {
    this.sim = sim;
    if (!sim) {
      EV3 ev3 = (EV3) BrickFinder.getLocal();
      lcd = ev3.getTextLCD();
    } else {
      display = new String[10];
    }
  }


  public void drawString(String text, int x, int y) {
    if (!sim) lcd.drawString(text, x, y);
    else {
      display[y] = text;
      System.out.println(text);
    }
  }

  public void clear() {
    if (!sim) lcd.clear();
    else {
      for ( int i = 0; i < display.length; i++)
        display[i] = null;
    }
  }

  public void clear(int i) {
    if (!sim) lcd.clear(i);
    else display[i] = null;
  }
}
