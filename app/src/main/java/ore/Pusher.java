package ore;
/*
public class Pusher implements Machine{
}
*/

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.util.List;

public class Pusher extends Actor implements Machine
{
    private List<String> controls = null;
    private int autoMovementIndex = 0;

    private OreSim oreSim;

    public Pusher(OreSim oreSim)
    {
        super(true, "sprites/pusher.png");  // Rotatable
        this.oreSim = oreSim;
    }
    public void setupPusher(boolean isAutoMode, List<String> controls) {
        this.controls = controls;
    }

    /**
     * Method to move pusher automatically based on the instructions input from properties file
     */
    public void autoMoveNext(boolean isFinished) {
        if (controls != null && autoMovementIndex < controls.size()) {
            String[] currentMove = controls.get(autoMovementIndex).split("-");
            String machine = currentMove[0];
            String move = currentMove[1];
            autoMovementIndex++;
            if (machine.equals("P")) {
                if (isFinished)
                    return;

                Location next = null;
                switch (move)
                {
                    case "L":
                        next = getLocation().getNeighbourLocation(Location.WEST);
                        setDirection(Location.WEST);
                        break;
                    case "U":
                        next = getLocation().getNeighbourLocation(Location.NORTH);
                        setDirection(Location.NORTH);
                        break;
                    case "R":
                        next = getLocation().getNeighbourLocation(Location.EAST);
                        setDirection(Location.EAST);
                        break;
                    case "D":
                        next = getLocation().getNeighbourLocation(Location.SOUTH);
                        setDirection(Location.SOUTH);
                        break;
                }

                Target curTarget = (Target) oreSim.getOneActorAt(getLocation(), Target.class);
                if (curTarget != null){
                    curTarget.show();
                }
                if (next != null && oreSim.canMove(next))
                {
                    setLocation(next);
                }
                oreSim.refresh();
            }
        }
    }
}