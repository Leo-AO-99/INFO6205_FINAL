package edu.neu.info6205.othello;

import java.util.List;

import edu.neu.info6205.core.Move;

public class OthelloMove implements Move<Othello> {

    private final NextStep nextStep;

    public OthelloMove(NextStep nextStep) {
        this.nextStep = nextStep;
    }

    public OthelloMove(int player, int x, int y, List<int[]> directions) {
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
