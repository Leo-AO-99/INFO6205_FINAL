package edu.neu.info6205.othello;

import org.junit.Test;

import edu.neu.info6205.othello.Othello.OthelloState;

public class OthelloTest {
    
    @Test
    public void runGame() {
        long seed = 0L;
        Othello target = new Othello(seed);
        OthelloState state = (OthelloState) target.start();
        while (!state.isTerminal()) {
            System.out.println(state.player());
            System.out.println(state.showBoard());

            OthelloMove move = (OthelloMove) state.chooseMove(state.player());

            state = (OthelloState) state.next(move);
        }
    }
}
