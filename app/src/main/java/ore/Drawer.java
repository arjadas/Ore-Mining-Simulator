package ore;

import ch.aplu.jgamegrid.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public abstract class Drawer {

    /**
     * Draw all different actors on the board: pusher, ore, target, rock, clay, bulldozer, excavator
     */
    public static void drawActors(OreSim oreSim)
    {
        int oreIndex = 0;
        int targetIndex = 0;

        for (int y = 0; y < oreSim.nbVertCells; y++)
        {
            for (int x = 0; x < oreSim.nbHorzCells; x++)
            {
                Location location = new Location(x, y);
                OreSim.ElementType a = oreSim.grid.getCell(location);
                if (a == OreSim.ElementType.PUSHER)
                {
                    oreSim.pusher = new Pusher(oreSim);
                    oreSim.addActor(oreSim.pusher, location);
                    oreSim.pusher.setupPusher(oreSim.isAutoMode, oreSim.controls);
                }
                if (a == OreSim.ElementType.ORE)
                {
                    oreSim.ores[oreIndex] = new Ore();
                    oreSim.addActor(oreSim.ores[oreIndex], location);
                    oreIndex++;
                }
                if (a == OreSim.ElementType.TARGET)
                {
                    oreSim.targets[targetIndex] = new Target();
                    oreSim.addActor(oreSim.targets[targetIndex], location);
                    targetIndex++;
                }

                if (a == OreSim.ElementType.ROCK)
                {
                    oreSim.addActor(new Rock(), location);
                }

                if (a == OreSim.ElementType.CLAY)
                {
                    oreSim.addActor(new Clay(), location);
                }

                if (a == OreSim.ElementType.BULLDOZER)
                {
                    oreSim.bulldozer = new Bulldozer(oreSim);
                    oreSim.addActor(oreSim.bulldozer, location);
                    oreSim.bulldozer.setupBulldozer(oreSim.isAutoMode, oreSim.controls);
                }
                if (a == OreSim.ElementType.EXCAVATOR)
                {
                    oreSim.excavator = new Excavator(oreSim);
                    oreSim.addActor(oreSim.excavator, location);
                    oreSim.excavator.setupExcavator(oreSim.isAutoMode, oreSim.controls);
                }
            }
        }
        System.out.println("ores = " + Arrays.asList(oreSim.ores));
        oreSim.setPaintOrder(Target.class);
    }

    /**
     * Draw the basic board with outside color and border color
     * @param bg, grid, nbVertCells, nbHorzCells
     */
    public static void drawBoard(GGBackground bg, MapGrid grid, int nbVertCells, int nbHorzCells)
    {
        bg.clear(new Color(230, 230, 230));
        bg.setPaintColor(Color.darkGray);
        for (int y = 0; y < nbVertCells; y++)
        {
            for (int x = 0; x < nbHorzCells; x++)
            {
                Location location = new Location(x, y);
                OreSim.ElementType a = grid.getCell(location);
                if (a != OreSim.ElementType.OUTSIDE)
                {
                    bg.fillCell(location, Color.lightGray);
                }
                if (a == OreSim.ElementType.BORDER)  // Border
                    bg.fillCell(location, OreSim.borderColor);
            }
        }
    }

    /**
     * Transform the list of actors to a string of location for a specific kind of actor.
     * @param actors
     * @return
     */
    public static String actorLocations(List<Actor> actors) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean hasAddedColon = false;
        boolean hasAddedLastComma = false;
        for (int i = 0; i < actors.size(); i++) {
            Actor actor = actors.get(i);
            if (actor.isVisible()) {
                if (!hasAddedColon) {
                    stringBuilder.append(":");
                    hasAddedColon = true;
                }
                stringBuilder.append(actor.getX() + "-" + actor.getY());
                stringBuilder.append(",");
                hasAddedLastComma = true;
            }
        }

        if (hasAddedLastComma) {
            stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
        }

        return stringBuilder.toString();
    }

}
