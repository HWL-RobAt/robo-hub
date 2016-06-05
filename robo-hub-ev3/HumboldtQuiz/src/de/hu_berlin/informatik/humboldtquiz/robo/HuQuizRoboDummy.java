package de.hu_berlin.informatik.humboldtquiz.robo;

import de.hu_berlin.informatik.app.connection.ConnectionInfo;
import de.hu_berlin.informatik.app.connection.SocketConnection;
import de.hu_berlin.informatik.app.connection.protocol.EV3Command;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 10.04.16
 * Time: 22:36
 * To change this template use File | Settings | File Templates.
 */
public class HuQuizRoboDummy {

  static boolean running = true;

  public static void main(String[] args) {

    int rxTxInt = -1;
    int params[] = { 0,0,0 };
    int command = -1;

    testTranslateSpeed();

    ConnectionInfo ci = new ConnectionInfo("config/server.properties");

    SocketConnection sock = new SocketConnection(ci);
    sock.init();
    sock.waitForClient();

    System.out.println("App is connected");

    do {

      do {
        rxTxInt = sock.receiveInt();
        command = EV3Command.decode(rxTxInt, params);
      } while ((command != EV3Command.COMMAND_START) && (command != EV3Command.COMMAND_DISCONNECT));

      if (command == EV3Command.COMMAND_START) {
        System.out.println("Start");
        System.out.println("Mode: " + params[0]);

        running = true;
      } else {
        if (command == EV3Command.COMMAND_DISCONNECT) {
          System.out.println("and Quit");
          running = false;
          sock.close();
        } else {
          System.out.println("Wrong command");
          running = false;
        }
      }

      int round = 0;
      while (running) {
        if (round == 50000) {
          System.out.println("Detect Marker");
          rxTxInt = EV3Command.encode(EV3Command.COMMAND_QUESTION);
          sock.sendInt(rxTxInt);

          do {
            rxTxInt = sock.receiveInt();
            command = EV3Command.decode(rxTxInt, params);
          } while ((command != EV3Command.COMMAND_ANSWER) && (command != EV3Command.COMMAND_STOP));

          System.out.println("Question finshed");

          switch (command) {
            case EV3Command.COMMAND_ANSWER: {
              System.out.println("Answer");
              break;
            }
            default: {
              System.out.println("Wrong command: " + command);
            }
          }

          round = 0;
        } else if (sock.rxAvailable()) {
          rxTxInt = sock.receiveInt();
          command = EV3Command.decode(rxTxInt, params);

          switch (command) {
            case EV3Command.COMMAND_MOVE: {
              System.out.println("Move on with speed pre: " + params[0] + " / " + params[1]);
              translateSpeed(params);
              System.out.println("Move on with speed: " + params[0] + " / " + params[1]);
              break;
            }
            case EV3Command.COMMAND_STOP: {
              System.out.println("Stop robo");
              running = false;
              break;
            }
            default: {
              System.out.println("Wrong command");
              running = false;
            }
          }
        }

        if (running) {
          try {
            round++;
            Thread.sleep(10, 0);
          } catch (InterruptedException ie) {
            System.out.println("Interrupted");
            System.exit(0);
          }
        }
      }
    } while (!sock.isClosed());

    System.out.println("App disconnected");
    System.out.println("Finished");
  }


  static public void testTranslateSpeed() {
    int tests[][] = { {0,0}, {50,50}, {100,100}, {0,100}, {100,0}, {0,50}, {50,0}, {100,50},  {50,100}};

    for ( int i = 0; i < tests.length; i++) {
      System.out.print("" + tests[i][0] + "/" + tests[i][1] +" -> ");
      translateSpeed(tests[i]);
      System.out.println("" + tests[i][0] + "/" + tests[i][1] );
    }
  }


  // x < 0
  // 1 = abs(y)+x
  // 2 = abs(y)
  // x > 0
  // 1 = abs(y)
  // 2 = abs(y)-x

  static public void translateSpeed(int speed[]) {
    int x = speed[0] - 50;
    int y = speed[1] - 50;

    int absSpeed = (int)Math.round(Math.sqrt(x*x + y*y));

    if ( y < 0 ) {
      speed[0] = 8 * (absSpeed - x);
      speed[1] = 8 * (absSpeed + x);
    } else {
      speed[0] = -8 * (absSpeed - x);
      speed[1] = -8 * (absSpeed + x);
    }
  }
}
