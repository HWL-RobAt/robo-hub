import lejos.hardware.Audio;
import lejos.remote.ev3.RemoteEV3;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 04.05.16
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class BluetoothConnector {
  public static void main(String[] args) {
    try {
      RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
      Audio sound = ev3.getAudio();
      sound.systemSound(0);
      sound.systemSound(2);
    } catch (Exception e) {
      System.out.println("Couldn't connect");
    }
  }
}
