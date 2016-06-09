package de.hu_berlin.informatik.ev3.sim;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;

import java.io.IOException;

/**
 * Created by robert on 08.06.16.
 */
public class Keys {

  boolean sim = false;
  lejos.hardware.Keys keys = null;

  public static final int[] keyCodes = { 0x62 /* b */,
                                         0x66 /* f */,
                                         0x67 /* g */,
                                         0x68 /* h */,
                                         0x74 /* t */,
                                         0x1B /* ESC */ };

  public static final int[] ev3KeyCodes = { Button.ID_DOWN,
                                            Button.ID_LEFT,
                                            Button.ID_ENTER,
                                            Button.ID_RIGHT,
                                            Button.ID_UP,
                                            Button.ID_ESCAPE };

  public static final String[] keyNames = { "Down",
                                            "Left",
                                            "Enter",
                                            "Right",
                                            "Up",
                                            "Escape" };

  public Keys() {
    this(false);
  }

  public Keys(boolean sim) {
    this.sim = sim;
    init(sim);
  }

  private void init(boolean sim) {
    if (!sim) {
      EV3 ev3 = (EV3) BrickFinder.getLocal();
      keys = ev3.getKeys();
    }
  }

  public int waitForAnyPress() {
    if (!sim) return keys.waitForAnyPress();
    else {
      int c = -2;
      while (c < 0) {
        c = getPressedKey();
        try {
          Thread.sleep(5);
        } catch (InterruptedException ie) {
          throw new RuntimeException();
        }
      }
      return ev3KeyCodes[c];
    }
  }

  public boolean isButtonDown(String button) {
    if (!sim) return BrickFinder.getDefault().getKey(button).isDown();
    else {
      int c = getPressedKey();
      if (c < 0) return false;
      return keyNames[c].equals(button);
    }
  }

  public boolean isButtonUp(String button) {
    if (!sim) return BrickFinder.getDefault().getKey(button).isUp();
    else {
      int c = getPressedKey();
      if (c < 0) return true;
      return !keyNames[c].equals(button);
    }
  }

  public int getPressedKey() {
    if (sim) {
      int c = -2;

      try {
        KeyConsole.setTerminalToCBreak();

        if (System.in.available() != 0) c = System.in.read();
      } catch (IOException e) {
        System.err.println("IOException");
      } catch (InterruptedException e) {
        System.err.println("InterruptedException");
      } finally {
        try {
          KeyConsole.stty(KeyConsole.ttyConfig.trim());
        } catch (Exception e) {
          System.err.println("Exception restoring tty config");
        }
      }

      if ( c != -2 ) {
        for (int i = 0; i < keyCodes.length; i++)
          if (keyCodes[i] == c) return i;

        return -1;
      }

      return -2;
    }

    return -1;
  }

}
