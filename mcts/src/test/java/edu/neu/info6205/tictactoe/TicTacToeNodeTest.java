package edu.neu.info6205.tictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TicTacToeNodeTest {


    // test for incrementPlayouts and addWins
    @Test
    public void winsAndPlayouts() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState(Position.parsePosition("X . 0\nX O .\nX . 0", TicTacToe.X));
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.isLeaf());

        // not work for our implementation
        // assertEquals(2, node.wins());
        // assertEquals(1, node.playouts());

        // in our implementation, wins and playouts are initialized to 0
        // will be changed by backPropagate part of MCTS
        assertEquals(0, (int) node.wins());
        assertEquals(0, node.playouts());


        for (int i = 1; i <= 9; i ++ ) {
            node.incrementPlayouts();
            node.addWins(1);
            assertEquals(i, node.playouts());
            assertEquals(i, (int) node.wins());
        }
    }

    // test constructor of TicTacToeNode
    @Test
    public void state() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(state, node.state());
    }

    // test white()
    @Test
    public void white() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.white());
    }

    // test children()
    @Test
    public void children() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(0, node.children().size());
    }

    // test expandAll()
    @Test
    public void expandAll() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(0, node.children().size());

        node.expandAll();
        assertEquals(9, node.children().size());
    }

    // for our implementation, backPropagate is implemented in MCTS class, so no test for it here
    // @Test
    // public void backPropagate() {
    //     // no tests yet
    // }
}