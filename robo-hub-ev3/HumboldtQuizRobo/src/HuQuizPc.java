import java.io.IOException;
import java.util.Scanner;

/**
 * Created by robert on 13.05.16.
 */
public class HuQuizPc {
   static boolean running = true;

  public static void main(String[] args) {

    QuestionsCatalog qc = new QuestionsCatalog();
    qc.loadCatalog("/home/robert/UNI/PhD/2016/application/robo-hub/robo-hub-ev3/AppLogic/data/test_questions.csv");

    Scanner scanner = new Scanner(System.in, "UTF-8");
    scanner.useLocale(java.util.Locale.US);
    ConnectionInfo ci = new ConnectionInfo("config/client.properties");

    SocketConnector sock = new SocketConnector(ci);

    System.out.println("Connection to Robo");

    int params[] = { 0,0,0 };
    int command = AppCommand.COMMAND_MOVE;
    int rxTxInt = AppCommand.encode(command);
    sock.sendInt(rxTxInt);

    while ( running ) {
      if ( sock.rxAvailable() ) {
        rxTxInt = sock.receiveInt();
        command = AppCommand.decode(rxTxInt, params);

        switch (command) {
          case AppCommand.COMMAND_QUESTION: {
            System.out.println("Next Question.");
            Question q = qc.getNextQuestion();
            boolean isCorrect = q.askQuestion();

            if (isCorrect) {
              System.out.println("Richtig!");
              params[0] = 1;
            } else {
              System.out.println("Nöö!");
              params[0] = 2;
            }

            System.out.print("Noch 'ne Frage (y/n)? ");
            String ans = scanner.next();
            running = (((int) ans.charAt(0) - 110) != 0); //n
            break;
          }
          default: {
            System.out.println("Wrong command");
            running = false;
          }
        }

        if ( running ) {
          System.out.println("Move on");
          rxTxInt = AppCommand.encode(AppCommand.COMMAND_MOVE, params);
          sock.sendInt(rxTxInt);
        }
      } else {
        try {
          int c = 0;
          KeyConsole.setTerminalToCBreak();

          if ( System.in.available() != 0 ) {
            c = System.in.read();
            System.out.println( c + "" );

            switch (c) {
              case 0x62: { //b
                params[0] = params[1] = 0;
                break;
              }
              case 0x66: { //f
                params[1] = 50;
                params[0] = 200;
                break;
              }
              case 0x67: { //g
                params[0] = params[1] = 200;
                break;
              }
              case 0x68: { //h
                params[1] = 200;
                params[0] = 50;
                break;
              }
              case 0x1B:  //ESC
              default: {
                params[0] = params[1] = 0;
                running = false;
              }
            }
            rxTxInt = AppCommand.encode(AppCommand.COMMAND_MOVE, params);
            sock.sendInt(rxTxInt);
          }

          params[0] = params[1] = 0;

        } catch (IOException e) {
          System.err.println("IOException");
        } catch (InterruptedException e) {
          System.err.println("InterruptedException");
        } finally {
          try {
            KeyConsole.stty( KeyConsole.ttyConfig.trim() );
          } catch (Exception e) {
            System.err.println("Exception restoring tty config");
          }
        }
      }

      if ( running ) {
        try {
          Thread.sleep(1000/50);
        } catch (InterruptedException ie) {
          System.out.println("Interrupted");
          System.exit(0);
        }
      }

    }

    System.out.println("Send finished");
    rxTxInt = AppCommand.encode(AppCommand.COMMAND_CONFIG);
    sock.sendInt(rxTxInt);

    System.out.println("End");

    sock.close();
  }
}
