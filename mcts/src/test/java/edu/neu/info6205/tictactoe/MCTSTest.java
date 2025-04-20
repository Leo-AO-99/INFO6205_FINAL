package edu.neu.info6205.tictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class MCTSTest {

    @Test
    public void testTerminal() {
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe().new TicTacToeState()));
        assertEquals(mcts.isTerminal(), false);
        TicTacToeNode nextNode = (TicTacToeNode) MCTS.nextNode(mcts);
        mcts = new MCTS(nextNode);
        assertEquals(mcts.isTerminal(), false);

        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState(Position.parsePosition("X . 0\nX O .\nX . 0", TicTacToe.X));
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(node.isLeaf(), true);
        mcts = new MCTS(node);
        assertEquals(mcts.isTerminal(), true);
    }

    @Test
    public void testMCTS() {
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe().new TicTacToeState()));

        int left_empty = 9;

        // to test `Terminal` works correctly
        while (!mcts.isTerminal()) {
            TicTacToeNode nextNode = (TicTacToeNode) MCTS.nextNode(mcts);
            TicTacToeNode tmp = new TicTacToeNode(nextNode.state());
            tmp.expandAll();
            left_empty -= 1;
            // to test `nextNode` works correctly
            assertEquals(tmp.children().size(), left_empty);
            mcts = new MCTS(nextNode);
        }
    }

    @Test
    public void testDraw() {
        // based on our conclusion, if both players play optimally, the game will end in a draw

        for (int i = 0; i < 100; i++) {
            MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe().new TicTacToeState()));

            while (!mcts.isTerminal()) {
                TicTacToeNode nextNode = (TicTacToeNode) MCTS.nextNode(mcts);
                mcts = new MCTS(nextNode);
            }

            assertEquals(true, mcts.winner().isEmpty());
        }
    }

    @Test
    public void testBenchmark() {
        assertEquals(MCTS.totalTime, 0);
        assertEquals(MCTS.totalSelectTime, 0);
        assertEquals(MCTS.totalExpandTime, 0);
        assertEquals(MCTS.totalSimulateTime, 0);
        assertEquals(MCTS.totalBackpropTime, 0);
        assertEquals(MCTS.stepCount, 0);
        MCTS.benchmark();
        assertNotEquals(MCTS.totalTime, 0);
        assertNotEquals(MCTS.totalSelectTime, 0);
        assertNotEquals(MCTS.totalExpandTime, 0);
        assertNotEquals(MCTS.totalSimulateTime, 0);
        assertNotEquals(MCTS.totalBackpropTime, 0);
        assertNotEquals(MCTS.stepCount, 0);
    }
}
