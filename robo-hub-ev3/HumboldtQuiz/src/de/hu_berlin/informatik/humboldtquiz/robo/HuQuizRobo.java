package de.hu_berlin.informatik.humboldtquiz.robo;

import de.hu_berlin.informatik.app.connection.ConnectionInfo;
import de.hu_berlin.informatik.app.connection.SocketConnection;
import de.hu_berlin.informatik.app.connection.protocol.EV3Command;
import de.hu_berlin.informatik.ev3.ai.Braitenberg;
import de.hu_berlin.informatik.ev3.control.motion.MotionControl;
import de.hu_berlin.informatik.ev3.ai.BraitenbergLine;
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
import de.hu_berlin.informatik.ev3.sim.*;
import lejos.hardware.Button;
import lejos.robotics.Color;

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

  public static final int ROBO_MODE_MOVE = 0;
  public static final int ROBO_MODE_MARKER = 1;

  public static final int ROBO_MODE_CTRL_GYRO = 0;
  public static final int ROBO_MODE_CTRL_TOUCH = 1;
  public static final int ROBO_MODE_CTRL_LINE = 2;
  public static final int ROBO_MODE_CTRL_DIST = 3;

  public static final int ROBO_TOUR_ALEXANDER = 0;
  public static final int ROBO_TOUR_TEST = 1;


  public static void main(String[] args) {

    boolean running;
    boolean closeApp = false;

    int defaultHighSpeed = 300;
    int defaultLowSpeed = 200;
    int defaultStartSpeed = defaultHighSpeed;

    int frequency = 50;

    int markerColor[];
    int lineColor;

    boolean simMode = false;
    int simMarkerColorIndex = 0;
    int simMarkerDelayCount = 0;
    int simMarkerDelayMax = 100;

    boolean debug = true;
    boolean debug_remote = false;

    if ( args.length > 0 ) {
      if ( args[0].equals("sim") ) {
        System.out.println("Simulation");
        simMode = true;
      }
    }

    LCD lcd = new LCD(simMode);
    Keys keys = new Keys(simMode);

    int tour = ROBO_TOUR_ALEXANDER;
    String configfilePrefix = "/home/robo-hub/";

    lcd.drawString("Alex: Up Indoor: Down", 1, 2);

    int pressedKey = simMode?Button.ID_DOWN:keys.waitForAnyPress();

    if ( pressedKey == Button.ID_DOWN ) tour = ROBO_TOUR_TEST;

    lcd.clear(2);
    lcd.drawString("ColorTab: U/D/L/R", 1, 2);

    pressedKey = simMode?Button.ID_DOWN:keys.waitForAnyPress();

    String colortabPost = "";

    if ( pressedKey == Button.ID_DOWN )  colortabPost = "_1";
    if ( pressedKey == Button.ID_LEFT )  colortabPost = "_2";
    if ( pressedKey == Button.ID_RIGHT ) colortabPost = "_3";

    if ( tour == ROBO_TOUR_ALEXANDER ) {

      int tour_markerColor[] = { Color.GREEN,
                                 Color.RED, Color.GREEN,
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
    else         roboMotorCtl = new DualMotorControl("MotorPort.A", "MotorPort.B");

    Audio audio = new Audio(simMode);
    audio.setVolume(15);

    PrintWriter debugRS = null;

    if ( debug && debug_remote ) {
      try {
        debugRS = new PrintWriter("/home/robo-hub/remote_sender.dbg");
        if (debugRS != null) {
          remoteSensor.setDebugWriter(debugRS);
          motionCtrlRemote.setDebugWriter(debugRS);
        }
      } catch (IOException ioe)  {
        throw new RuntimeException();
      }
    }

    /*
     * Main-Loop
     *
     * waiting for new clients
     *   - open socket
     *   - wait for client
     */
    do { //App loop

      lcd.clear();

      int mode = ROBO_MODE_MOVE;

      /*
       * Communication
       */
      //TODO: just call init in the loop!!
      //ConnectionInfo ci = new ConnectionInfo("server.properties");
      ConnectionInfo ci = new ConnectionInfo("0.0.0.0", 2000);
      SocketConnection sock = new SocketConnection(ci);
      sock.init();

      lcd.drawString("Wait for Client", 1, 2);
      audio.systemSound(0);

      sock.waitForClient();    //TODO: add "abort"-option

      lcd.clear(2);
      audio.systemSound(1);

      int rxTxInt = 0;
      int params[] = {0, 0, 0};
      int command = -1;

      /*
       * Client-Loop
       *   - handle single Client
       *   - client starts/stops quiz
       */

      do { //Client loop

        markerDetect.reset();

        lcd.clear();
        lcd.drawString("Ready for next Quiz", 1, 2);

        /*
         *  Wait for client-command to start next quiz
         *
         *  TODO: check wether "DISCONNECT" works
         */
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
        audio.systemSound(3);

        /*
         * Handle Client start command: start quiz or disconnect
         */

        switch (command) {
          case EV3Command.COMMAND_START:
            roboMoveMode = params[0];

            if (debug) lcd.drawString("Mode: " + roboMoveMode, 1, 1);

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
            break;
          case EV3Command.COMMAND_DISCONNECT:
            running = false;
            sock.reset();
            sock.close();
            break;
          default:
            System.out.println("Invalid state in 'wait for next quiz': command is " + command);
            running = false;
        }

        /*
         * Quiz-Loop: runs until:
         *    - quiz end
         *    - client stops the quiz (go to next quiz)
         *    - client disconnects
         *
         *    TODO: More options to leave quiz-loop
         */

        while (running) { //Quiz loop
          for (int i = 0; i < colSensorList.length; i++) colSensorList[i].update(blackBoard);

          remoteSensor.update(blackBoard);

          colorSensors.update(blackBoard);
          markerDetect.update(blackBoard);

          lineDetector.update(blackBoard);
          braitenberg.update(blackBoard);

          motionCtrlRemote.update(blackBoard);
          motionCtrlBB.update(blackBoard);

          int colors[] = colorSensors.getSensorOutput();

          if (debug) lcd.drawString("Color: " + colors[0] + " / " + colors[1], 1, 5, true);

          simMarkerDelayCount++;

          if ((mode == ROBO_MODE_MOVE) && (markerDetect.hasMarkerDetected() ||
               (simMode && ( simMarkerDelayCount == simMarkerDelayMax)))) {

            mode = ROBO_MODE_MARKER;
            int markerCol = -1;

            if ( simMode ) {
              simMarkerDelayCount = 0;
              markerCol = markerColor[simMarkerColorIndex];
              simMarkerColorIndex = (simMarkerColorIndex+1)%markerColor.length;
            } else {
              markerCol = markerDetect.getDetectedMarker();
            }

            if (debug) lcd.drawString("Marker: " + markerCol + "", 1, 7, true);

            roboMotorCtl.backward(10);

            try {
              Thread.sleep((long) (1000 / frequency));
            } catch (InterruptedException ie) {
              throw new RuntimeException();
            }

            roboMotorCtl.stop();

            if (markerCol == markerColor[markerColor.length - 1]) {
              audio.systemSound(3);

              rxTxInt = EV3Command.encode(EV3Command.COMMAND_STOP);
              sock.sendInt(rxTxInt);

              running = false;
            } else {
              audio.systemSound(4);
              rxTxInt = EV3Command.encode(EV3Command.COMMAND_QUESTION);
              sock.sendInt(rxTxInt);

              /*
               * SingleQuestion/Answer-Loop
               *
               * TODO: what about disconnect?
               */
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

            motionCtrl.reset();

            if (running == false) break;

            if ( simMode ) mode = ROBO_MODE_MOVE;

          } else { //Robo in marker_mode or marker is not detected
            if (!markerDetect.hasMarkerDetected()) {
              mode = ROBO_MODE_MOVE;
            }
          }

          if (sock.rxAvailable()) {
            rxTxInt = sock.receiveInt();
            command = EV3Command.decode(rxTxInt, params);

            switch (command) {
              case EV3Command.COMMAND_STOP:
                running = false;
                break;
              //Test the App (if the question in the app in activate by user)
              case EV3Command.COMMAND_ANSWER:
                if (params[0] == 1) motionCtrl.setMaxSpeed(defaultHighSpeed);
                if (params[0] == 2) motionCtrl.setMaxSpeed(defaultLowSpeed);
                running = true;
                break;

              default:
                sock.returnReceivedInt(rxTxInt);
            }
          }

          if (running == false) break;

          int speed[] = motionCtrl.getSpeed();

          roboMotorCtl.setSpeed(speed);

          if (debug) {
            lcd.clear(1, 7);

            lcd.drawString("Mode: " + mode + "", 1, 1);
            lcd.drawString("Speed: " + speed[0] + " / " + speed[1], 1, 2);
            lcd.drawString("Remote: " + motionCtrlRemote.getSpeed()[0] + " " + motionCtrlRemote.getSpeed()[1], 1, 3);
            lcd.drawString("Line: " + lineDetector.getSensorOutput()[0] + "", 1, 4);
            lcd.drawString("Color: " + colors[0] + " / " + colors[1], 1, 5);
            lcd.drawString("Cmd: " + command, 1, 6);
            lcd.drawString("Marker: " + markerDetect.getNextColor() + "", 1, 7);
          }

          try {
            Thread.sleep((long) (1000 / frequency));
          } catch (InterruptedException ie) {
            throw new RuntimeException();
          }

          running = running && keys.isButtonUp("Escape") && (!sock.isClosed());

        } //End of quiz loop

        roboMotorCtl.stop();
        lcd.clear();

      } while (!sock.isClosed());  //Client loop

      lcd.drawString("Up to quit", 1, 2);

      pressedKey = simMode?Button.ID_UP:keys.waitForAnyPress();

      if (keys.isButtonDown("Up")) {
        closeApp = true;
      }

    } while (closeApp == false); //app loop

    for ( int i = 0; i < colSensorList.length; i++) colSensorList[i].close();
    if ( debugRS != null ) debugRS.close();
  }

}
