package edu.neu.info6205.othello;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class OthelloMCTSTest {
    @Test
    public void testTerminal() {
        OthelloMCTS mcts = new OthelloMCTS(new OthelloNode(new Othello().new OthelloState()));
        assertEquals(mcts.isTerminal(), false);

        OthelloNode nextNode = (OthelloNode) OthelloMCTS.nextNode(mcts);
        mcts = new OthelloMCTS(nextNode);
        assertEquals(mcts.isTerminal(), false);

        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othello.BLACK;
            }
        }
        

        Othello.OthelloState state = new Othello().new OthelloState(new OthelloPosition(grid, Othello.BLACK));
        OthelloNode node = new OthelloNode(state);
        assertEquals(node.isLeaf(), true);
        mcts = new OthelloMCTS(node);
        assertEquals(mcts.isTerminal(), true);
    }

    @Test
    public void testOthelloMCTS() {
        OthelloMCTS mcts = new OthelloMCTS(new OthelloNode(new Othello().new OthelloState()));
        assertEquals(mcts.isTerminal(), false);

        while (!mcts.isTerminal()) {
            OthelloNode nextNode = (OthelloNode) OthelloMCTS.nextNode(mcts);
            mcts = new OthelloMCTS(nextNode);
        }
        assertEquals(mcts.isTerminal(), true);
    }

    @Test
    public void testBenchmark() {
        assertEquals(OthelloMCTS.totalTime, 0);
        assertEquals(OthelloMCTS.totalSelectTime, 0);
        assertEquals(OthelloMCTS.totalExpandTime, 0);
        assertEquals(OthelloMCTS.totalSimulateTime, 0);
        assertEquals(OthelloMCTS.totalBackpropTime, 0);
        assertEquals(OthelloMCTS.stepCount, 0);
        OthelloMCTS.benchmark();
        assertNotEquals(OthelloMCTS.totalTime, 0);
        assertNotEquals(OthelloMCTS.totalSelectTime, 0);
        assertNotEquals(OthelloMCTS.totalExpandTime, 0);
        assertNotEquals(OthelloMCTS.totalSimulateTime, 0);
        assertNotEquals(OthelloMCTS.totalBackpropTime, 0);
        assertNotEquals(OthelloMCTS.stepCount, 0);
    }
}
