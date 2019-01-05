package interfaces;

import disc.data.Waypoint;

public abstract class Navigation {

    public Navigation() {}
    
    public abstract void goTo(Waypoint w);
    
    public abstract void goTowards(Waypoint w);
}
