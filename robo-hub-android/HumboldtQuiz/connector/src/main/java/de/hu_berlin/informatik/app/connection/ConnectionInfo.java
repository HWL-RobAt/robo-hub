package de.hu_berlin.informatik.app.connection;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 11.05.16
 * Time: 07:54
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionInfo {
  String nameOrIP = "";
  int port = -1;

  public ConnectionInfo(String filename) {
    Properties p = new Properties();

    try {
      p.load(new FileReader(filename));
    } catch ( FileNotFoundException e) {
      System.out.println("File " + filename + " not found");
    } catch ( IOException ioe ) {
      System.out.println("File " + filename + " not found");
    }

    nameOrIP = p.getProperty("brickname");
    port = Integer.parseInt(p.getProperty("brickport"));
  }

  public ConnectionInfo(String nameOrIP, int port) {
    this.nameOrIP = nameOrIP;
    this.port = port;
  }

  public String getNameOrIP() {
    return nameOrIP;
  }

  public int getPort() {
    return port;
  }

  public void setNameOrIP(String nameOrIP) {
    this.nameOrIP = nameOrIP;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
