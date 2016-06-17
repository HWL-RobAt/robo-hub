package de.hu_berlin.informatik.app.connection.test;

import de.hu_berlin.informatik.app.connection.ConnectionInfo;
import de.hu_berlin.informatik.app.connection.SocketConnection;

/**
 * Created by robert on 13.05.16.
 */
public class SocketConnectionTest {

  public static void main(String[] args) {
    System.out.println("Len: " + args.length);
    if ( args.length != 2 ) {
      System.out.println("Use java SocketConnectionTest [c|s] properties-file");
      System.exit(0);
    }

    ConnectionInfo ci = new ConnectionInfo(args[1]);
    SocketConnection csock = new SocketConnection(ci);
    if ( args[0].equals("c")) {
      csock.writeString("Hello World");
    } else if ( args[0].equals("s")) {
      csock.waitForClient();
      csock.readString();
    } else {
      System.out.println("Unknown mode \"" + args[0] + "\".");
      System.exit(0);
    }
    csock.close();
  }
}
