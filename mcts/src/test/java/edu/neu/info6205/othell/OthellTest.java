package edu.neu.info6205.othell;

import org.junit.Test;

import edu.neu.info6205.othell.Othell.OthellState;

public class OthellTest {
    
    @Test
    public void runGame() {
        long seed = 0L;
        Othell target = new Othell(seed);
        OthellState state = (OthellState) target.start();
        while (!state.isTerminal()) {
            System.out.println(state.showBoard());

            OthellMove move = (OthellMove) state.chooseMove(state.player());

            state = (OthellState) state.next(move);

        }
    }
}
