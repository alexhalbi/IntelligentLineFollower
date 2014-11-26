import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;

public class IFollower {
	
	
	public static void main(String[] args) {
		LightSensor sensLeft = new LightSensor(SensorPort.S1);
		LightSensor sensRight = new LightSensor(SensorPort.S2);
		

		sensLeft.setFloodlight(true);
		sensRight.setFloodlight(true);

		double Tv = 0.05;//1.8;
		double Tn = 1.5;
		double Kp = 0.18;
		double ei = 0;
		double el = 0;
		double diff = calibrate(sensLeft, sensRight);
		double tt = 0;
		double fspeed = 300;
		double t = 0.05;
		
		while (true) {
			int left = sensLeft.getNormalizedLightValue();
			int right = sensRight.getNormalizedLightValue();

			double e = left - right - diff;
			
			if(Math.abs(e)<10) {
				e = 0;
			}
			
			ei += e;

			tt-=System.currentTimeMillis()/1000;
			double w = Kp * (e + t/Tn * ei + Tv/t * (e - el));
			LCD.drawString("l:" + left + "  diff="+diff+"\nr:" + right + "   tt="+tt+"\ne:" + e + "\nw:" + w, 0, 0);
			
			tt= System.currentTimeMillis()/1000;
			
			Motor.A.setSpeed((int) (fspeed+w));
			Motor.B.setSpeed((int) (fspeed-w));
			if(w>fspeed) {
				LCD.drawString("ERROR" + w, 30, 0);
			}
			Motor.A.forward();
			Motor.B.forward();
			
			el = e;
			try {
				Thread.sleep((long) (t*1000));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	private static double calibrate(LightSensor sensLeft, LightSensor sensRight) {
		LCD.drawString("Calibrating \n(All white)", 0, 0);
		Button.waitForAnyPress();
		return sensLeft.getNormalizedLightValue()-sensRight.getNormalizedLightValue();
	}
}
