package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import java.util.ArrayList;

public class BackupDrive extends Sequence {

    @Override
    protected void init() {
        // ArrayList<Command> block1 = new ArrayList<>();
        // block1.add(new TomInit(0));
        // block1.add(new RotarySet(0, 6));
        // block1.add(new ClawSet(2, true));

        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new RotarySet(0, 1));

        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new DriveTime(0.5, 1.8, 0.7));
        block2.add(new DriveTime(2.3, 0.5, -0.1));

        addBlock(block1);
        // addBlock(block2);
    }

}