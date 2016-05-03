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
        b1 = new Ball(1,1,2.3,1,1);
	}

	@Override
	public void tick(double deltaT) {
        b1.tick(deltaT);
	}

	@Override
	public List<Ellipse2D> getBalls() {
		List<Ellipse2D> myBalls = new LinkedList<Ellipse2D>();
		myBalls.add(b1.asEllipse());
		return myBalls;
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
