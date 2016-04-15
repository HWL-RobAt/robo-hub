import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 10.04.16
 * Time: 22:36
 * To change this template use File | Settings | File Templates.
 */
public class HuQuizRobo {

  public static final int ROBO_MODE_LINE = 0;
  public static final int ROBO_MODE_MARKER = 1;

  static final int[][] speedLookup = {{20, 40, 100, 160, 180}, {30, 50, 150, 250, 270}, {30, 50, 200, 350, 370}};

  public static void main(String[] args) {

    int frequency = 25;
    int markerColor[] = {Color.YELLOW, Color.GREEN};

    EV3 ev3 = (EV3) BrickFinder.getLocal();
    TextLCD lcd = ev3.getTextLCD();

    LineDetectorWithMarker lineDetect = new LineDetectorWithMarker(ev3, Color.BLUE, "S4", "S2");
    lineDetect.setMarker(markerColor);

    BraitenbergLine bbLine = new BraitenbergLine();
    Keys keys = ev3.getKeys();

    RegulatedMotor motorR = new EV3LargeRegulatedMotor(MotorPort.A);
    RegulatedMotor motorL = new EV3LargeRegulatedMotor(MotorPort.B);

    int speedIndex = 0;
    int curveIndexR = 2;
    int curveIndexL = 2;

    int rightSpeed = speedLookup[speedIndex][curveIndexR];
    int leftSpeed =  speedLookup[speedIndex][curveIndexL];

    int mode = ROBO_MODE_LINE;

    while ( Button.ESCAPE.isUp()) {
      lineDetect.updateSensorData();

      if ((mode == ROBO_MODE_LINE) && lineDetect.hasMarkerDetected()) {
        mode = ROBO_MODE_MARKER;
        int markerCol = lineDetect.getDetectedMarker();
        lcd.clear(6);
        lcd.drawString("Marker: " + markerCol + "", 1, 6);

        motorR.setSpeed(10);
        motorL.setSpeed(10);
        motorL.backward();
        motorR.backward();

        Delay.msDelay((long) (1000/frequency));

        motorR.stop();
        motorL.stop();

        keys.waitForAnyPress();

        lcd.clear(6);
        speedIndex = 0;
        curveIndexL = 2;
        curveIndexR = 2;
      } else {
        if (!lineDetect.hasMarkerDetected()) {
          mode = ROBO_MODE_LINE;
          lcd.clear(6);
        }
      }

      int lineDetectValue = lineDetect.getSensorData();
      int action = bbLine.nextAction(lineDetectValue);

      /*
       * adjust speed
       */

      if (action == BraitenbergLine.BRAITENBERG_MOVE_NONE) {
        speedIndex = Math.min(speedIndex+1, speedLookup.length);
      } else {
        speedIndex = Math.max(speedIndex-1, 0);
      }

      if (action == BraitenbergLine.BRAITENBERG_MOVE_RIGHT) {
        curveIndexR = Math.max( curveIndexR-1, 0);
        curveIndexL = Math.min( curveIndexL+1, speedLookup[0].length );

      }

      if (action == BraitenbergLine.BRAITENBERG_MOVE_LEFT) {
        curveIndexL = Math.max( curveIndexL-1, 0);
        curveIndexR = Math.min( curveIndexR+1, speedLookup[0].length );
      }

      rightSpeed = speedLookup[speedIndex][curveIndexR];
      leftSpeed = speedLookup[speedIndex][curveIndexL];
      motorR.setSpeed(rightSpeed);
      motorL.setSpeed(leftSpeed);

      motorR.forward();
      motorL.forward();

      lcd.clear(3);
      lcd.clear(4);
      lcd.clear(5);
      lcd.drawString("Speed: " + leftSpeed + " / " + rightSpeed, 1, 3);
      lcd.drawString("Line: " + lineDetectValue + "", 1, 4);
      lcd.drawString("Action: " + action + "", 1, 5);



      Delay.msDelay((long) (1000/frequency));
    }

    motorR.stop();
    motorL.stop();
  }
}
