package edu.neu.info6205.othello;

import java.util.ArrayList;
import java.util.Collection;

import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

public class OthelloNode implements Node<Othello> {

    public OthelloNode getParent() {
        return parent;
    }

    public void incrementPlayouts() {
        playouts++;
    }

    public void addWins(double reward) {
        wins += reward;
    }

    @Override
    public boolean isLeaf() {
        return state.isTerminal();
    }

    @Override
    public State<Othello> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == Othello.WHITE;
    }

    @Override
    public Collection<Node<Othello>> children() {
        return children;
    }

    @Override
    public void backPropagate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'backPropagate'");
    }

    @Override
    public void addChild(State<Othello> state) {
        children.add(new OthelloNode(state, this));
    }

    @Override
    public double wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }
    

    public OthelloNode(State<Othello> state) {
        this(state, null);
    }

    public OthelloNode(State<Othello> state, OthelloNode parent) {
        this.state = state;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    private final State<Othello> state;
    private final ArrayList<Node<Othello>> children;

    private double wins;
    private int playouts;
    private OthelloNode parent;
}
