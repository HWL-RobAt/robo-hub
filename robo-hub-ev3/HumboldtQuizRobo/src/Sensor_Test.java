
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;

public class Sensor_Test
{
	public static void main(String[] args) throws InterruptedException
	{
		LCD.drawString("Color Sensor Demo", 0, 0);
		EV3ColorSensor s = new EV3ColorSensor(SensorPort.S1);
		float[] sample = new float[3];
		
		ColorDetector detector = new ColorDetector();
		
		s.setCurrentMode("RGB");
		while(true)
		{
			s.fetchSample(sample, 0);
			int color = detector.getColor(sample);
			System.out.println(colorToString(color));
			
			Thread.sleep(100);
		}
	}
	
	public static String colorToString(int c) {
		switch(c) {
			case Color.RED: return "RED";
			case Color.GREEN: return "GREEN";
			case Color.BLUE: return "BLUE";
			case Color.YELLOW: return "YELLOW";
			case Color.MAGENTA: return "MAGENTA";
			case Color.ORANGE: return "ORANGE";
			case Color.WHITE: return "WHITE";
		    case Color.BLACK: return "BLACK";
		    case Color.PINK: return "PINK";
		    case Color.GRAY: return "GRAY";
		    case Color.LIGHT_GRAY: return "LIGHT_GRAY";
		    case Color.DARK_GRAY: return "DARK_GRAY";
		    case Color.CYAN: return "CYAN";
		    case Color.BROWN: return "BROWN";
		    case Color.NONE: return "NONE";
		}
		return "INVALIDE ID";
	}
	
}
