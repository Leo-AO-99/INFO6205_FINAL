
/*
 * Copyright (c) 2024. Robin Hillyard
 */

package edu.neu.info6205.tictactoe;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import edu.neu.info6205.core.Move;
import edu.neu.info6205.core.Node;
import edu.neu.info6205.core.State;

public class MCTS {

    private static final int SIMULATION_COUNT = 5000;
    static double C = 1.414;

    public static long totalSelectTime = 0;
    public static long totalExpandTime = 0;
    public static long totalSimulateTime = 0;
    public static long totalBackpropTime = 0;
    public static long totalTime = 0;
    public static int stepCount = 0;
    private static PrintWriter csvWriter;

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

    public static void benchmark() {
        long seed = System.currentTimeMillis();
        System.out.println("Seed: " + seed);

        try {
            csvWriter = new PrintWriter(new FileWriter("ttt_mcts_timing_log.csv"));
            csvWriter.println("Step,SelectTime,ExpandTime,SimulateTime,BackpropTime,TotalTime");
        } catch (IOException e) {
            System.err.println("Failed to create CSV file.");
            e.printStackTrace();
            return;
        }

        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe(seed).new TicTacToeState()));
        Node<TicTacToe> root = mcts.root;

        while (!root.state().isTerminal()) {
            root = nextNodeWithBenchmark(root);
            System.out.println(showBoard(root.state()));
        }

        System.out.println("Game over");

        System.out.println("==== Final Timing Summary ====");
        System.out.printf("Total Selection Time: %.3f ms%n", totalSelectTime / 1_000_000.0);
        System.out.printf("Total Expansion Time: %.3f ms%n", totalExpandTime / 1_000_000.0);
        System.out.printf("Total Simulation Time: %.3f ms%n", totalSimulateTime / 1_000_000.0);
        System.out.printf("Total Backpropagation Time: %.3f ms%n", totalBackpropTime / 1_000_000.0);
        System.out.printf("Total MCTS Time: %.3f ms%n", totalTime / 1_000_000.0);
        System.out.println("================================");

        csvWriter.close();
        System.out.println("Timing data written to ttt_mcts_timing_log.csv");
    }

    public boolean isTerminal(){
        return root.state().isTerminal();
    }

    public Optional<Integer> winner() {
        return root.state().winner();
    }

    static Node<TicTacToe> nextNode(MCTS mcts) {
        Node<TicTacToe> root = mcts.root;
        return nextNode(root);
    }

    static Node<TicTacToe> nextNodeWithBenchmark(Node<TicTacToe> node) {
        long totalStart = System.nanoTime();
        long selectTime = 0;
        long expandTime = 0;
        long simulateTime = 0;
        long backpropTime = 0;

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            long t1 = System.nanoTime();
            Node<TicTacToe> curNode = select(node);
            long t2 = System.nanoTime();
            selectTime += (t2 - t1);

            curNode = expand(curNode);
            long t3 = System.nanoTime();
            expandTime += (t3 - t2);

            int reward = simulate(curNode);
            long t4 = System.nanoTime();
            simulateTime += (t4 - t3);

            backPropagate(curNode, reward);
            long t5 = System.nanoTime();
            backpropTime += (t5 - t4);
        }

        long totalEnd = System.nanoTime();
        long totalNano = totalEnd - totalStart;

        double selectMs = selectTime / 1_000_000.0;
        double expandMs = expandTime / 1_000_000.0;
        double simulateMs = simulateTime / 1_000_000.0;
        double backpropMs = backpropTime / 1_000_000.0;
        double totalMs = totalNano / 1_000_000.0;

        totalSelectTime += selectTime;
        totalExpandTime += expandTime;
        totalSimulateTime += simulateTime;
        totalBackpropTime += backpropTime;
        totalTime += totalNano;

        csvWriter.printf("%d,%.3f,%.3f,%.3f,%.3f,%.3f%n", ++stepCount,
                selectMs, expandMs, simulateMs, backpropMs, totalMs);

        return new TicTacToeNode(Collections.max(node.children(), Comparator.comparing(c -> c.playouts())).state());
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

        return new TicTacToeNode(bestChild.state());
    }

    static int simulate(Node<TicTacToe> node) {
        State<TicTacToe> simState = node.state();
        int currentPlayer = simState.player();
        while (!simState.isTerminal()) {
            Move<TicTacToe> move = simState.chooseMove(simState.player());
            simState = simState.next(move);
        }

        Optional<Integer> winner = simState.winner();
        int reward = 2;
        if (winner.isEmpty()) {
            return 1;
        } else {
            if (winner.get() == currentPlayer) {
                return 2 - reward;
            } else {
                return reward;
            }
        }
    }

    static void backPropagate(Node<TicTacToe> node, int reward) {
        int player = node.state().player();

        while (node != null) {
            if (node instanceof TicTacToeNode ticNode) {
                int curPlayer = ticNode.state().player();
                ticNode.incrementPlayouts();
                ticNode.addWins(curPlayer == player ? reward : 2 - reward);
                // ticNode.addWins(reward);
                // reward = 2 - reward;
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
        return Collections.max(node.children(), Comparator.comparing(
                n -> ((double) n.wins()) / n.playouts() + C * Math.sqrt(Math.log(parentPlayouts) / n.playouts())));
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
