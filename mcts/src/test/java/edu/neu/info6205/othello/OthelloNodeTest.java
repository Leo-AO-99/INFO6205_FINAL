package edu.neu.info6205.othello;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class OthelloNodeTest {
    @Test
    public void winsAndPlayouts() {
        Othello.OthelloState state1 = new Othello().new OthelloState();
        OthelloNode node1 = new OthelloNode(state1);
        assertTrue(!node1.isLeaf());

        assertEquals(0, node1.playouts());
        assertEquals(0.0, node1.wins(), 0.0001);

        for (int i = 1; i <= 100; i++) {
            node1.incrementPlayouts();
            node1.addWins(1);
            assertEquals(i, node1.playouts());
            assertEquals(i, node1.wins(), 0.0001);
        }

        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othello.EMPTY;
            }
        }
        

        Othello.OthelloState state2 = new Othello().new OthelloState(new OthelloPosition(grid, Othello.BLACK));
        OthelloNode node2 = new OthelloNode(state2);
        assertTrue(node2.isLeaf());
    }

    @Test
    public void state() {
        Othello.OthelloState state = new Othello().new OthelloState();
        OthelloNode node = new OthelloNode(state);
        assertEquals(state, node.state());
    }

    @Test
    public void white() {
        Othello.OthelloState state = new Othello().new OthelloState();
        OthelloNode node = new OthelloNode(state);
        assertTrue(!node.white());
    }

    @Test
    public void children() {
        Othello.OthelloState state = new Othello().new OthelloState();
        OthelloNode node = new OthelloNode(state);
        assertEquals(0, node.children().size());
    }

    @Test
    public void expandAll() {
        Othello.OthelloState state = new Othello().new OthelloState();
        OthelloNode node = new OthelloNode(state);
        assertEquals(0, node.children().size());

        node.expandAll();
        assertEquals(4, node.children().size());
    }
}
