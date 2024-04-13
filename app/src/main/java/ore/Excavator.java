package ore;
/*
public class Excavator implements Machine {
}*/

import ch.aplu.jgamegrid.Actor;

public class Excavator extends Actor implements Machine
{
    public Excavator()
    {
        super(true, "sprites/excavator.png");  // Rotatable
    }
}
