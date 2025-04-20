package edu.neu.info6205.othello;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import edu.neu.info6205.core.Game;
import edu.neu.info6205.core.Move;
import edu.neu.info6205.core.State;

public class Othello implements Game<Othello> {

    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int EMPTY = 0;

    public static final int[][] POSITION_WEIGHTS = {
        { 100, -25, 10, 5, 5, 10, -25, 100 },
        { -25, -50, -2, -2, -2, -2, -50, -25 },
        { 10, -2, 5, 1, 1, 5, -2, 10 },
        { 5, -2, 1, 0, 0, 1, -2, 5 },
        { 5, -2, 1, 0, 0, 1, -2, 5 },
        { 10, -2, 5, 1, 1, 5, -2, 10 },
        { -25, -50, -2, -2, -2, -2, -50, -25 },
        { 100, -25, 10, 5, 5, 10, -25, 100 }
    };

    private static final double wholeScore = 116;

    private final Random random;

    @Override
    public State<Othello> start() {
        OthelloPosition position = new OthelloPosition();
        for (NextStep nextStep : position.getPossibleMoves()) {
            System.out.println(nextStep);
        }
        return new OthelloState(position);
    }

    @Override
    public int opener() {
        return BLACK;
    }

    public Othello() {
        this(System.currentTimeMillis());
    }

    public Othello(Random random) {
        this.random = random;
    }

    public Othello(long seed) {
        this(new Random(seed));
    }
    
    

    class OthelloState implements State<Othello> {

        private final OthelloPosition position;
        private final Othello game;
        private final int player;
    
        public OthelloState(OthelloPosition position) {
            this.position = position;
            this.game = Othello.this;
            this.player = position.getPlayer();
        }
    
        public OthelloState() {
            this.game = Othello.this;
            this.position = new OthelloPosition();
            this.player = position.getPlayer();
        }

        public String showBoard() {
            return position.showBoard();
        }

        @Override
        public double reward() {
            Optional<Integer> winner = winner();
            if (winner.isEmpty()) {
                return 1.0;
            }
            int w = winner.get();
            double reward = 0.0;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (position.getPiece(i, j) == w) {
                        reward += POSITION_WEIGHTS[i][j];
                    }
                }
            }

            if (reward > wholeScore) {
                return 2.0;
            } else if (reward < -wholeScore) {
                return 1.0;
            }

            return reward / wholeScore + 1.0;
        }

        @Override
        public Othello game() {
            return this.game;
        }
    
        @Override
        public boolean isTerminal() {
            return this.position.isEnd();
        }
    
        @Override
        public int player() {
            return this.position.getPlayer();
        }
    
        @Override
        public Optional<Integer> winner() {
            return this.position.winner();
        }
    
        @Override
        public Random random() {
            return random;
        }
    
        @Override
        public Collection<Move<Othello>> moves(int player) {
            if (player != this.player) {
                return new ArrayList<>();
            }
            List<NextStep> possibleMoves = position.getPossibleMoves();
            ArrayList<Move<Othello>> list = new ArrayList<>();
            for (NextStep nextStep : possibleMoves) {
                list.add(new OthelloMove(nextStep));
            }
            return list;
        }
    
        @Override
        public State<Othello> next(Move<Othello> move) {
            OthelloMove othellMove = (OthelloMove) move;
            if (othellMove.player() != player()) {
                throw new IllegalArgumentException("Invalid move");
            }
            NextStep nextStep = othellMove.move();
            return new OthelloState(position.move(nextStep, player()));
        }
    }
}
