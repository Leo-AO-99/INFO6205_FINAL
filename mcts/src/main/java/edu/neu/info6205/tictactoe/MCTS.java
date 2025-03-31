/*
 * Copyright (c) 2024. Robin Hillyard
 */

package edu.neu.info6205.tictactoe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import edu.neu.info6205.core.Move;
import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

/**
 * Class to represent a Monte Carlo Tree Search for TicTacToe.
 */
public class MCTS {

    private static final int SIMULATION_COUNT = 5000;
    static double C = 1.414;


    public static void main(String[] args) {
        // long seed = 1743396199967L;
        long seed = System.currentTimeMillis();
        
        System.out.println("Seed: " + seed);
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe(seed).new TicTacToeState()));
        Node<TicTacToe> root = mcts.root;

        while (!root.state().isTerminal()) {
            root = nextNode(root);
            System.out.println(showBoard(root.state()));
        }
        System.out.println("Game over");
        System.exit(0);
    }

    static Node<TicTacToe> nextNode(Node<TicTacToe> node) {
        for (int i = 0; i < SIMULATION_COUNT; i++) {
            // select
            Node<TicTacToe> curNode = select(node);
            // expand
            curNode = expand(curNode);
            // simulate
            int reward = simulate(curNode);
            // backpropagate
            backPropagate(curNode, reward);
        }

        // Node<TicTacToe> bestChild = Collections.max(node.children(), Comparator.comparing(c -> c.wins() / c.playouts()));
        Node<TicTacToe> bestChild = Collections.max(node.children(), Comparator.comparing(c -> c.playouts()));

        // return bestChild;
        return new TicTacToeNode(bestChild.state());
    }

    static int simulate(Node<TicTacToe> node) {
        
        boolean invert = true;
        State<TicTacToe> simState = node.state();  
        int currentPlayer = simState.player();
        while (!simState.isTerminal()) {
            Move<TicTacToe> move = simState.chooseMove(simState.player());
            simState = simState.next(move);
            invert = !invert;
        }

        Optional<Integer> winner = simState.winner();
        int reward = 2;
        if (winner.isEmpty()) {
            return 1;
        } else {
            // do same thing as invert
            if (winner.get() == currentPlayer) {
                return 2 - reward;
            } else {
                return reward;
            }

            // if (invert) {
            //     return reward;
            // } else {
            //     return 2 - reward;
            // }
        }
    }

    static void backPropagate(Node<TicTacToe> node, int reward) {
        while (node != null) {
            if (node instanceof TicTacToeNode ticNode) {
                ticNode.incrementPlayouts();
                ticNode.addWins(reward);
                reward = 2 - reward;
                node = ticNode.getParent();
            } else {
                throw new RuntimeException("Node is not a TicTacToeNode");
            }
        }
    }

    static Node<TicTacToe> select(Node<TicTacToe> node) {
        while (!node.isLeaf()) {
            if (node.children().isEmpty()) {
                return node;
            }
            for (Node<TicTacToe> child : node.children()) {
                if (child.playouts() == 0) {
                    return child;
                }
            }
            node = BestUCTNode(node);
        }
        return node;
    }

    static Node<TicTacToe> expand(Node<TicTacToe> node) {
        if (node.isLeaf()) {
            return node;
        }
        if (node.children().isEmpty()) {
            node.expandAll();
        }
        ArrayList<Node<TicTacToe>> children = new ArrayList<>(node.children());
        return children.get(node.state().random().nextInt(children.size()));
    }

    static Node<TicTacToe> BestUCTNode(Node<TicTacToe> node) {
        int parentPlayouts = node.playouts();
        return Collections.max(node.children(), Comparator.comparing(n -> ((double) n.wins()) / n.playouts() + C * Math.sqrt(Math.log(parentPlayouts) / n.playouts())));
    }

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    public static String showBoard(State<TicTacToe> state) {
        String board = state.toString();
        board = board.replace("-1", "_");
        board = board.replace("1", "X");
        board = board.replace("0", "O");
        board = board.replace(",", " ");

        return board;
    }

    private final Node<TicTacToe> root;
}