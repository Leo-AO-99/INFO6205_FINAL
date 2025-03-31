package edu.neu.info6205.othell;

import java.util.ArrayList;
import java.util.Collection;

import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

public class OthellNode implements Node<Othell> {

    public OthellNode getParent() {
        return parent;
    }

    public void incrementPlayouts() {
        playouts++;
    }

    public void addWins(int reward) {
        wins += reward;
    }

    @Override
    public boolean isLeaf() {
        return state.isTerminal();
    }

    @Override
    public State<Othell> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == Othell.WHITE;
    }

    @Override
    public Collection<Node<Othell>> children() {
        return children;
    }

    @Override
    public void backPropagate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'backPropagate'");
    }

    @Override
    public void addChild(State<Othell> state) {
        children.add(new OthellNode(state, this));
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }
    

    public OthellNode(State<Othell> state) {
        this(state, null);
    }

    public OthellNode(State<Othell> state, OthellNode parent) {
        this.state = state;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    private final State<Othell> state;
    private final ArrayList<Node<Othell>> children;

    private int wins;
    private int playouts;
    private OthellNode parent;
}
