package kattmatare;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.NXTLineLeader;

public class Main {

	private static final int DIVISIONS = 12;
	private static final float GEARING = 1.4f;

	private static final int DIVISION_DEGREES = (int) ((360 * GEARING) / DIVISIONS);

	private static final float MOVE_SPEED = DIVISION_DEGREES / 1.0f;
	private static final int MOVE_ACC = 6000;

	private static final float SHAKE_SPEED = 500;
	private static final int SHAKE_ACC = 20000;

	private static int numberOfFeedings = 6;
	private static long millisBeforeFirstFeeding = 10 * 1000;
	private static long millisBetweenFeedings = (24 * 60 * 60 * 1000) / 6;

	private static LightSensor lightSensor;

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		lightSensor = new LightSensor(SensorPort.S4, true);

		while (true) {
			showMenu();

			long lastFeedTime = System.currentTimeMillis() - millisBetweenFeedings + millisBeforeFirstFeeding;

			lightSensor.setFloodlight(false);
			LCD.clear();
			for (int i = 0; i < numberOfFeedings; i++) {
				drawStrings(
						new String[] { "Feed count: ", Integer.toString(i), "/", Integer.toString(numberOfFeedings) },
						0, 1);
				waitForFeeding(lastFeedTime);
				lastFeedTime = System.currentTimeMillis();
				doFeed();
			}
			lightSensor.setFloodlight(true);
		}
	}

	private static void showMenu() {
		LCD.clear();
		drawStrings(new String[] { "Feed count: ", Long.toString(numberOfFeedings) }, 0, 1);
		drawStrings(new String[] { "First   ", millisToTimeString(millisBeforeFirstFeeding) }, 0, 3);
		drawStrings(new String[] { "Spacing ", millisToTimeString(millisBetweenFeedings) }, 0, 5);
		LCD.drawString("Orange to start!", 0, 7);
		Button.ENTER.waitForPress();

	}

	private static void waitForFeeding(long lastFeedTime) {
		LCD.clear(3);

		final String foodInString = "Food in ";
		LCD.drawString(foodInString, 0, 3);

		long elapsed;
		while ((elapsed = (System.currentTimeMillis() - lastFeedTime)) < millisBetweenFeedings) {

			String timeString = millisToTimeString(millisBetweenFeedings - elapsed);

			LCD.drawString(timeString, foodInString.length(), 3);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void doFeed() {
		final String feedString = "Food time!";
		final int xPos = LCD.DISPLAY_CHAR_WIDTH / 2 - feedString.length() / 2;

		LCD.clear(3);
		LCD.drawString(feedString, xPos, 3);

		moveOneDivision();
		shake();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		moveOneDivision();
	}

	private static void moveOneDivision() {
		setSpeed(MOVE_SPEED);
		setAcceleration(MOVE_ACC);

		rotate(DIVISION_DEGREES);
	}

	private static void shake() {
		setSpeed(SHAKE_SPEED);
		setAcceleration(SHAKE_ACC);

		int shake_degrees = 8;

		rotate(-shake_degrees / 2);
		for (int i = 0; i < 20; i++) {
			rotate(shake_degrees);
			shake_degrees = -shake_degrees;
		}
		rotate(shake_degrees / 2);
	}

	private static void setSpeed(float speed) {
		Motor.B.setSpeed(speed);
		Motor.C.setSpeed(speed);
	}

	private static void setAcceleration(int acceleration) {
		Motor.B.setAcceleration(acceleration);
		Motor.C.setAcceleration(acceleration);
	}

	private static void rotate(int degrees) {
		Motor.B.rotate(-degrees, true);
		Motor.C.rotate(degrees);
		Motor.B.waitComplete();
	}

	private static void drawStrings(String[] strings, int x, int y, boolean addSpace) {
		for (String s : strings) {
			LCD.drawString(s, x, y);
			x += s.length();
			if (addSpace) {
				LCD.drawChar(' ', x, y);
				x++;
			}
		}
	}

	private static void drawStrings(String[] strings, int x, int y) {
		drawStrings(strings, x, y, false);
	}

	private static String millisToTimeString(long millis) {

		long hours = millis / (60 * 60 * 1000);
		long minutes = (millis / (60 * 1000)) % 60;
		long seconds = (millis / 1000) % 60;

		StringBuilder sb = new StringBuilder();

		if (hours < 10) {
			sb.append('0');
		}
		sb.append(hours);

		sb.append(':');

		if (minutes < 10) {
			sb.append('0');
		}
		sb.append(minutes);

		sb.append(':');

		if (seconds < 10) {
			sb.append('0');
		}
		sb.append(seconds);

		return sb.toString();
	}

}
