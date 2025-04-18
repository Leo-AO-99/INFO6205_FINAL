package edu.neu.info6205.othello;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

// import edu.neu.info6205.othello.NextStep;
// import edu.neu.info6205.othello.Othello;
// import edu.neu.info6205.othello.OthelloPosition;
// import edu.neu.info6205.othello.PositionState;

public class OthelloPositionTest {
    @Test
    public void testBlackWin1() {
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othello.BLACK;
            }
        }
        OthelloPosition position = new OthelloPosition(grid, Othello.BLACK);
        assertEquals(PositionState.BLACK_WIN, position.getPositionState());
    }

    @Test
    public void testBlackWin2() {
        int[][] grid = new int[8][8];
        grid[0] = new int[] {2, 1, 1, 1, 1, 1, 1, 1};
        OthelloPosition position = new OthelloPosition(grid, Othello.BLACK);
        assertEquals(0, position.getPossibleMoves().size());
        assertEquals(PositionState.BLACK_WIN, position.getPositionState());
    }

    @Test
    public void testInProgress1() {
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othello.BLACK;
            }
        }
        grid[0][0] = Othello.EMPTY;
        grid[0][1] = Othello.WHITE;
        grid[0][2] = Othello.WHITE;
        grid[0][3] = Othello.WHITE;
        
        OthelloPosition position = new OthelloPosition(grid, Othello.WHITE);
        assertEquals(PositionState.IN_PEOGRESS, position.getPositionState());
        assertEquals(1, position.getPossibleMoves().size());
    }

    @Test
    public void testInProgress2() {
        OthelloPosition position = new OthelloPosition();
        assertEquals(4, position.getPossibleMoves().size());
        assertEquals(PositionState.IN_PEOGRESS, position.getPositionState());
    }

    @Test
    public void testMove1() {
        OthelloPosition position = new OthelloPosition();
        for (NextStep nextStep : position.getPossibleMoves()) {
            System.out.println("nextStep: " + nextStep);
            OthelloPosition newPosition = position.move(nextStep, Othello.BLACK);
            System.out.println(newPosition.showBoard());
            assertEquals(1, newPosition.whitCount());
            assertEquals(4, newPosition.blackCount());
        }
        
    }
}
