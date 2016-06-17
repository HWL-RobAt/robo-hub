package de.hu_berlin.informatik.robokart.robo;

import de.hu_berlin.informatik.app.connection.ConnectionInfo;
import de.hu_berlin.informatik.app.connection.SocketConnection;
import de.hu_berlin.informatik.app.connection.protocol.EV3Command;
import de.hu_berlin.informatik.ev3.ai.Braitenberg;
import de.hu_berlin.informatik.ev3.ai.BraitenbergLine;
import de.hu_berlin.informatik.ev3.control.motion.MotionControl;
import de.hu_berlin.informatik.ev3.control.motion.MotionControlBraitenbergMultiSpeed;
import de.hu_berlin.informatik.ev3.control.motion.MotionControlRemote;
import de.hu_berlin.informatik.ev3.control.motor.DualMotorControl;
import de.hu_berlin.informatik.ev3.control.motor.MotorControl;
import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.sensor.color.ColorSensor;
import de.hu_berlin.informatik.ev3.sensor.color.ColorSensorMulti;
import de.hu_berlin.informatik.ev3.sensor.detector.LineDetector;
import de.hu_berlin.informatik.ev3.sensor.detector.MarkerDetector;
import de.hu_berlin.informatik.ev3.sensor.detector.MarkerDetectorColorList;
import de.hu_berlin.informatik.ev3.sensor.remote.RemoteMotionSensor2D;
import de.hu_berlin.informatik.ev3.sim.ColorSensorSim;
import de.hu_berlin.informatik.ev3.sim.DualMotorControlSim;
import de.hu_berlin.informatik.ev3.sim.Keys;
import de.hu_berlin.informatik.ev3.sim.LCD;
import lejos.hardware.Button;
import lejos.robotics.Color;

import java.io.PrintWriter;

/**
 * Created by robert on 18.06.16.
 */
public class RoboKart {

  public static final int ROBO_MODE_LINE = 0;
  public static final int ROBO_MODE_MARKER = 1;

  public static final int ROBO_MODE_CTRL_GYRO = 0;
  public static final int ROBO_MODE_CTRL_TOUCH = 1;
  public static final int ROBO_MODE_CTRL_LINE = 2;
  public static final int ROBO_MODE_CTRL_DIST = 3;

  public static final int ROBO_TOUR_ALEXANDER = 0;
  public static final int ROBO_TOUR_TEST = 1;


  public static void main(String[] args) {

    boolean running = false;
    boolean simMode = false;

    int defaultHighSpeed = 300;
    int defaultLowSpeed = 200;
    int defaultStartSpeed = defaultHighSpeed;

    int frequency = 50;

    int markerColor[] = null;
    int lineColor = Color.NONE;

    int tour = ROBO_TOUR_ALEXANDER;
    String configfilePrefix = "/home/robo-hub/";

    LCD lcd = new LCD(simMode);
    Keys keys = new Keys(simMode);

    lcd.drawString("Alex: Up Indoor: Down", 1, 2);

    int pressedKey = keys.waitForAnyPress();

    if ( pressedKey == Button.ID_DOWN ) tour = ROBO_TOUR_TEST;

    lcd.clear(2);
    lcd.drawString("ColorTab: U/D/L/R", 1, 2);

    pressedKey = keys.waitForAnyPress();

    String colortabPost = "";

    if ( pressedKey == Button.ID_DOWN ) colortabPost = "_1";
    if ( pressedKey == Button.ID_LEFT ) colortabPost = "_2";
    if ( pressedKey == Button.ID_RIGHT ) colortabPost = "_3";

    if ( tour == ROBO_TOUR_ALEXANDER ) {

      int tour_markerColor[] = { Color.RED, Color.GREEN,
                                 Color.RED, Color.GREEN,
                                 Color.RED, Color.BLACK };

      markerColor = tour_markerColor;

      lineColor = Color.WHITE;

      configfilePrefix = configfilePrefix + "alexander/";

    } else {
      int tour_markerColor[] = { Color.BLACK, Color.GREEN,
                                 Color.BLACK, Color.GREEN,
                                 Color.BLACK, Color.GREEN,
                                 Color.RED};

      markerColor = tour_markerColor;

      lineColor = Color.ORANGE;

      configfilePrefix = configfilePrefix + "test/";
    }

    lcd.clear(2);

    int roboMoveMode = ROBO_MODE_CTRL_LINE;

    BlackBoard blackBoard = new BlackBoard();

    String colorSensorPorts[] = {"S1", "S2"};
    ColorSensor colSensorList[] = new ColorSensor[colorSensorPorts.length];

    for ( int i = 0; i < colSensorList.length; i++) {
      if (simMode) colSensorList[i] = new ColorSensorSim(colorSensorPorts[i]);
      else {
        colSensorList[i] = new ColorSensor(colorSensorPorts[i]);
        colSensorList[i].configureClassifier(configfilePrefix + "color_clf" + colortabPost + ".properties");
      }
    }

    ColorSensorMulti colorSensors = new ColorSensorMulti();

    LineDetector lineDetector = new LineDetector(lineColor);
    Braitenberg braitenberg = new BraitenbergLine();

    MarkerDetector markerDetect = new MarkerDetectorColorList(markerColor);

    RemoteMotionSensor2D remoteSensor = new RemoteMotionSensor2D(null);

    MotionControl motionCtrlBB = new MotionControlBraitenbergMultiSpeed();
    MotionControl motionCtrlRemote = new MotionControlRemote();
    MotionControl motionCtrl = null;


    MotorControl roboMotorCtl = null;
    if (simMode) roboMotorCtl = new DualMotorControlSim("MotorPort.A", "MotorPort.B");
    else roboMotorCtl = new DualMotorControl("MotorPort.A", "MotorPort.B");

    PrintWriter debugRS = null;
    /*try {
      debugRS = new PrintWriter("/home/robo-hub/remote_sender.dbg");
      if (debugRS != null) {
        remoteSensor.setDebugWriter(debugRS);
        motionCtrlRemote.setDebugWriter(debugRS);
      }
    } catch (IOException ioe)  {
      throw new RuntimeException();
    } */

    int mode = ROBO_MODE_LINE;

    /*
     * Communication
     */
    //ConnectionInfo ci = new ConnectionInfo("server.properties");
    ConnectionInfo ci = new ConnectionInfo("0.0.0.0", 2000);
    SocketConnection sock = new SocketConnection(ci);
    sock.init();

    lcd.drawString("Wait for Client", 1, 2);
    //ev3.getAudio().setVolume(15);
    //ev3.getAudio().systemSound(0);

    sock.waitForClient();
    //ev3.getAudio().systemSound(1);

    lcd.clear(2);

    int rxTxInt = 0;
    int params[] = {0, 0, 0};
    int command = -1;

    do {

      lcd.drawString("Ready for next Quiz", 1, 2);

      do {
        command = EV3Command.COMMAND_NONE;
        if (sock.rxAvailable()) {
          rxTxInt = sock.receiveInt();
          command = EV3Command.decode(rxTxInt, params);
        } else {
          if (keys.isButtonDown("Escape")) {
            command = EV3Command.COMMAND_DISCONNECT;
          } else {
            try {
              Thread.sleep(5);
            } catch (InterruptedException ie) {
            }
          }
        }
      } while ((command != EV3Command.COMMAND_START) && (command != EV3Command.COMMAND_DISCONNECT));

      lcd.clear(2);
      //ev3.getAudio().systemSound(3);

      if (command == EV3Command.COMMAND_START) {
        roboMoveMode = params[0];

        lcd.drawString("Mode: " + roboMoveMode, 1, 1);

        switch (roboMoveMode) {
          case ROBO_MODE_CTRL_LINE:
            motionCtrl = motionCtrlBB;
            break;
          default:
            motionCtrl = motionCtrlRemote;
        }

        motionCtrl.setMaxSpeed(defaultStartSpeed);
        remoteSensor.setDataInputStream(sock);

        running = true;
      } else {
        if (command == EV3Command.COMMAND_DISCONNECT) {
          running = false;
          sock.close();
        } else {
          running = false;
        }
      }

      while (running) {
        for ( int i = 0; i < colSensorList.length; i++) colSensorList[i].update(blackBoard);

        remoteSensor.update(blackBoard);

        colorSensors.update(blackBoard);
        markerDetect.update(blackBoard);

        lineDetector.update(blackBoard);
        braitenberg.update(blackBoard);

        motionCtrlRemote.update(blackBoard);
        motionCtrlBB.update(blackBoard);

        int colors[] = colorSensors.getSensorOutput();
        lcd.clear(5);
        lcd.drawString("Color: " + colors[0] + " / " + colors[1], 1, 5);

        if ((mode == ROBO_MODE_LINE) && markerDetect.hasMarkerDetected()) {
          mode = ROBO_MODE_MARKER;
          int markerCol = markerDetect.getDetectedMarker();

          lcd.clear(6);
          lcd.drawString("Marker: " + markerCol + "", 1, 6);

          roboMotorCtl.backward(10);

          try {
            Thread.sleep((long)(1000 / frequency));
          } catch (InterruptedException ie) {
            throw new RuntimeException();
          }

          roboMotorCtl.stop();

          if (markerCol == markerColor[markerColor.length-1]) {
            //ev3.getAudio().systemSound(3);

            rxTxInt = EV3Command.encode(EV3Command.COMMAND_STOP);
            sock.sendInt(rxTxInt);

            running = false;
          } else {
            //ev3.getAudio().systemSound(4);
            rxTxInt = EV3Command.encode(EV3Command.COMMAND_QUESTION);
            sock.sendInt(rxTxInt);

            do {
              command = EV3Command.COMMAND_NONE;
              if (sock.rxAvailable()) {
                rxTxInt = sock.receiveInt();
                command = EV3Command.decode(rxTxInt, params);
              } else {
                if (keys.isButtonDown("Escape")) {
                  command = EV3Command.COMMAND_STOP;
                } else {
                  try {
                    Thread.sleep(5);
                  } catch (InterruptedException ie) {
                  }
                }
              }
            } while ((command != EV3Command.COMMAND_ANSWER) && (command != EV3Command.COMMAND_STOP));

            switch (command) {
              case EV3Command.COMMAND_ANSWER: {
                if (params[0] == 1) motionCtrl.setMaxSpeed(defaultHighSpeed);
                if (params[0] == 2) motionCtrl.setMaxSpeed(defaultLowSpeed);
                break;
              }
              case EV3Command.COMMAND_STOP: {
                running = false;
                break;
              }
            }
          }

          lcd.clear(6);
          motionCtrl.reset();

          if (running == false) break;

        } else {
          if (!markerDetect.hasMarkerDetected()) {
            mode = ROBO_MODE_LINE;
            lcd.clear(6);
          }
        }

        if (sock.rxAvailable()) {
          rxTxInt = sock.receiveInt();
          command = EV3Command.decode(rxTxInt, params);

          switch (command) {
            case EV3Command.COMMAND_STOP:
              running = false;
              break;
            default:
              sock.returnReceivedInt(rxTxInt);
          }
        }

        if (running == false) break;

        int speed[] = motionCtrl.getSpeed();

        roboMotorCtl.setSpeed(speed);

        lcd.clear(2);
        lcd.clear(3);
        lcd.clear(4);
        lcd.clear(7);

        lcd.drawString("Speed: " + speed[0] + " / " + speed[1], 1, 2);
        lcd.drawString("Remote: " + motionCtrlRemote.getSpeed()[0] + " " + motionCtrlRemote.getSpeed()[1], 1, 3);
        lcd.drawString("Line: " + lineDetector.getSensorOutput()[0] + "", 1, 3);
        lcd.drawString("Mode: " + mode + "", 1, 4);
        lcd.drawString("Marker: " + markerDetect.getNextColor() + "", 1, 7);

        try {
          Thread.sleep((long)(1000 / frequency));
        } catch (InterruptedException ie) {
          throw new RuntimeException();
        }

        running = running && keys.isButtonUp("Escape") && (!sock.isClosed());
      }

      roboMotorCtl.stop();
      lcd.clear();

    } while (!sock.isClosed());

    for ( int i = 0; i < colSensorList.length; i++) colSensorList[i].close();
    if ( debugRS != null ) debugRS.close();
  }

}
