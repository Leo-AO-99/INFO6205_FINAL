/*
 * Copyright (c) 2024. Robin Hillyard
 */

package edu.neu.info6205.tictactoe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import edu.neu.info6205.core.Move;
import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

public class TicTacToeNode implements Node<TicTacToe> {

    /**
     * @return true if this node is a leaf node (in which case no further
     *         exploration is possible).
     */
    @Override
    public boolean isLeaf() {
        return state().isTerminal();
    }

    /**
     * @return the State of the Game G that this Node represents.
     */
    @Override
    public State<TicTacToe> state() {
        return state;
    }

    /**
     * Method to determine if the player who plays to this node is the opening
     * player (by analogy with chess).
     * For this method, we assume that X goes first so is "white."
     * NOTE: this assumes a two-player game.
     *
     * @return true if this node represents a "white" move; false for "black."
     */
    @Override
    public boolean white() {
        return state.player() == state.game().opener();
    }

    /**
     * @return the children of this Node.
     */
    @Override
    public Collection<Node<TicTacToe>> children() {
        return children;
    }

    /**
     * Method to add a child to this Node.
     *
     * @param state the State for the new chile.
     */
    @Override
    public void addChild(State<TicTacToe> state) {
        children.add(new TicTacToeNode(state));
    }

    /**
     * This method sets the number of wins and playouts according to the children
     * states.
     */
    @Override
    public void backPropagate() {
        playouts = 0;
        wins = 0;
        for (Node<TicTacToe> child : children) {
            wins += child.wins();
            playouts += child.playouts();
        }
    }

    /**
     * @return the score for this Node and its descendents a win is worth 2 points,
     *         a draw is worth 1 point.
     */
    @Override
    public int wins() {
        return wins;
    }

    /**
     * @return the number of playouts evaluated (including this node). A leaf node
     *         will have a playouts value of 1.
     */
    @Override
    public int playouts() {
        return playouts;
    }

    @Override
    public boolean isExpandable() {
        return possibleMoves.hasNext();
    }

    @Override
    public Node<TicTacToe> expand() {
        if (!isExpandable()) {
            throw new RuntimeException("Node is not expandable");
        }
        Move<TicTacToe> move = possibleMoves.next();
        addChild(state.next(move));
        return children.get(children.size() - 1);
    }

    public TicTacToeNode(State<TicTacToe> state) {
        this.state = state;
        this.possibleMoves = state.moveIterator(state.player());
        children = new ArrayList<>();
        initializeNodeData();
    }

    private void initializeNodeData() {
        if (isLeaf()) {
            playouts = 1;
            Optional<Integer> winner = state.winner();
            if (winner.isPresent())
                wins = 2; // CONSIDER check that the winner is the correct player. We shouldn't need to.
            else
                wins = 1; // a draw.
        }
    }

    private final State<TicTacToe> state;
    private final ArrayList<Node<TicTacToe>> children;

    private int wins;
    private int playouts;

    private final Iterator<Move<TicTacToe>> possibleMoves;
}