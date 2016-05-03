import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.List;

public class DummyModel implements IBouncingBallsModel {

	private final double areaWidth;
	private final double areaHeight;

    private List<Ball> balls;

	public DummyModel(double width, double height) {
		this.areaWidth = width;
		this.areaHeight = height;
        balls = new LinkedList<>();

        //Add balls
        balls.add(randomBall());
        balls.add(randomBall());
	}

	@Override
	public void tick(double deltaT) {
        for (Ball b : balls) {
            b.tick(deltaT);
        }
	}

	@Override
	public List<Ellipse2D> getBalls() {
		List<Ellipse2D> myBalls = new LinkedList<Ellipse2D>();
        for (Ball b : balls) {
            myBalls.add(b.asEllipse());
        }
		return myBalls;
	}

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

    public class Ball {

        private double x, y, vx, vy, r;

        public Ball(double x, double y, double vx, double vy, double r) {
            this.x =  x;
            this.y =  y;
            this.vx =  vx;
            this.vy =  vy;
            this.r =  r;
        }

        public void tick(double deltaT) {
            if (x < r || x > areaWidth - r) {
                vx *= -1;
            }
            if (y < r || y > areaHeight - r) {
                vy *= -1;
            }
            x += vx * deltaT;
            y += vy * deltaT;
        }

        public Ellipse2D asEllipse() {
		    return new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r);
        }

    }
}
