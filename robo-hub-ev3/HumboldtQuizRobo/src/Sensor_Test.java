import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;

public class Sensor_Test
{
	public static void main(String[] args) throws InterruptedException
	{
		//LCD.drawString("Color Sensor Demo", 0, 0);
		EV3ColorSensor s = new EV3ColorSensor(SensorPort.S1);
		float[] sample = new float[3];
		
		ColorClassifierYUV detector = new ColorClassifierYUV();
		detector.loadConfig("/home/robo-hub/color_clf.properties");

		s.setCurrentMode("RGB");

		int number = 0;

		while ( Button.ESCAPE.isUp()) {

			System.out.println("Next is: " + number);
			Thread.sleep(5000);

		  detector.startDebug("/home/robo-hub/coloryuv.debug_" + number);

		  while(Button.UP.isUp()) {
  			s.fetchSample(sample, 0);
	  		int color = detector.getColorID(new int[]{(int)(sample[0]*255+0.5),
		  			                                      (int)(sample[1]*255+0.5),
			  		                                      (int)(sample[2]*255+0.5)});
				System.out.println(ColorClassifier.colorToString(color));
			
				Thread.sleep(50);
			}

			detector.stopDebug();

			number++;
			System.out.println("Pressed and Hold Esc to quit!");
			Thread.sleep(2000);
		}

		s.close();

	}

}
