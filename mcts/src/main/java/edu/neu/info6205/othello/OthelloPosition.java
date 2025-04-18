package edu.neu.info6205.othello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;



public final class OthelloPosition {

    private final int[][] grid;
    private final int whiteCount;
    private final int blackCount;
    private final int count;
    private final int last;
    private final int nextPlayer;
    private final List<NextStep> possibleMoves;
    private final PositionState positionState;

    private final static int GRID_SIZE = 8;

    public OthelloPosition(int[][] grid, int last) {
        this.grid = grid;
        this.last = last;
        int wc = 0;
        int bc = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == Othello.WHITE) {
                    wc++;
                } else if (grid[i][j] == Othello.BLACK) {
                    bc++;
                }
            }
        }
        this.whiteCount = wc;
        this.blackCount = bc;
        this.count = wc + bc;
        List<NextStep> moves = new ArrayList<>();
        PositionState ps = PositionState.IN_PEOGRESS;
        boolean isEnd = false;
        if (wc == 0 || bc == 0) {
            isEnd = true;
            nextPlayer = last;
        } else {
            int opponent = opponent(last);
            moves = moves(opponent);
            if (moves.isEmpty()) {
                moves = moves(last);
                nextPlayer = last;
                if (moves.isEmpty()) {
                    isEnd = true;// does not matter
                }
            } else {
                nextPlayer = opponent;
            }
        }
        this.possibleMoves = moves;
        
        if (isEnd) {
            if (wc > bc) {
                ps = PositionState.WHITE_WIN;
            } else if (wc < bc) {
                ps = PositionState.BLACK_WIN;
            } else {
                ps = PositionState.DRAW;
            }
        }
        this.positionState = ps;
    }

    public OthelloPosition() {
        this.grid = new int[GRID_SIZE][GRID_SIZE];
        this.grid[3][3] = Othello.BLACK;
        this.grid[4][4] = Othello.BLACK;
        this.grid[3][4] = Othello.WHITE;
        this.grid[4][3] = Othello.WHITE;
        this.last = Othello.WHITE;
        this.nextPlayer = Othello.BLACK;
        this.count = 4;
        this.whiteCount = 2;
        this.blackCount = 2;
        this.possibleMoves = moves(Othello.BLACK);
        this.positionState = PositionState.IN_PEOGRESS;
    }

    public int getPlayer() {
        return nextPlayer;
    }

    public int getPiece(int x, int y) {
        return grid[x][y];
    }



    public PositionState getPositionState() {
        return positionState;
    }

    public boolean isEnd() {
        return positionState != PositionState.IN_PEOGRESS;
    }

    public Optional<Integer> winner() {
        if (positionState == PositionState.WHITE_WIN) {
            return Optional.of(Othello.WHITE);
        } else if (positionState == PositionState.BLACK_WIN) {
            return Optional.of(Othello.BLACK);
        }
        return Optional.empty();
    }

    static int opponent(int player) {
        return player == Othello.BLACK ? Othello.WHITE : Othello.BLACK;
    }

    private boolean checkFlipDirection(int x, int y, int dx, int dy, int player) {
        int opponent = opponent(player);
        int nx = x + dx;
        int ny = y + dy;

        // new piece must sit beside the opponent's piece
        if (!inBounds(nx, ny) || grid[nx][ny] != opponent) {
            return false;
        }

        // in this specific direction, thers is at least one opponent piece
        // we just need to find same color piece
        while (true) { 
            nx += dx;
            ny += dy;
            if (!inBounds(nx, ny) || grid[nx][ny] == Othello.EMPTY) {
                return false;
            }
            if (grid[nx][ny] == player) {
                return true;
            }
        }
    }

    private List<int[]> checkPlace(int x, int y, int player) {
        List<int[]> validDirections = new ArrayList<>();

        if (grid[x][y] != Othello.EMPTY) {
            return validDirections;
        }

        for (int[] dir: DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];
            // if any direction is valid, then the move is valid
            if (checkFlipDirection(x, y, dx, dy, player)) {
                validDirections.add(dir);
            }
        }
        
        return validDirections;
    }

    public List<NextStep> getPossibleMoves() {
        return possibleMoves;
    }

    private List<NextStep> moves(int player) {
        List<NextStep> result = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                List<int[]> validDirections = checkPlace(i, j, player);
                if (!validDirections.isEmpty()) {
                    result.add(new NextStep(player, i, j, validDirections));
                }
            }
        }
        return result;
    }

    private static int flip(int[][] newGrid, NextStep nextStep) {
        int x = nextStep.x;
        int y = nextStep.y;
        int reverseCount = 0;
        for (int[] dir: nextStep.directions) {
            int dx = dir[0];
            int dy = dir[1];
            int nx = x + dx;
            int ny = y + dy;
            while (inBounds(nx, ny)) {
                if (newGrid[nx][ny] == Othello.EMPTY) {
                    throw new IllegalArgumentException("Invalid move");
                }
                if (newGrid[nx][ny] == nextStep.player) {
                    break;
                }
                newGrid[nx][ny] = nextStep.player;
                reverseCount++;
                nx += dx;
                ny += dy;
            }
        }
        return reverseCount;
    }


    /**
     * this method will not check if the move is valid,
     * @param x
     * @param y
     * @param player
     * @return
     */
    public OthelloPosition move(NextStep nextStep, int player) {
        // double check
        if (nextStep.player != player) {
            throw new IllegalArgumentException("Invalid move");
        }
        int[][] newGrid = copyGrid();
        newGrid[nextStep.x][nextStep.y] = player;
        flip(newGrid, nextStep);


        return new OthelloPosition(newGrid, player);
    }

    public String showBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                sb.append(grid[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int whitCount() {
        return whiteCount;
    }

    public int blackCount() {
        return blackCount;
    }

    private int[][] copyGrid() {
        int[][] copy = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, GRID_SIZE);
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof OthelloPosition position))
            return false;
        return Arrays.deepEquals(grid, position.grid) && nextPlayer == position.nextPlayer;
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.deepHashCode(grid) + nextPlayer;
    }


    
    private static final int[][] DIRECTIONS = {
        {-1,  0},
        { 1,  0},
        { 0, -1},
        { 0,  1},
        {-1, -1},
        {-1,  1},
        { 1, -1},
        { 1,  1}
    };

    private static boolean inBounds(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

    
}
