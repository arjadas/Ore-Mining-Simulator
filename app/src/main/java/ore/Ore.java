package ore;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.List;

public class Ore extends Actor implements Item
{
    public Ore()
    {
        super("sprites/ore.png",2);
    }

    /**
     * When the pusher pushes the ore in 1 direction, this method will be called to check if the ore can move in that direction
     *  and if it can move, then it changes the location
     * @param sim
     * @return
     */
    public boolean moveOre(OreSim sim)
    {

        Location next = getNextMoveLocation();
        // Test if try to move into border
        Color c = sim.getBg().getColor(next);;
        Rock rock = (Rock) sim.getOneActorAt(next, Rock.class);
        Clay clay = (Clay) sim.getOneActorAt(next, Clay.class);
        if (c.equals(sim.borderColor) || rock != null || clay != null)
            return false;

        // Test if there is another ore
        Ore neighbourOre =
                (Ore) sim.getOneActorAt(next, Ore.class);
        if (neighbourOre != null)
            return false;

        // Reset the target if the ore is moved out of target
        Location currentLocation = getLocation();
        List<Actor> actors = sim.getActorsAt(currentLocation);
        if (actors != null) {
            for (Actor actor : actors) {
                if (actor instanceof Target) {
                    Target currentTarget = (Target) actor;
                    currentTarget.show();
                    show(0);
                }
            }
        }

        // Move the ore
        setLocation(next);

        // Check if we are at a target
        Target nextTarget = (Target) sim.getOneActorAt(next, Target.class);
        if (nextTarget != null) {
            show(1);
            nextTarget.hide();
        } else {
            show(0);
        }

        return true;
    }

    /**
     * Check the number of ores that are collected
     * @return nbTarget
     */

    public static int checkOresDone(Ore[] ores, MapGrid grid) {
        int nbTarget = 0;
        for (int i = 0; i < grid.getNbOres(); i++)
        {
            if (ores[i].getIdVisible() == 1)
                nbTarget++;
        }

        return nbTarget;
    }
}
