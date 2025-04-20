package edu.neu.info6205.othello;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NextStep nextStep = (NextStep) o;
        return player == nextStep.player && x == nextStep.x && y == nextStep.y && directions.equals(nextStep.directions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, x, y, directions);
    }
}