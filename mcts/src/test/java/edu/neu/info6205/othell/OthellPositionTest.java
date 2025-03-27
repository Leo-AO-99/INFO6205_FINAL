package edu.neu.info6205.othell;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class OthellPositionTest {
    @Test
    public void testBlackWin1() {
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othell.BLACK;
            }
        }
        OthellPosition position = new OthellPosition(grid, Othell.BLACK);
        assertEquals(PositionState.BLACK_WIN, position.getPositionState());
    }

    @Test
    public void testBlackWin2() {
        int[][] grid = new int[8][8];
        grid[0] = new int[] {2, 1, 1, 1, 1, 1, 1, 1};
        OthellPosition position = new OthellPosition(grid, Othell.BLACK);
        assertEquals(0, position.getPossibleMoves().size());
        assertEquals(PositionState.BLACK_WIN, position.getPositionState());
    }

    @Test
    public void testInProgress1() {
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = Othell.BLACK;
            }
        }
        grid[0][0] = Othell.EMPTY;
        grid[0][1] = Othell.WHITE;
        grid[0][2] = Othell.WHITE;
        grid[0][3] = Othell.WHITE;
        
        OthellPosition position = new OthellPosition(grid, Othell.WHITE);
        assertEquals(PositionState.IN_PEOGRESS, position.getPositionState());
        assertEquals(1, position.getPossibleMoves().size());
    }

    @Test
    public void testInProgress2() {
        OthellPosition position = new OthellPosition();
        assertEquals(4, position.getPossibleMoves().size());
        assertEquals(PositionState.IN_PEOGRESS, position.getPositionState());
    }

    @Test
    public void testMove1() {
        OthellPosition position = new OthellPosition();
        for (NextStep nextStep : position.getPossibleMoves()) {
            System.out.println("nextStep: " + nextStep);
            OthellPosition newPosition = position.move(nextStep, Othell.BLACK);
            System.out.println(newPosition.showBoard());
            assertEquals(1, newPosition.whitCount());
            assertEquals(4, newPosition.blackCount());
        }
        
    }
}
