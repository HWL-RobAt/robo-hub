/**
 * Created by robert on 13.05.16.
 */
public class SocketConnectorTest {

  public static void main(String[] args) {
    System.out.println("Len: " + args.length);
    if ( args.length != 2 ) {
      System.out.println("Use java SocketConnectorTest [c|s] properties-file");
      System.exit(0);
    }

    ConnectionInfo ci = new ConnectionInfo(args[1]);
    SocketConnector csock = new SocketConnector(ci);
    if ( args[0].equals("c")) {
      csock.write();
    } else if ( args[0].equals("s")) {
      csock.waitForClient();
      csock.read();
    } else {
      System.out.println("Unknown mode \"" + args[0] + "\".");
      System.exit(0);
    }
    csock.close();
  }
}
