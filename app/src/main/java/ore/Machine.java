package ore;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.List;

public interface Machine {

    public void autoMoveNext(boolean isFinished);

    public boolean canMove(Location location);


}
