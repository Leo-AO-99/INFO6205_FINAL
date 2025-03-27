package edu.neu.info6205.othell;

import java.util.List;

public class NextStep {
    public final int player;
    public final int x;
    public final int y;
    public final List<int[]> directions;

    public NextStep(int player, int x, int y, List<int[]> directions) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.directions = directions;
    }

    @Override
    public String toString() {
        return "NextStep{" +
                "player=" + player +
                ", x=" + x +
                ", y=" + y +
                ", directions=" + directions +
                '}';
    }
    
}