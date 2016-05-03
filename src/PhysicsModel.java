import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class PhysicsModel implements IBouncingBallsModel {

    private final int NBR_OF_BALLS = 2;
    private final double areaWidth;
    private final double areaHeight;
    private final double G = -5;

    private List<Ball> balls;

    public PhysicsModel(double width, double height) {
        this.areaWidth = width;
        this.areaHeight = height;

        //Add balls
        balls = new ArrayList<>();
        while (balls.size() < NBR_OF_BALLS) {
            Ball newBall = randomBall();
            boolean add = true;
            for (Ball b : balls) {
                if (hasCollided(b, newBall)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                balls.add(newBall);
            }
        }
    }

    //CALCULATIONS

    private void handleCollision(Ball b1, Ball b2) {
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

    private static double[] multiplyMatrix(double[][] m, double[] v) {
        return new double[]{
            m[0][0] * v[0] + m[1][0] * v[1],
            m[0][1] * v[0] + m[1][1] * v[1]
        };
    }

    //Invert a 2D matrix
    private static double[][] inverse2DMatrix(double[][] m) {
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
        final double MIN_SIZE = 1;
        final double MAX_SIZE = 3;
        final double MAX_SPEED = 25;

        double r = random(MIN_SIZE, MAX_SIZE);
        double vx = random(-MAX_SPEED, MAX_SPEED);
        double vy = random(-MAX_SPEED, MAX_SPEED);
        double x = random(0+r, areaWidth-r);
        double y = random(0+r, areaHeight-r);

        return new Ball(x,y,vx,vy,r);
    }

    private static double random(double min, double max) {
        return Math.random()*(max - min) + min;
    }

    //APPLET METHODS

    @Override
    public void tick(double deltaT) {
        //Bounce off walls
        for (Ball b : balls) {
            b.wallBounce();
        }
        //Check collision between all balls pairwise
        for (int i = 0; i < balls.size(); i++) {
            for (int j = 0; j < i; j++) {
                handleCollision(balls.get(i), balls.get(j));
            }
        }
        //Move all balls forward
        for (Ball b : balls) {
            b.tick(deltaT);
        }
    }

    @Override
    public List<Ellipse2D> getBalls() {
        List<Ellipse2D> myBalls = new ArrayList<Ellipse2D>();
        for (Ball b : balls) {
            myBalls.add(b.asEllipse());
        }
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
            if ((y < r && vy < 0) /* Unroof it! */ || (y > areaHeight - r && vy > 0) /* */) {
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
            return density * Math.PI * Math.pow(r, 2);
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
