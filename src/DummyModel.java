import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.List;

public class DummyModel implements IBouncingBallsModel {

	private final double areaWidth;
	private final double areaHeight;

    private Ball b1, b2;

	public DummyModel(double width, double height) {
		this.areaWidth = width;
		this.areaHeight = height;

        //Add balls
        b1 = randomBall();
        b2 = randomBall();
	}

    //CALCULATIONS

    public void recalcVelocity() {
        //Bounce off walls
        b1.wallBounce();
        b2.wallBounce();

        if (hasCollided(b1,b2)) {
            b1.vx = -b1.vx;
            b2.vy = -b2.vy;
        }
    }

    private boolean hasCollided(Ball b1, Ball b2) {
        //Two balls have collided when their distance is less than the sum of their radiuses.
        double dx = b1.x - b2.x;
        double dy = b1.y - b2.y;
        double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return dist < b1.r + b2.r;
    }

    //UTILITY

    /**
     * Generate a random ball in the field
     */
    private Ball randomBall() {
        final double MIN_SIZE = 0.2;
        final double MAX_SIZE = 2;
        final double MIN_SPEED = 0.5;
        final double MAX_SPEED = 5;
        
        double r = random(MIN_SIZE, MAX_SIZE); 
        double vx = random(MIN_SPEED, MAX_SPEED); 
        double vy = random(MIN_SPEED, MAX_SPEED); 
        double x = random(0+r, areaWidth-r); 
        double y = random(0+r, areaHeight-r); 

        return new Ball(x,y,vx,vy,r);
    }

    private double random(double min, double max) {
        return Math.random()*(max - min) + min;
    }

    //APPLET METHODS

	@Override
	public void tick(double deltaT) {
            recalcVelocity();
            b1.tick(deltaT);
            b2.tick(deltaT);
	}

	@Override
	public List<Ellipse2D> getBalls() {
		List<Ellipse2D> myBalls = new LinkedList<Ellipse2D>();
        myBalls.add(b1.asEllipse());
        myBalls.add(b2.asEllipse());
		return myBalls;
	}

    public class Ball {

        double x, y, vx, vy, r;

        public Ball(double x, double y, double vx, double vy, double r) {
            this.x =  x;
            this.y =  y;
            this.vx =  vx;
            this.vy =  vy;
            this.r =  r;
        }

        public void tick(double deltaT) {
            x += vx * deltaT;
            y += vy * deltaT;
        }

        public void wallBounce() {
            if (x < r || x > areaWidth - r) {
                vx *= -1;
            }
            if (y < r || y > areaHeight - r) {
                vy *= -1;
            }
        }

        public Ellipse2D asEllipse() {
		    return new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r);
        }

    }
}
