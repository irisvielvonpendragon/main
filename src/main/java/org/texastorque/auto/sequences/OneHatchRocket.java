package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class OneHatchRocket extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        Waypoint[] points1 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(10, 0, Pathfinder.d2r(45)),
            new Waypoint(15, 5, 0)
        };
        block1.add(new DrivePath(0, points1));
        // block1.add(new LiftSet(1, 1));

        ArrayList<Command> block2 = new ArrayList<>();
        Waypoint[] points2 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(-5, -5, Pathfinder.d2r(45)),
            new Waypoint(-15, -5, 0)
        };
        block2.add(new DrivePath(0, points2));
        // block2.add(new LiftSet(1, 0));

        addBlock(block1);
        addBlock(block2);
    }

}