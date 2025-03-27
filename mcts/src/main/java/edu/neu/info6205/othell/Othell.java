package edu.neu.info6205.othell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import edu.neu.info6205.core.Game;
import edu.neu.info6205.core.Move;
import edu.neu.info6205.core.State;

public class Othell implements Game<Othell> {

    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int EMPTY = 0;

    private final Random random;

    @Override
    public State<Othell> start() {
        OthellPosition position = new OthellPosition();
        for (NextStep nextStep : position.getPossibleMoves()) {
            System.out.println(nextStep);
        }
        return new OthellState(position);
    }

    @Override
    public int opener() {
        return BLACK;
    }

    public Othell(Random random) {
        this.random = random;
    }

    public Othell(long seed) {
        this(new Random(seed));
    }
    
    

    class OthellState implements State<Othell> {

        private final OthellPosition position;
        private final Othell game;
    
        public OthellState(OthellPosition position) {
            this.position = position;
            this.game = Othell.this;
        }
    
        public OthellState() {
            this.game = Othell.this;
            this.position = new OthellPosition();
        }

        public String showBoard() {
            return position.showBoard();
        }
    
        @Override
        public Othell game() {
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
        public Collection<Move<Othell>> moves(int player) {
            List<NextStep> possibleMoves = position.getPossibleMoves();
            ArrayList<Move<Othell>> list = new ArrayList<>();
            for (NextStep nextStep : possibleMoves) {
                list.add(new OthellMove(nextStep));
            }
            return list;
        }
    
        @Override
        public State<Othell> next(Move<Othell> move) {
            OthellMove othellMove = (OthellMove) move;
            NextStep nextStep = othellMove.move();
            return new OthellState(position.move(nextStep, player()));
        }
    }
}
