package ore;

import ch.aplu.jgamegrid.Actor;

/*
public class Ore implements Item {
}*/

public class Ore extends Actor implements Item
{
    public Ore()
    {
        super("sprites/ore.png",2);
    }
}
