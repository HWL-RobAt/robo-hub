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
		while(Button.ESCAPE.isUp())
		{
			s.fetchSample(sample, 0);
			int color = detector.getColorID(new int[]{(int)(sample[0]*255+0.5),
					                                      (int)(sample[1]*255+0.5),
					                                      (int)(sample[2]*255+0.5)});
			System.out.println(ColorClassifier.colorToString(color));
			
			Thread.sleep(20);
		}

		s.close();

	}

	
}
