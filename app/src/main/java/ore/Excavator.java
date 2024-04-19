package ore;
/*
public class Excavator implements Machine {
}*/

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.List;


public class Excavator extends Actor implements Machine
{
    private int ID;
    private List<String> controls = null;
    private int autoMovementIndex = 0;
    private int movesCount = 0;
    private int itemsRemovedCount = 0;
    private OreSim oreSim;

    public Excavator(OreSim oreSim, int ID)
    {
        super(true, "sprites/excavator.png");  // Rotatable
        this.oreSim = oreSim;
        this.ID = ID;
    }
    public void setupExcavator(boolean isAutoMode, List<String> controls) {
        this.controls = controls;
    }

    /**
     * Method to move excavator automatically based on the instructions input from properties file
     */
    public void autoMoveNext(boolean isFinished) {
        if (controls != null && autoMovementIndex < controls.size()) {
            String[] currentMove = controls.get(autoMovementIndex).split("-");
            String machine = currentMove[0];
            String move = currentMove[1];
            autoMovementIndex++;
            if (machine.equals("E")) {
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

                /*Target curTarget = (Target) oreSim.getOneActorAt(getLocation(), Target.class);
                if (curTarget != null){
                    curTarget.show();
                }*/
                if (next != null && canMove(next))
                {
                    setLocation(next);
                    movesCount++;
                }
                oreSim.refresh();
            }
        }
    }

    /**
     * Check if we can move the bulldozer into the location
     * @param location
     * @return
     */
    public boolean canMove(Location location)
    {
        // Test if try to move into border, rock or clay
        Color c = oreSim.getBg().getColor(location);
        Clay clay = (Clay) oreSim.getOneActorAt(location, Clay.class);
        Ore ore = (Ore) oreSim.getOneActorAt(location, Ore.class);
        Pusher pusher = (Pusher) oreSim.getOneActorAt(location, Pusher.class);
        Bulldozer bulldozer = (Bulldozer) oreSim.getOneActorAt(location, Bulldozer.class);
        if (c.equals(oreSim.borderColor) || clay != null || ore != null || pusher != null || bulldozer != null)
            return false;
        else // Test if there is a rock
        {
            Rock rock = (Rock) oreSim.getOneActorAt(location, Rock.class);
            if (rock != null)
            {

                // remove rock
                rock.removeSelf();
                itemsRemovedCount++;
            }
        }

        return true;
    }

    // Method to retrieve moves count
    public int getMovesCount() {
        return movesCount;
    }

    // Method to retrieve items removed count
    public int getItemsRemovedCount() {
        return itemsRemovedCount;
    }
    public int getID() {
        return ID;
    }
}
