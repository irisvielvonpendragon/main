package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class TestSequence extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new RotarySet(0, 2));
        block1.add(new ClawExtenderSet(0.5, false));

        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new DriveTurn(0, -30));

        addBlock(block1);
        addBlock(block2);
    }
}