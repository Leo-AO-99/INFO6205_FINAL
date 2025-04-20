package edu.neu.info6205.othello;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class OthelloStateTest {
    
    @Test
    public void testShowBoard() {
        Othello othello = new Othello(123L);
        Othello.OthelloState state1 = othello.new OthelloState();
        assertEquals(state1.showBoard(), "00000000\n00000000\n00000000\n00012000\n00021000\n00000000\n00000000\n00000000\n");

        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othello.BLACK;
            }
        }
        Othello.OthelloState state2 = othello.new OthelloState(new OthelloPosition(grid, Othello.BLACK));
        assertEquals(state2.showBoard(), "11111111\n11111111\n11111111\n11111111\n11111111\n11111111\n11111111\n11111111\n");
    }

    @Test
    public void testReward() {
        Othello othello = new Othello(123L);
        Othello.OthelloState state1 = othello.new OthelloState();
        // no winner so draw reward, 1.0
        assertEquals(1.0, state1.reward(), 0.0001);

        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othello.BLACK;
            }
        }
        Othello.OthelloState state2 = othello.new OthelloState(new OthelloPosition(grid, Othello.BLACK));
        // board is full of black, black win, reward 2.0
        assertEquals(2.0, state2.reward(), 0.0001);
    }

    @Test
    public void testMoves() {
        Othello othello = new Othello(123L);
        Othello.OthelloState state1 = othello.new OthelloState();
        // black has legal moves
        assertEquals(state1.moves(Othello.BLACK).size(), 4);
        // white has no legal moves
        assertEquals(state1.moves(Othello.WHITE).size(), 0);

        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othello.BLACK;
            }
        }
        Othello.OthelloState state2 = othello.new OthelloState(new OthelloPosition(grid, Othello.BLACK));
        // no legal moves for all colors
        assertEquals(state2.moves(Othello.BLACK).size(), 0);
        assertEquals(state2.moves(Othello.WHITE).size(), 0);
    }

    @Test
    public void testNext() {
        Othello othello = new Othello(123L);
        Othello.OthelloState state1 = othello.new OthelloState();

        OthelloMove illegalMove = new OthelloMove(Othello.BLACK, 0, 0, new ArrayList<>());
        // illegal move
        try {
            state1.next(illegalMove);
            fail("Expected IllegalArgumentException but no exception was thrown");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
        
        // legal move
        OthelloMove legalMove = (OthelloMove) state1.moves(Othello.BLACK).iterator().next();
        Othello.OthelloState state2 = (Othello.OthelloState) state1.next(legalMove);

        assertEquals(state2.showBoard(), "00000000\n00000000\n00001000\n00011000\n00021000\n00000000\n00000000\n00000000\n");

    }
}
