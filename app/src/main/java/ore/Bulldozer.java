package ore;

import ch.aplu.jgamegrid.Actor;
/*
public class Bulldozer implements Machine {
}*/

public class Bulldozer extends Actor implements Machine
{
    public Bulldozer()
    {
        super(true, "sprites/bulldozer.png");  // Rotatable
    }
}
