package edu.neu.info6205.othell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OthelloGUI extends JFrame {
    private static final int TILE_SIZE = 60;
    private static final int BOARD_SIZE = 8;

    private Othell game;
    private Othell.OthellState state;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private List<NextStep> validMoves;
    private final Deque<Othell.OthellState> history = new ArrayDeque<>();
    private boolean aiVsAi = false;

    public OthelloGUI() {
        setTitle("Othello (MCTS vs Human)");
        setSize(TILE_SIZE * BOARD_SIZE + 20, TILE_SIZE * BOARD_SIZE + 120);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        statusLabel = new JLabel();

        initGame();

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE));
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!aiVsAi)
                    handleClick(e);
            }
        });

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undo());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> reset());

        JButton aiPlayButton = new JButton("AI vs AI");
        aiPlayButton.addActionListener(e -> {
            aiVsAi = true;
            new Thread(this::autoPlay).start();
        });

        statusLabel = new JLabel();
        updateStatusLabel();

        JPanel controlPanel = new JPanel();
        controlPanel.add(undoButton);
        controlPanel.add(resetButton);
        controlPanel.add(aiPlayButton);
        controlPanel.add(statusLabel);

        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void initGame() {
        game = new Othell(System.currentTimeMillis());
        state = (Othell.OthellState) game.start();
        validMoves = extractValidMoves(state);
        history.clear();
        aiVsAi = false;
        updateStatusLabel();
    }

    private void handleClick(MouseEvent e) {
        if (state.isTerminal())
            return;

        int row = e.getY() / TILE_SIZE;
        int col = e.getX() / TILE_SIZE;
        int player = state.player();

        for (OthellMove move : state.moves(player).stream().map(m -> (OthellMove) m).toList()) {
            NextStep step = move.move();
            if (step.x == row && step.y == col) {
                history.push(state);
                state = (Othell.OthellState) state.next(move);
                validMoves = extractValidMoves(state);
                updateStatusLabel();
                repaint();
                checkEnd();
                SwingUtilities.invokeLater(this::aiMove);
                break;
            }
        }
    }

    private void aiMove() {
        if (state.isTerminal())
            return;
        history.push(state);
        OthellNode root = new OthellNode(state);
        OthellNode next = new OthellNode(OthellMCTS.nextNode(root).state());
        state = (Othell.OthellState) next.state();
        validMoves = extractValidMoves(state);
        updateStatusLabel();
        repaint();
        checkEnd();
    }

    private void autoPlay() {
        while (!state.isTerminal()) {
            aiMove();
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void undo() {
        if (!history.isEmpty()) {
            state = history.pop();
            validMoves = extractValidMoves(state);
            updateStatusLabel();
            repaint();
        }
    }

    private void reset() {
        initGame();
        repaint();
    }

    private void checkEnd() {
        if (state.isTerminal()) {
            Optional<Integer> winner = state.winner();
            String message = winner.map(w -> w == Othell.BLACK ? "Black wins!" : "White wins!")
                    .orElse("It's a draw!");
            JOptionPane.showMessageDialog(this, message);
        }
    }

    private List<NextStep> extractValidMoves(Othell.OthellState currentState) {
        List<NextStep> moves = new ArrayList<>();
        for (OthellMove move : currentState.moves(currentState.player()).stream().map(m -> (OthellMove) m).toList()) {
            moves.add(move.move());
        }
        return moves;
    }

    private void updateStatusLabel() {
        String player = (state.player() == Othell.BLACK) ? "Black ●" : "White ○";
        statusLabel.setText(" Current Turn: " + player);
    }

    private void drawBoard(Graphics g) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                g.setColor(Color.GRAY);
                g.fillRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                int val = state.showBoard().charAt(i * 9 + j) - '0';
                if (val == Othell.BLACK) {
                    g.setColor(Color.BLACK);
                    g.fillOval(j * TILE_SIZE + 10, i * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                } else if (val == Othell.WHITE) {
                    g.setColor(Color.WHITE);
                    g.fillOval(j * TILE_SIZE + 10, i * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                } else {
                    for (NextStep step : validMoves) {
                        if (step.x == i && step.y == j) {
                            g.setColor(new Color(255, 255, 0, 128));
                            g.fillOval(j * TILE_SIZE + 20, i * TILE_SIZE + 20, TILE_SIZE - 40, TILE_SIZE - 40);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OthelloGUI::new);
    }
}
