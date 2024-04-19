package ore;

import ch.aplu.jgamegrid.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Properties;

public class OreSim extends GameGrid implements GGKeyListener
{
  // ------------- Inner classes -------------
  public enum ElementType{
    OUTSIDE("OS"), EMPTY("ET"), BORDER("BD"),
    PUSHER("P"), BULLDOZER("B"), EXCAVATOR("E"), ORE("O"),
    ROCK("R"), CLAY("C"), TARGET("T");
    private String shortType;

    ElementType(String shortType) {
      this.shortType = shortType;
    }

    public String getShortType() {
      return shortType;
    }

    public static ElementType getElementByShortType(String shortType) {
      ElementType[] types = ElementType.values();
      for (ElementType type: types) {
        if (type.getShortType().equals(shortType)) {
          return type;
        }
      }

      return ElementType.EMPTY;
    }
  }

  // ------------- End of inner classes ------

  protected MapGrid grid;
  public static final Color borderColor = new Color(100, 100, 100);
  protected Ore[] ores;
  protected Target[] targets;
  protected Pusher pusher;
  protected Bulldozer bulldozer;
  protected Excavator excavator;
  private boolean isFinished = false;
  private Properties properties;
  protected boolean isAutoMode;
  private double gameDuration;
  protected List<String> controls;
  private int movementIndex;
  protected int pusherCount = 0, bulldozerCount = 0, excavatorCount = 0;
  private StringBuilder logResult = new StringBuilder();
  public OreSim(Properties properties, MapGrid grid)
  {
    super(grid.getNbHorzCells(), grid.getNbVertCells(), 30, false);
    this.grid = grid;
    this.properties = properties;

    ores = new Ore[grid.getNbOres()];
    targets = new Target[grid.getNbOres()];

    isAutoMode = properties.getProperty("movement.mode").equals("auto");
    gameDuration = Integer.parseInt(properties.getProperty("duration"));
    setSimulationPeriod(Integer.parseInt(properties.getProperty("simulationPeriod")));
    controls = Arrays.asList(properties.getProperty("machines.movements").split(","));

  }

  /**
   * The main method to run the game
   * @param isDisplayingUI
   * @return
   */
  public String runApp(boolean isDisplayingUI) {
    GGBackground bg = getBg();
    Drawer.drawBoard(bg, grid, nbVertCells, nbHorzCells);
    Drawer.drawActors(this);
    addKeyListener(this);
    if (isDisplayingUI) {
      show();
    }

    if (isAutoMode) {
      doRun();
    }

    int oresDone = Ore.checkOresDone(ores, grid);
    double ONE_SECOND = 1000.0;
    while(oresDone < grid.getNbOres() && gameDuration >= 0) {
      try {
        Thread.sleep(simulationPeriod);
        double minusDuration = (simulationPeriod / ONE_SECOND);
        gameDuration -= minusDuration;
        String title = String.format("# Ores at Target: %d. Time left: %.2f seconds", oresDone, gameDuration);
        setTitle(title);
        if (isAutoMode) {
          pusher.autoMoveNext(isFinished);
          if (bulldozer != null) bulldozer.autoMoveNext(isFinished);
          if (excavator != null) excavator.autoMoveNext(isFinished);
          updateLogResult();
        }

        oresDone = Ore.checkOresDone(ores, grid);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    doPause();

    if (oresDone == grid.getNbOres()) {
      setTitle("Mission Complete. Well done!");
    } else if (gameDuration < 0) {
      setTitle("Mission Failed. You ran out of time");
    }

    updateStatistics();
    isFinished = true;
    return logResult.toString();
  }


  /**
   * Students need to modify this method so it can write an actual statistics into the statistics file. It currently
   *  only writes the sample data.
   */
  private void updateStatistics() {
    File statisticsFile = new File("statistics.txt");
    FileWriter fileWriter = null;

    try {
      fileWriter = new FileWriter(statisticsFile);

      if (pusher != null )
        fileWriter.write("Pusher-" + pusher.getID() + " Moves: " + pusher.getMovesCount() + "\n");

      if (excavator != null )
        fileWriter.write("Excavator-" + excavator.getID() + " Moves: " + excavator.getMovesCount() + "\n");

      if (excavator != null )
        fileWriter.write("Excavator-" + excavator.getID() + " Rock removed: " + excavator.getItemsRemovedCount() +
                "\n");

      if (bulldozer != null )
        fileWriter.write("Bulldozer-" + bulldozer.getID() + " Moves: " + bulldozer.getMovesCount() + "\n");

      if (bulldozer != null )
        fileWriter.write("Bulldozer-" + bulldozer.getID() + " Clay removed: " + bulldozer.getItemsRemovedCount() +
                "\n");

    } catch (IOException e) {
      System.out.println("Cannot write to file - e: " + e.getLocalizedMessage());
    } finally {
      try {
        fileWriter.close();
      } catch (IOException e) {
        System.out.println("Cannot close file - e: " + e.getLocalizedMessage());
      }
    }
  }

  /**
   * The method is automatically called by the framework when a key is pressed. Based on the pressed key, the pusher
   *  will change the direction and move
   * @param evt
   * @return
   */
  public boolean keyPressed(KeyEvent evt)
  {
    if (isFinished)
      return true;

    Location next = null;
    switch (evt.getKeyCode())
    {
      case KeyEvent.VK_LEFT:
        next = pusher.getLocation().getNeighbourLocation(Location.WEST);
        pusher.setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = pusher.getLocation().getNeighbourLocation(Location.NORTH);
        pusher.setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = pusher.getLocation().getNeighbourLocation(Location.EAST);
        pusher.setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = pusher.getLocation().getNeighbourLocation(Location.SOUTH);
        pusher.setDirection(Location.SOUTH);
        break;
    }

    Target curTarget = (Target) getOneActorAt(pusher.getLocation(), Target.class);
    if (curTarget != null){
      curTarget.show();
    }

    if (next != null && pusher.canMove(next))
    {
      pusher.setLocation(next);
      updateLogResult();
    }
    refresh();
    return true;
  }

  public boolean keyReleased(KeyEvent evt)
  {
    return true;
  }

  /**
   * The method will generate a log result for all the movements of all actors
   * The log result will be tested against our expected output.
   * Your code will need to pass all the 3 test suites with 9 test cases.
   */
  private void updateLogResult() {
    movementIndex++;
    List<Actor> pushers = getActors(Pusher.class);
    List<Actor> ores = getActors(Ore.class);
    List<Actor> targets = getActors(Target.class);
    List<Actor> rocks = getActors(Rock.class);
    List<Actor> clays = getActors(Clay.class);
    List<Actor> bulldozers = getActors(Bulldozer.class);
    List<Actor> excavators = getActors(Excavator.class);

    logResult.append(movementIndex + "#");
    logResult.append(ElementType.PUSHER.getShortType()).append(Drawer.actorLocations(pushers)).append("#");
    logResult.append(ElementType.ORE.getShortType()).append(Drawer.actorLocations(ores)).append("#");
    logResult.append(ElementType.TARGET.getShortType()).append(Drawer.actorLocations(targets)).append("#");
    logResult.append(ElementType.ROCK.getShortType()).append(Drawer.actorLocations(rocks)).append("#");
    logResult.append(ElementType.CLAY.getShortType()).append(Drawer.actorLocations(clays)).append("#");
    logResult.append(ElementType.BULLDOZER.getShortType()).append(Drawer.actorLocations(bulldozers)).append("#");
    logResult.append(ElementType.EXCAVATOR.getShortType()).append(Drawer.actorLocations(excavators));

    logResult.append("\n");
  }

}