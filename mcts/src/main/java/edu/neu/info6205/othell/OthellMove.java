package edu.neu.info6205.othell;

import java.util.List;

import edu.neu.info6205.core.Move;

public class OthellMove implements Move<Othell> {

    private final NextStep nextStep;

    public OthellMove(NextStep nextStep) {
        this.nextStep = nextStep;
    }

    public OthellMove(int player, int x, int y, List<int[]> directions) {
        this.nextStep = new NextStep(player, x, y, directions);
    }

    public NextStep move() {
        return nextStep;
    }

    @Override
    public int player() {
        return nextStep.player;
    }

    @Override
    public String toString() {
        return "OthellMove{" +
                "nextStep=" + nextStep +
                '}';
    }
}
