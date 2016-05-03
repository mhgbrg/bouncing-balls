import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.List;

public class DummyModel implements IBouncingBallsModel {

    private final double areaWidth;
    private final double areaHeight;
    private final double G = -5;

    private Ball b1, b2;

    public DummyModel(double width, double height) {
        this.areaWidth = width;
        this.areaHeight = height;

        //Add balls
        b1 = randomBall();
        b2 = randomBall();

        /* DEBUG */
        System.out.println("B1 + \n" + b1);
        System.out.println("B2 + \n" + b2);

        double[][] testMatrix = {{4,3}, {3,2}};
        double[][] inverse = inverse2DMatrix(testMatrix);
        for (double[] v : inverse)
            for (double d : v)
                System.out.println(d);
        /* */
    }

    //CALCULATIONS

    public void handleCollisions() {
        //Bounce off walls
        b1.wallBounce();
        b2.wallBounce();

        if (hasCollided(b1,b2)) {
            // Calculate the basis of the collision
            double v1[] = {b2.x - b1.x, b2.y - b1.y};
            double v2[] = {-v1[1], v1[0]};
            double m[][] = {v1, v2};

            // Change the basis of the velocities to the new basis
            double vm1[] = multiplyMatrix(inverse2DMatrix(m), b1.velocityAsVector());
            double vm2[] = multiplyMatrix(inverse2DMatrix(m), b2.velocityAsVector());

            // Only change directions if the balls are getting closer
            if (vm1[0] > vm2[0]) {
                // Calculate new velocity in the direction of the collision
                // It is assumed that no energy is lost in the collision
                double i = b1.getMass() * vm1[0] + b2.getMass() * vm2[0];
                double r = -(vm2[0] - vm1[0]);
                double newV1 = (i - b2.getMass() * r) / (b1.getMass() + b2.getMass());
                double newV2 = r + (i - b2.getMass() * r) / (b1.getMass() + b2.getMass());

                // Change basis back to the standard basis
                double ve1[] = multiplyMatrix(m, new double[]{newV1, vm1[1]});
                double ve2[] = multiplyMatrix(m, new double[]{newV2, vm2[1]});

                // Set the new velocities
                b1.setVelocity(ve1);
                b2.setVelocity(ve2);
            }
        }
    }

    private boolean hasCollided(Ball b1, Ball b2) {
        //Two balls have collided when their distance is less than the sum of their radiuses.
        double dx = b1.x - b2.x;
        double dy = b1.y - b2.y;
        double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return dist < b1.r + b2.r;
    }

    private double[] multiplyMatrix(double[][] m, double[] v) {
        return new double[]{
            m[0][0] * v[0] + m[1][0] * v[1],
            m[0][1] * v[0] + m[1][1] * v[1]
        };
    }

    //Invert a 2D matrix
    private double[][] inverse2DMatrix(double[][] m) {
        /*  _     _
         * | a   b |
         * | c   d |
         * |_     _|
         */
        double a = m[0][0];
        double b = m[1][0];
        double c = m[0][1];
        double d = m[1][1];
        //determinant
        double det = a*d - b*c;
        double[][] inverse = {
            {
                d/det,
                -c/det
            },
            {
                -b/det,
                a/det
            }
        };
        return inverse;
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
        handleCollisions();
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

    //ADDITIONAL CLASSES

    public class Ball {
        double density = 1;

        double x, y, vx, vy, r;

        public Ball(double x, double y, double vx, double vy, double r) {
            this.x =  x;
            this.y =  y;
            this.vx =  vx;
            this.vy =  vy;
            this.r =  r;
        }

        public void tick(double deltaT) {
            vy += G * deltaT;
            x += vx * deltaT;
            y += vy * deltaT;
        }

        public void wallBounce() {
            final double EPS = 1E-4;
            if ((x < r && vx < 0) || (x > areaWidth - r && vx > 0)) {
                vx *= -1;
            }
            if ((y < r && vy < 0) || (y > areaHeight - r && vy > 0)) {
                vy *= -1;
            }

            //Counteract gravity and make sure the ball isn't stuck against the bottom wall
            if (y < r) {
                y = r+EPS;
            }
        }

        public Ellipse2D asEllipse() {
            return new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r);
        }

        public double[] velocityAsVector() {
            return new double[]{
                this.vx, this.vy
            };
        }

        public double getMass() {
            return density * 4 * Math.PI * Math.pow(r, 3) / 3;
        }

        public void setVelocity(double[] v) {
            this.vx = v[0];
            this.vy = v[1];
        }

        public String toString() {
            return "r: " + r ;
        }
    }
}
