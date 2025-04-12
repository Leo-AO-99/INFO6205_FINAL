package edu.neu.info6205.othell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OthellState {

    private final OthellPosition position;
    private final Othell game;
    private final Random random;

    public OthellState(Othell game, OthellPosition position, Random random) {
        this.game = game;
        this.position = position;
        this.random = random;
    }

    public OthellState(Othell game, Random random) {
        this(game, new OthellPosition(), random);
    }

    public String showBoard() {
        return position.showBoard();
    }

    public OthellPosition getPosition() {
        return position;
    }

    public Othell game() {
        return this.game;
    }

    public boolean isTerminal() {
        return this.position.isEnd();
    }

    public int player() {
        return this.position.getPlayer();
    }

    public Optional<Integer> winner() {
        return this.position.winner();
    }

    public Random random() {
        return random;
    }

    public Collection<OthellMove> moves(int player) {
        List<NextStep> possibleMoves = position.getPossibleMoves();
        ArrayList<OthellMove> list = new ArrayList<>();
        for (NextStep nextStep : possibleMoves) {
            list.add(new OthellMove(nextStep));
        }
        return list;
    }

    public OthellState next(OthellMove move) {
        NextStep nextStep = move.move();
        return new OthellState(game, position.move(nextStep, player()), random);
    }
}
