/*
 * Copyright (c) 2024. Robin Hillyard
 */

package edu.neu.info6205.tictactoe;

import java.util.ArrayList;
import java.util.Collections;

import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

/**
 * Class to represent a Monte Carlo Tree Search for TicTacToe.
 */
public class MCTS {

    private static final int SIMULATION_COUNT = 12;

    public static void main(String[] args) {
        long seed = 1742849870735L;
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe(seed).new TicTacToeState()));
        Node<TicTacToe> root = mcts.root;

        while (!root.state().isTerminal()) {
            root = nextNode(root);
            System.out.println(showBoard(root.state()));
        }
        // This is where you process the MCTS to try to win the game.
    }

    static Node<TicTacToe> nextNode(Node<TicTacToe> node) {
        for (int i = 0; i < SIMULATION_COUNT; i++) {
            // select and expand
            Node<TicTacToe> curNode = select_and_expand(node);
            System.out.println(curNode.state().player());
            System.out.println(showBoard(curNode.state()));
            // simulate
            int reward = simulate(curNode);
            // backpropagate
            backPropagate(curNode, reward);
        }
        // TODO choose the best child
        return node;
    }

    static int simulate(Node<TicTacToe> node) {
        int player = node.state().player();
        int opponent = 1 - player;
        // TODO simulate the game until the end


        return 1;
    }

    static void backPropagate(Node<TicTacToe> node, int reward) {
        // TODO backpropagate the reward
    }

    static Node<TicTacToe> select_and_expand(Node<TicTacToe> node) {
        // TODO it seems that whether expand all children or just one is not important, because when choosing the best UCT child, playout = 0 is the first choice
        while (!node.isLeaf()) {
            if (node.isExpandable()) {
                return expand(node);
            }
            node = BestUCTNode(node);
        }
        return node;
    }

    static Node<TicTacToe> expand(Node<TicTacToe> node) {
        if (!node.isExpandable()) {
            throw new RuntimeException("Node is not expandable");
        }
        return node.expand();
    }

    static Node<TicTacToe> BestUCTNode(Node<TicTacToe> node) {
        int parentPlayouts = node.playouts();

        
        double bestUCT = -1;
        // Random choose a child with the best UCT
        ArrayList<Node<TicTacToe>> children = new ArrayList<>(node.children());
        Collections.shuffle(children, node.state().random());
        ArrayList<Node<TicTacToe>> bestChildren = new ArrayList<>();
        for (Node<TicTacToe> child : children) {
            // make sure we will simulate all children
            int childPlayouts = child.playouts();
            if (childPlayouts == 0) {
                return child;
            }
            double curUCT = (double) child.wins() / childPlayouts + Math.sqrt(2) * Math.sqrt(Math.log(parentPlayouts) / childPlayouts);
            if (curUCT > bestUCT) {
                bestUCT = curUCT;
                bestChildren.clear();
                bestChildren.add(child);
            } else if (curUCT == bestUCT) {
                bestChildren.add(child);
            }
        }

        return bestChildren.get(node.state().random().nextInt(bestChildren.size()));
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