package maybeFlappyBird;

import java.awt.Color; 
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import support.cse131.Timing;



public class Game {
	private final double RADIUS = 0.02;
	private final double REASONABLE_DISTANCE = 0.4;
	private final double START_X = 0.3;
	private final double GAP_HEIGHT_LOWER_BOUND = 0.1;
	private final double GAP_WIDTH_LOWER_BOUND = 0.09;
	private final double GAP_HEIGHT_UPPER_BOUND = 0.9;
	private final double GAP_WIDTH_UPPER_BOUND = 0.15;
	private double speed = 0.3;
	private Color birdColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	public LinkedList<Obstacle> obs = new LinkedList<Obstacle>();
	double xBird = 0.2;
	double yBird = 0.5;
	private final int SPACE = KeyEvent.VK_SPACE;
   
	//	returns the x coordinate of the previous obstacle in the list
	public double getLastXCoord(LinkedList<Obstacle> a, int count) {
		if(count == 0) {
			return START_X;
		}
		return a.get(count - 1).getXCoord();
	}

	// creates amt of obstacles in the obs list
	public LinkedList<Obstacle> createObstacles(int amt) {
		int count = obs.size();
		while(count < amt) {
			double xCoordLast = getLastXCoord(obs, count);
			double xCoord =  (Math.random()) / 2;
			while(xCoord - xCoordLast < REASONABLE_DISTANCE) {
				xCoord += Math.random() / 2;
			}
			double gapHeight = Math.random() + GAP_HEIGHT_LOWER_BOUND;
			double gapWidth = Math.random() + GAP_WIDTH_LOWER_BOUND;
			while(gapHeight > GAP_HEIGHT_UPPER_BOUND) {
				gapHeight = Math.random() + GAP_HEIGHT_LOWER_BOUND;
			}
			while (gapWidth > GAP_WIDTH_UPPER_BOUND) {
				gapWidth = Math.random() + GAP_WIDTH_LOWER_BOUND;
			}
			Obstacle temp = new Obstacle(xCoord, gapHeight, gapWidth);
			obs.addLast(temp);
			count++;
		}
		return obs;
	}
	
	public void offScreen() {
		if(obs.get(obs.size()-1).getXCoord() < REASONABLE_DISTANCE) {
			Obstacle temp = obs.get(obs.size()-1);
			obs.clear();
			obs.addFirst(temp);
			createObstacles(3);
		}
	}

	public void drawInitial() {
		createObstacles(5);
		draw();
	}

	public void draw() {
		StdDraw.clear(StdDraw.BOOK_LIGHT_BLUE);
		for(Obstacle o : obs) {
			o.draw();
		}
		StdDraw.setPenColor(birdColor);
		StdDraw.filledCircle(xBird, yBird, RADIUS);
		StdDraw.show();
	}

	public void moveGame(double deltaTime) {
		for(Obstacle o : obs) {
			o.moveXCoord(deltaTime * speed);
		}
		offScreen();
		draw();
	}

	public boolean waitKeyInput() {
		return StdDraw.isKeyPressed(SPACE);
	}
	public void moveBird(boolean pressed) {
		yBird  = pressed ? yBird + 0.005 : yBird - 0.007;
	}
	
	public boolean gameAlive() {
		if(yBird >= 1 || yBird <= 0) {
			return false;
		}
		for(Obstacle o : obs) {
			double[] yCoords = o.getYCoords();
			double bottomLimit = yCoords[0];
			double topLimit = yCoords[1];
			double xCoord = o.getXCoord();
			if((Math.abs(xBird + RADIUS - xCoord) <= o.getWidth()) && (yBird + RADIUS < bottomLimit || yBird + RADIUS >= topLimit)) {
				return false;
			}
		}
		return true;
	}
	
	public void flapTheBird() {
		double prevTime = Timing.getCurrentTimeInSeconds();
		StdDraw.enableDoubleBuffering();
		drawInitial();
		boolean pressed = false;
		int frameCount = 0;
		int frameUp = -2;
		while(gameAlive()) {
			double currTime = Timing.getCurrentTimeInSeconds();
			double deltaTime = currTime - prevTime;
			moveGame(deltaTime);
			if(waitKeyInput() && !pressed) {
				pressed = true;
				frameUp = frameCount;
			}
			int temp = frameCount;
			while(frameUp >= 0 && temp - frameUp < 5) {
				moveBird(true);
				temp++;
			}
			if(!waitKeyInput() && pressed)  {
				pressed = false;
			}
			moveBird(pressed);
			draw();
			frameCount++;
			prevTime = currTime;
		} 
		StdDraw.setPenColor();
		StdDraw.text(0.5, 0.5,  "game over :(");
		StdDraw.show();
	}

	public static void main(String[] args) {
		Game n = new Game();
		n.flapTheBird();
	}  
		
}
