package interfaces;

public abstract class Movement {

    public Movement() {}
    
    /**
     * Causes the robot to travel in a direction at a specified speed.
     * @param angle The angle the robot should travel towards.
     * @param speed The speed at which the robot should travel.
     */
    public abstract void move(double angle, double speed);
    
    /**
     * Causes the robot to rotate in its own space to orient to a certain angle.
     * @param angle The angle the robot should orient towards.
     * @param speed The speed at which the robot should orient.
     */
    public abstract void turn(double angle, double speed);
    
    /**
     * Causes the robot to stop moving or turning.
     */
    public abstract void stop();
}
