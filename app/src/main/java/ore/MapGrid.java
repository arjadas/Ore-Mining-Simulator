
package ore;
import ch.aplu.jgamegrid.*;

public class MapGrid
{
  protected static int nbHorzCells = -1;
  protected static int nbVertCells = -1;
  private OreSim.ElementType[][] mapElements; // = new OreSim.ElementType[nbHorzCells][nbVertCells];
  private int nbStones = 0;



  private final static String[] mapModel =
          {
                  Mapstorer.map_0, Mapstorer.map_1
          };

  private final static int[] nbHorzCellsModel =
          {
                  Mapstorer.nbHorzCells_0, Mapstorer.nbHorzCells_1
          };

  private final static int[] nbVertCellsModel =
          {
                  Mapstorer.nbVertCells_0, Mapstorer.nbVertCells_1
          };

  private static int model;

  /**
   * Mapping from the string to a HashMap to prepare drawing
   * @param model
   */
  public MapGrid(int model)
  {
    this.model = model;
    nbHorzCells = nbHorzCellsModel[model];
    nbVertCells = nbVertCellsModel[model];

    mapElements = new OreSim.ElementType[nbHorzCells][nbVertCells];

    // Copy structure into integer array
    for (int k = 0; k < nbVertCellsModel[model]; k++)
    {
      for (int i = 0; i < nbHorzCellsModel[model]; i++)
      {
        switch (mapModel[model].charAt(nbHorzCellsModel[model] * k + i))
        {
          case ' ':
            mapElements[i][k] = OreSim.ElementType.OUTSIDE;  // Empty outside
            break;
          case '.':
            mapElements[i][k] = OreSim.ElementType.EMPTY;  // Empty inside
            break;
          case 'x':
            mapElements[i][k] = OreSim.ElementType.BORDER;  // Border
            break;
          case '*':
            mapElements[i][k] = OreSim.ElementType.ORE;  // Stones
            nbStones++;
            break;
          case 'o':
            mapElements[i][k] = OreSim.ElementType.TARGET;  // Target positions
            break;
          case 'P':
            mapElements[i][k] = OreSim.ElementType.PUSHER;
            break;
          case 'B':
            mapElements[i][k] = OreSim.ElementType.BULLDOZER;
            break;
          case 'E':
            mapElements[i][k] = OreSim.ElementType.EXCAVATOR;
            break;
          case 'R':
            mapElements[i][k] = OreSim.ElementType.ROCK; // Rocks
            break;
          case 'D':
            mapElements[i][k] = OreSim.ElementType.CLAY; // Clay
            break;
        }
      }
    }
  }

  public int getNbHorzCells()
  {
    return nbHorzCellsModel[model];
  }

  public int getNbVertCells()
  {
    return nbVertCellsModel[model];
  }

  public int getNbOres()
  {
    return nbStones;
  }

  public OreSim.ElementType getCell(Location location)
  {
    return mapElements[location.x][location.y];
  }
}
