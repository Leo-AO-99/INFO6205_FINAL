package edu.neu.info6205.othello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

import edu.neu.info6205.core.Move;
import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;
import static edu.neu.info6205.othello.Othello.POSITION_WEIGHTS;

public class OthelloMCTS {

    private static final int SIMULATION_COUNT = 10000;
    private static final double C = 1.414;


    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        System.out.println("Seed: " + seed);
        Othello game = new Othello(seed);
        Node<Othello> root = new OthelloNode(game.start());

        while (!root.state().isTerminal()) {
            root = nextNode(root);
            System.out.println(othellShowBoard(((Othello.OthelloState) root.state()).showBoard()));
        }

        Optional<Integer> winner = root.state().winner();
        if (winner.isPresent()) {
            System.out.println("Game over. Winner: " + (winner.get() == Othello.BLACK ? "Black" : "White"));
        } else {
            System.out.println("Game over. Draw!");
        }
    }

    static Node<Othello> nextNode(Node<Othello> node) {
        for (int i = 0; i < SIMULATION_COUNT; i++) {
            Node<Othello> current = select(node);
            current = expand(current);
            double reward = simulate(current);
            backPropagate(current, reward);
        }

        return Collections.max(node.children(), Comparator.comparing(Node::playouts));
    }

    static Node<Othello> select(Node<Othello> node) {
        while (!node.isLeaf()) {
            if (node.children().isEmpty()) {
                return node;
            }
            for (Node<Othello> child : node.children()) {
                if (child.playouts() == 0) {
                    return child;
                }
            }
            node = bestUCTNode(node);
        }
        return node;
    }

    static Node<Othello> expand(Node<Othello> node) {
        if (node.isLeaf())
            return node;

        if (node.children().isEmpty()) {
            State<Othello> state = node.state();
            ArrayList<Move<Othello>> moves = new ArrayList<>(state.moves(state.player()));

            moves.sort((a, b) -> {
                OthelloMove moveA = (OthelloMove) a;
                OthelloMove moveB = (OthelloMove) b;
                return Integer.compare(
                        getMoveScore(moveB.move()),
                        getMoveScore(moveA.move()));
            });

            for (Move<Othello> move : moves) {
                State<Othello> childState = state.next(move);
                node.addChild(childState);
            }
        }

        return node.children().iterator().next();
    }

    private static int getMoveScore(NextStep step) {
        return POSITION_WEIGHTS[step.x][step.y];
    }

    static double simulate(Node<Othello> node) {
        State<Othello> state = node.state();
        int currentPlayer = state.player();

        while (!state.isTerminal()) {
            ArrayList<Move<Othello>> moves = new ArrayList<>(state.moves(state.player()));
            if (moves.isEmpty())
                break;

            int topK = Math.min(3, moves.size());

            PriorityQueue<Move<Othello>> heap = new PriorityQueue<>(topK, Comparator.comparingInt(a -> {
                OthelloMove move = (OthelloMove) a;
                return getMoveScore(move.move());
            }));

            for (Move<Othello> move : moves) {
                heap.offer(move);
                if (heap.size() > topK) {
                    heap.poll();
                }
            }

            ArrayList<Move<Othello>> topMoves = new ArrayList<>(heap);
            Move<Othello> selected = topMoves.get(state.random().nextInt(topMoves.size()));
            state = state.next(selected);
        }

        Optional<Integer> winner = state.winner();
        if (winner.isEmpty())
            return 1.0;
        return winner.get() == currentPlayer ? state.reward() : 2.0 - state.reward();
    }

    static void backPropagate(Node<Othello> node, double reward) {
        boolean rewardForWhite = node.white();
        while (node != null) {
            if (node instanceof OthelloNode oNode) {
                oNode.incrementPlayouts();
                if (node.white() == rewardForWhite) {
                    oNode.addWins(reward);
                } else {
                    oNode.addWins(2.0 - reward);
                }
                node = oNode.getParent();
            } else {
                throw new RuntimeException("Node is not OthellNode");
            }
        }
    }

    static Node<Othello> bestUCTNode(Node<Othello> node) {
        int parentPlayouts = node.playouts();
        return Collections.max(node.children(), Comparator.comparing(
                n -> (n.wins()) / (double) n.playouts() + C * Math.sqrt(Math.log(parentPlayouts) / n.playouts())));
    }

    public static String othellShowBoard(String state) {
        String board = state;
        board = board.replace("0", "_");
        board = board.replace("1", "●");
        board = board.replace("2", "○");

        return board;
    }
}
