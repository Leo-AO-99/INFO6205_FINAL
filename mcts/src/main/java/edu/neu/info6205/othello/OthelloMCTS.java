package edu.neu.info6205.othello;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    // Global timing accumulators (in milliseconds)
    private static long totalSelectTime = 0;
    private static long totalExpandTime = 0;
    private static long totalSimulateTime = 0;
    private static long totalBackpropTime = 0;
    private static long totalTime = 0;

    private static PrintWriter csvWriter;
    private static int stepCount = 0;

    public static void main(String[] args) {
        try {
            csvWriter = new PrintWriter(new FileWriter("mcts_timing_log.csv"));
            csvWriter.println("Step,SelectTime,ExpandTime,SimulateTime,BackpropTime,TotalTime"); // header
        } catch (IOException e) {
            System.err.println("Failed to create CSV file.");
            e.printStackTrace();
            return;
        }
        long seed = System.nanoTime();
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

        System.out.println("==== Final Timing Summary ====");
        System.out.printf("Total Selection Time: %.3f ms\n", totalSelectTime / 1_000_000.0);
        System.out.printf("Total Expansion Time: %.3f ms\n", totalExpandTime / 1_000_000.0);
        System.out.printf("Total Simulation Time: %.3f ms\n", totalSimulateTime / 1_000_000.0);
        System.out.printf("Total Backpropagation Time: %.3f ms\n", totalBackpropTime / 1_000_000.0);
        System.out.printf("Total MCTS Time: %.3f ms\n", totalTime / 1_000_000.0);
        System.out.println("================================");

        csvWriter.close();
        System.out.println("Timing data written to mcts_timing_log.csv");
    }

    static Node<Othello> nextNode(Node<Othello> node) {
        long totalStart = System.nanoTime();
        long selectTime = 0;
        long expandTime = 0;
        long simulateTime = 0;
        long backpropTime = 0;

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            long t1 = System.nanoTime();
            Node<Othello> current = select(node);
            long t2 = System.nanoTime();
            selectTime += (t2 - t1);

            current = expand(current);
            long t3 = System.nanoTime();
            expandTime += (t3 - t2);

            double reward = simulate(current);
            long t4 = System.nanoTime();
            simulateTime += (t4 - t3);

            backPropagate(current, reward);
            long t5 = System.nanoTime();
            backpropTime += (t5 - t4);
        }

        long totalEnd = System.nanoTime();
        long iterationTime = totalEnd - totalStart;

        // Add to global accumulators
        totalSelectTime += selectTime;
        totalExpandTime += expandTime;
        totalSimulateTime += simulateTime;
        totalBackpropTime += backpropTime;
        totalTime += iterationTime;

        // Per-node report
        System.out.println("==== Timing Report for this node ====");
        System.out.println("Selection Time: " + selectTime / 1_000_000.0 + " ms");
        System.out.println("Expansion Time: " + expandTime / 1_000_000.0 + " ms");
        System.out.println("Simulation Time: " + simulateTime / 1_000_000.0 + " ms");
        System.out.println("Backpropagation Time: " + backpropTime / 1_000_000.0 + " ms");
        System.out.println("Total Time for this node: " + iterationTime / 1_000_000.0 + " ms");
        System.out.println("=====================================");
        csvWriter.printf("%d,%.3f,%.3f,%.3f,%.3f,%.3f%n", ++stepCount, selectTime / 1_000_000.0,
                expandTime / 1_000_000.0, simulateTime / 1_000_000.0, backpropTime / 1_000_000.0,
                iterationTime / 1_000_000.0);

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