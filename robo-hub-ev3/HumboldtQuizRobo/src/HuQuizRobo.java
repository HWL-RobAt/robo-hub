import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.robotics.Color;
import lejos.utility.Delay;

import java.io.IOException;
import java.io.PrintWriter;

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

    boolean running = false;

    int defaultHighSpeed=300;
    int defaultLowSpeed=200;
    int defaultStartSpeed=defaultHighSpeed;

    int frequency = 50;

/*    int markerColor[] = { Color.BLACK, Color.GREEN,
                          Color.BLACK, Color.GREEN,
                          Color.BLACK, Color.GREEN,
                          Color.RED };

    int lineColor = Color.ORANGE;*/
    int markerColor[] = { Color.RED, Color.GREEN,
                          Color.RED, Color.GREEN,
                          Color.RED, Color.BLACK };

    int lineColor = Color.WHITE;

    int roboMoveMode = ROBO_MODE_CTRL_LINE;

    EV3 ev3 = (EV3) BrickFinder.getLocal();
    TextLCD lcd = ev3.getTextLCD();
    Keys keys = ev3.getKeys();

    SensorDB sensorDB = new SensorDB();

    String colorSensorPorts[] = {"S1", "S2"};
    ColorSensorMulti colorSensors = new ColorSensorMulti(colorSensorPorts);

    LineDetector lineDetector = new LineDetector(lineColor);

    //MarkerDetector markerDetect = new MarkerDetector(markerColor);
    //MarkerDetector markerDetect = new MarkerDetectorWithMemory(markerColor);
    MarkerDetector markerDetect = new MarkerDetectorColorList(markerColor);

    PrintWriter debugRS = null;
    /*try {
      debugRS = new PrintWriter("/home/robo-hub/remote_sender.dbg");
    } catch (IOException ioe) {
      throw new RuntimeException();
    }
    */

    RemoteSensor remoteSensor = new RemoteSensor(null);
    if (debugRS != null) remoteSensor.setDebugWriter(debugRS);

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
    ev3.getAudio().setVolume(15);
    ev3.getAudio().systemSound(0);

    sock.waitForClient();
    ev3.getAudio().systemSound(1);

    lcd.clear(2);

    int rxTxInt = 0;
    int params[] = {0, 0, 0};
    int command = -1;

    do {

      lcd.drawString("Ready for next Quiz", 1, 2);

      do {
        command = AppCommand.COMMAND_NONE;
        if (sock.rxAvailable()) {
          rxTxInt = sock.receiveInt();
          command = AppCommand.decode(rxTxInt, params);
        } else {
          if (Button.ESCAPE.isDown()) {
            command = AppCommand.COMMAND_DISCONNECT;
          } else {
            try {
              Thread.sleep(5);
            } catch (InterruptedException ie) {
            }
          }
        }
      } while ((command != AppCommand.COMMAND_START) && (command != AppCommand.COMMAND_DISCONNECT));

      lcd.clear(2);
      ev3.getAudio().systemSound(3);

      if (command == AppCommand.COMMAND_START) {
        roboMoveMode = params[0];

        lcd.drawString("Mode: " + roboMoveMode, 1, 1);

        if (roboMoveMode == ROBO_MODE_CTRL_LINE) {
          moveCtrl = new MoveControlBraitenbergLine();
        } else {
          moveCtrl = new MoveControlRemote();
        }

        if (debugRS != null) moveCtrl.setDebugWriter(debugRS);

        moveCtrl.setMaxSpeed(defaultStartSpeed);

        if (remoteSensor != null) remoteSensor.setDataInputStream(sock);

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

        int colors[] = colorSensors.readLastSensorsValues();
        lcd.clear(5);
        lcd.drawString("Color: " + colors[0] + " / " + colors[1], 1, 5);

        if ((mode == ROBO_MODE_LINE) && markerDetect.hasMarkerDetected()) {
          mode = ROBO_MODE_MARKER;
          int markerCol = markerDetect.getDetectedMarker();

          lcd.clear(6);
          lcd.drawString("Marker: " + markerCol + "", 1, 6);

          roboMotorCtl.backward(10);

          Delay.msDelay((long) (1000 / frequency));

          roboMotorCtl.stop();

          if (markerCol == markerColor[markerColor.length-1]) {
            ev3.getAudio().systemSound(3);

            rxTxInt = AppCommand.encode(AppCommand.COMMAND_STOP);
            sock.sendInt(rxTxInt);

            running = false;
          } else {
            ev3.getAudio().systemSound(4);
            rxTxInt = AppCommand.encode(AppCommand.COMMAND_QUESTION);
            sock.sendInt(rxTxInt);

            do {
              command = AppCommand.COMMAND_NONE;
              if (sock.rxAvailable()) {
                rxTxInt = sock.receiveInt();
                command = AppCommand.decode(rxTxInt, params);
              } else {
                if (Button.ESCAPE.isDown()) {
                  command = AppCommand.COMMAND_STOP;
                } else {
                  try {
                    Thread.sleep(5);
                  } catch (InterruptedException ie) {
                  }
                }
              }
            } while ((command != AppCommand.COMMAND_ANSWER) && (command != AppCommand.COMMAND_STOP));

            switch (command) {
              case AppCommand.COMMAND_ANSWER: {
                if (params[0] == 1) moveCtrl.setMaxSpeed(defaultHighSpeed);
                if (params[0] == 2) moveCtrl.setMaxSpeed(defaultLowSpeed);
                break;
              }
              case AppCommand.COMMAND_STOP: {
                running = false;
                break;
              }
            }
          }

          lcd.clear(6);
          moveCtrl.reset();
          remoteSensor.clear();

          if (running == false) break;

        } else {
          if (!markerDetect.hasMarkerDetected()) {
            mode = ROBO_MODE_LINE;
            lcd.clear(6);
          }
        }

        if (sock.rxAvailable()) {
          rxTxInt = sock.receiveInt();
          command = AppCommand.decode(rxTxInt, params);

          switch (command) {
            case AppCommand.COMMAND_STOP:
              running = false;
              break;
            default:
              sock.returnReceivedInt(rxTxInt);
          }
        }

        if (running == false) break;

        int moveCtrlInput[] = null;

        if (roboMoveMode == ROBO_MODE_CTRL_LINE) {
          moveCtrlInput = lineDetector.getSensorOutputs();
        } else {
          moveCtrlInput = remoteSensor.getSensorOutputs();
        }

        moveCtrl.updateSensorInputs(moveCtrlInput);
        int speed[] = moveCtrl.getNextSpeed();

        if ( debugRS != null ) debugRS.println("ToMotor:" + speed[0] + "," + speed[1]);

        if ((speed[0] == 0) && (speed[1] == 0)) roboMotorCtl.setSpeed(1,1);
        else                                    roboMotorCtl.setSpeed(speed[0], speed[1]);

        lcd.clear(2);
        lcd.clear(3);
        lcd.clear(4);
        lcd.drawString("Speed: " + speed[0] + " / " + speed[1], 1, 2);
        if (moveCtrlInput.length > 1) {
          lcd.drawString("Remote: " + moveCtrlInput[0] + " " + moveCtrlInput[1], 1, 3);
        } else {
          lcd.drawString("Line: " + moveCtrlInput[0] + "", 1, 3);
        }
        lcd.drawString("Mode: " + mode + "", 1, 4);

        lcd.clear(7);
        lcd.drawString("Marker: " + markerDetect.getNextColor() + "", 1, 7);

        Delay.msDelay((long) (1000 / frequency));

        if ((speed[0] == 0) && (speed[1] == 0)) roboMotorCtl.stop();

        running = running && Button.ESCAPE.isUp();
      }

      roboMotorCtl.stop();
      lcd.clear();

    } while (!sock.isClosed());

    colorSensors.stop();
    if ( debugRS != null ) debugRS.close();
  }

}
