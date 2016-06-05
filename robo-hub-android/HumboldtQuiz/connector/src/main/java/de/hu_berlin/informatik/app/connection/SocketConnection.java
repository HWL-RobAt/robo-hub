package de.hu_berlin.informatik.app.connection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
public class SocketConnection implements Connection {

  Socket s = null;
  ServerSocket serv = null;
  ConnectionInfo ci = null;
  DataOutputStream out = null;
  DataInputStream in = null;

  boolean hasPrefetched = false;
  int prefetchedInt = 0;

  public SocketConnection(ConnectionInfo connInfo) {
    this.ci = connInfo;
  }

  public void init() {
    if ( ci.nameOrIP.equals("0.0.0.0") ) {
      try {
        serv = new ServerSocket(this.ci.port);
      } catch (IOException ioe) {
        System.out.println("Socket error.");
        serv = null;
      }
    } else {
      try {
        s = new Socket(this.ci.nameOrIP, this.ci.port);
      } catch (UnknownHostException e) {
        System.out.println("Host " + this.ci.nameOrIP + " is not known.");
        s = null;
      } catch (IOException ioe) {
        System.out.println("Socket error.");
        s = null;
      }
      setIOStreams();
    }
  }

  public void waitForClient() {
    if ( serv != null ) {
      try {
        s = serv.accept(); //Wait for Laptop to connect
      } catch ( IOException ioe) {
        System.out.println("Socket error.");
        serv = null;
      }
      setIOStreams();
    }
  }

  private void setIOStreams() {
    if ( s != null ) {
      try {
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
      } catch ( IOException ioe) {
        System.out.println("Socket error.");
        out = null;
        in = null;
      }
    }
  }

  public int read() {
    try {
      //Test msg from laptop
      System.out.println(in.readUTF());
    } catch ( IOException ioe) {
      System.out.println("Write error.");
    }

    return 0;
  }

  public int write() {
    try {
      //Test msg from laptop
      out.writeUTF("Hello EV3!");
    } catch ( IOException ioe) {
      System.out.println("Write error.");
    }

    return 0;
  }

  public int receiveInt() {
    if ( hasPrefetched ) {
      hasPrefetched = false;
      return prefetchedInt;
    }
    int r = 0;
    try {
      //Test msg from laptop
      r = in.readInt();
    } catch ( IOException ioe) {
      System.out.println("receiveInt error.");
    }

    return r;
  }

  public void returnReceivedInt(int i) {
    hasPrefetched = true;
    prefetchedInt = i;
  }

  public void sendInt(int txInt) {
    try {
      out.writeInt(txInt);
    } catch ( IOException ioe) {
      System.out.println("sendInt error.");
    }
  }

  public void close() {
    if (s != null) {
      try {
        s.close();
      } catch (IOException ioe) {
        System.out.println("Error on close.");
        s = null;
      }
    }

    s = null;

    if (serv != null) {
      try {
        serv.close();
      } catch (IOException ioe) {
        System.out.println("Error on close.");
        serv = null;
      }
    }

    serv = null;
  }

  public boolean isClosed() {
    return ((serv == null) && (s == null));
  }

  public boolean rxAvailable() {
    int rxCount = 0;
    try {
      rxCount = in.available();
    } catch (IOException ioe) {
      System.out.println("Error on rxAvailable.");
    }
    return (rxCount > 0);
  }
}

