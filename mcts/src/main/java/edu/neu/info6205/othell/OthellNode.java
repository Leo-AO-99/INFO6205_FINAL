package edu.neu.info6205.othell;

import java.util.ArrayList;
import java.util.Collection;

import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

public class OthellNode implements Node<Othell> {

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'children'");
    }

    @Override
    public void backPropagate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'backPropagate'");
    }

    @Override
    public void addChild(State<Othell> state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addChild'");
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    @Override
    public boolean isExpandable() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isExpandable'");
    }

    @Override
    public Node<Othell> expand() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'expand'");
    }
    

    public OthellNode(State<Othell> state) {
        this.state = state;
        this.children = new ArrayList<>();
    }

    private final State<Othell> state;
    private final ArrayList<Node<Othell>> children;

    private int wins;
    private int playouts;
}
