package de.hu_berlin.informatik.app.connection.protocol;
/**
 * Created by robert on 13.05.16.
 */
public class EV3Command {

  public static final int COMMAND_NONE = 0;
  public static final int COMMAND_START = 1;
  public static final int COMMAND_STOP = 2;
  public static final int COMMAND_CONNECT = 3;
  public static final int COMMAND_DISCONNECT = 4;
  public static final int COMMAND_CONFIG = 5;
  public static final int COMMAND_MOVE = 6;
  public static final int COMMAND_SENSOR = 7;
  public static final int COMMAND_QUESTION = 8;
  public static final int COMMAND_ANSWER = 9;

  public static int encode(int command) {
    return EV3Command.encode(command, 0, 0, 0);
  }

  public static int encode(int command, int p0, int p1, int p2) {
    return ((command & 255) << 24) + ((p0 & 255) << 16) + ((p1 & 255) << 8) + (p2 & 255);
  }

  public static int encode(int command, int p[]) {
    return encode(command, p[0], p[1], p[2]);
  }

  public static int decode(int packedC, int params[]) {
    if ( params.length > 3 ) return -1;
    params[0] = (packedC >> 16) & 255;
    params[1] = (packedC >> 8) & 255;
    params[2] = (packedC ) & 255;
    return (packedC >> 24) & 255;
  }
}
