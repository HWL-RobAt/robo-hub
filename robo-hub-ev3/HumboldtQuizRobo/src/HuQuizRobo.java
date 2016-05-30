import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.robotics.Color;
import lejos.utility.Delay;

import static lejos.hardware.ev3.LocalEV3.ev3;

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

  public static final int ROBO_MODE_CTRL_GYRO = 0;
  public static final int ROBO_MODE_CTRL_TOUCH = 1;
  public static final int ROBO_MODE_CTRL_LINE = 2;
  public static final int ROBO_MODE_CTRL_DIST = 3;


  public static void main(String[] args) {

    boolean running = true;

    int frequency = 100;
    //int markerColor[] = {Color.YELLOW, Color.GREEN};
    //int lineColor = Color.BLUE;
    int markerColor[] = {Color.GREEN, Color.BLACK};
    int lineColor = Color.RED;

    int roboMoveMode = ROBO_MODE_CTRL_LINE;

    EV3 ev3 = (EV3) BrickFinder.getLocal();
    TextLCD lcd = ev3.getTextLCD();
    Keys keys = ev3.getKeys();

    SensorDB sensorDB = new SensorDB();

    String colorSensorPorts[] = {"S2", "S4"};
    ColorSensorMulti colorSensors = new ColorSensorMulti(colorSensorPorts);

    LineDetector lineDetector = new LineDetector(lineColor);

    //MarkerDetector markerDetect = new MarkerDetector(markerColor);
    MarkerDetector markerDetect = new MarkerDetectorWithMemory(markerColor);

    RemoteSensor remoteSensor = new RemoteSensor(null);

    MoveControl moveCtrl = null;

    DualMotorControl roboMotorCtl = new DualMotorControl("MotorPort.A", "MotorPort.B");

    int mode = ROBO_MODE_LINE;

    /*
     * Communication
     */
    //ConnectionInfo ci = new ConnectionInfo("server.properties");
    ConnectionInfo ci = new ConnectionInfo("0.0.0.0", 2000);
    SocketConnector sock = new SocketConnector(ci);
    sock.init();

    lcd.drawString("Wait for Client", 1, 2);

    sock.waitForClient();

    lcd.clear(2);

    int rxTxInt = 0;
    int params[] = {0, 0, 0};
    int command = -1;

    do {

      lcd.drawString("Ready for next Quiz", 1, 2);

      do {
        rxTxInt = sock.receiveInt();
        command = AppCommand.decode(rxTxInt, params);
      } while ((command != AppCommand.COMMAND_START) && (command != AppCommand.COMMAND_DISCONNECT));

      lcd.clear(2);

      if (command == AppCommand.COMMAND_START) {
        roboMoveMode = params[0];

        lcd.drawString("Mode: " + roboMoveMode, 1, 1);

        if (roboMoveMode == ROBO_MODE_CTRL_LINE) {
          moveCtrl = new MoveControlBraitenbergLine();
        } else {
          moveCtrl = new MoveControlRemote();
        }

        moveCtrl.setDefaultSpeed(100);
        moveCtrl.setMaxSpeed(200);

        if (remoteSensor != null) {
          remoteSensor.setDataInputStream(sock);
        }

        running = true;
      } else {
        if (command == AppCommand.COMMAND_DISCONNECT) {
          running = false;
          sock.close();
        } else {
          running = false;
        }
      }

      while (running) {
        colorSensors.updateSensorDB(sensorDB);
        markerDetect.updateSensorDB(sensorDB);

        lineDetector.updateSensorDB(sensorDB);
        remoteSensor.updateSensorDB(sensorDB);

        if ((mode == ROBO_MODE_LINE) && markerDetect.hasMarkerDetected()) {
          mode = ROBO_MODE_MARKER;
          int markerCol = markerDetect.getDetectedMarker();

          lcd.clear(6);
          lcd.drawString("Marker: " + markerCol + "", 1, 6);

          roboMotorCtl.backward(10);

          Delay.msDelay((long) (1000 / frequency));

          roboMotorCtl.stop();

          rxTxInt = AppCommand.encode(AppCommand.COMMAND_QUESTION);
          sock.sendInt(rxTxInt);

          do {
            rxTxInt = sock.receiveInt();
            command = AppCommand.decode(rxTxInt, params);
          } while ((command != AppCommand.COMMAND_ANSWER) && (command != AppCommand.COMMAND_STOP));

          switch (command) {
            case AppCommand.COMMAND_ANSWER: {
              moveCtrl.reset();
              if (params[0] == 1) moveCtrl.setDefaultSpeed(200);
              if (params[0] == 2) moveCtrl.setDefaultSpeed(300);
              break;
            }
            case AppCommand.COMMAND_STOP: {
              running = false;
              break;
            }
          }
          //keys.waitForAnyPress();

          lcd.clear(6);
          moveCtrl.reset();

        } else {
          if (!markerDetect.hasMarkerDetected()) {
            mode = ROBO_MODE_LINE;
            lcd.clear(6);
          }
        }

        int moveCtrlInput[] = null;

        if (roboMoveMode == ROBO_MODE_CTRL_LINE) {
          moveCtrlInput = lineDetector.getSensorOutputs();
        } else {
          moveCtrlInput = remoteSensor.getSensorOutputs();
        }

        moveCtrl.updateSensorInputs(moveCtrlInput);
        int speed[] = moveCtrl.getNextSpeed();

        roboMotorCtl.setSpeed(speed[0], speed[1]);

        lcd.clear(2);
        lcd.clear(3);
        lcd.clear(4);
        lcd.drawString("Speed: " + speed[0] + " / " + speed[1], 1, 2);
        if (moveCtrlInput.length > 1) {
          lcd.drawString("Line: " + moveCtrlInput[0] + " " + moveCtrlInput[1], 1, 3);
        } else {
          lcd.drawString("Line: " + moveCtrlInput[0] + "", 1, 3);
        }
        lcd.drawString("Mode: " + mode + "", 1, 4);

        Delay.msDelay((long) (1000 / frequency));

        running = running && Button.ESCAPE.isUp();
      }

      roboMotorCtl.stop();

    } while (!sock.isClosed());
  }

}
