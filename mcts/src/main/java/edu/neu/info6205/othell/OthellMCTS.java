package edu.neu.info6205.othell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import edu.neu.info6205.core.Move;
import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

public class OthellMCTS {

    private static final int SIMULATION_COUNT = 1000;
    private static final double C = 1.414;

    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        System.out.println("Seed: " + seed);
        Othell game = new Othell(seed);
        Node<Othell> root = new OthellNode(game.start());

        while (!root.state().isTerminal()) {
            root = nextNode(root);
            System.out.println(othellShowBoard(((Othell.OthellState) root.state()).showBoard()));
        }

        Optional<Integer> winner = root.state().winner();
        if (winner.isPresent()) {
            System.out.println("Game over. Winner: " + (winner.get() == Othell.BLACK ? "Black" : "White"));
        } else {
            System.out.println("Game over. Draw!");
        }
    }

    static Node<Othell> nextNode(Node<Othell> node) {
        for (int i = 0; i < SIMULATION_COUNT; i++) {
            Node<Othell> current = select(node);
            current = expand(current);
            int reward = simulate(current);
            backPropagate(current, reward);
        }

        return Collections.max(node.children(), Comparator.comparing(Node::playouts));
    }

    static Node<Othell> select(Node<Othell> node) {
        while (!node.isLeaf()) {
            if (node.children().isEmpty()) {
                return node;
            }
            for (Node<Othell> child : node.children()) {
                if (child.playouts() == 0) {
                    return child;
                }
            }
            node = bestUCTNode(node);
        }
        return node;
    }

    static Node<Othell> expand(Node<Othell> node) {
        if (node.isLeaf())
            return node;
        if (node.children().isEmpty()) {
            node.expandAll();
        }
        ArrayList<Node<Othell>> children = new ArrayList<>(node.children());
        return children.get(node.state().random().nextInt(children.size()));
    }

    static int simulate(Node<Othell> node) {
        State<Othell> state = node.state();
        int currentPlayer = state.player();

        while (!state.isTerminal()) {
            Move<Othell> move = state.chooseMove(state.player());
            state = state.next(move);
        }

        Optional<Integer> winner = state.winner();
        if (winner.isEmpty())
            return 1;
        return winner.get() == currentPlayer ? 2 : 0;
    }

    static void backPropagate(Node<Othell> node, int reward) {
        boolean rewardForWhite = node.white();
        while (node != null) {
            if (node instanceof OthellNode oNode) {
                oNode.incrementPlayouts();
                if (node.white() == rewardForWhite) {
                    oNode.addWins(reward);
                } else {
                    oNode.addWins(2 - reward);
                }
                node = oNode.getParent();
            } else {
                throw new RuntimeException("Node is not OthellNode");
            }
        }
    }

    static Node<Othell> bestUCTNode(Node<Othell> node) {
        int parentPlayouts = node.playouts();
        return Collections.max(node.children(), Comparator.comparing(
                n -> ((double) n.wins()) / n.playouts() + C * Math.sqrt(Math.log(parentPlayouts) / n.playouts())));
    }

    public static String othellShowBoard(String state) {
        String board = state;
        board = board.replace("0", "_");
        board = board.replace("1", "●");
        board = board.replace("2", "○");

        return board;
    }
}
