package edu.neu.info6205.othell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OthelloGUI extends JFrame {
    private static final int TILE_SIZE = 60;
    private static final int BOARD_SIZE = 8;

    private Othell game;
    private Othell.OthellState state;
    private JPanel boardPanel;

    public OthelloGUI() {
        game = new Othell(System.currentTimeMillis());
        state = (Othell.OthellState) game.start();

        setTitle("Othello (MCTS vs Human)");
        setSize(TILE_SIZE * BOARD_SIZE + 20, TILE_SIZE * BOARD_SIZE + 40);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
                handleClick(e);
            }
        });

        add(boardPanel);
        setVisible(true);
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
                state = (Othell.OthellState) state.next(move);
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
        OthellNode root = new OthellNode(state);
        OthellNode next = new OthellNode(OthellMCTS.nextNode(root).state());
        state = (Othell.OthellState) next.state();
        repaint();
        checkEnd();
    }

    private void checkEnd() {
        if (state.isTerminal()) {
            Optional<Integer> winner = state.winner();
            String message = winner.map(w -> w == Othell.BLACK ? "Black wins!" : "White wins!")
                    .orElse("It's a draw!");
            JOptionPane.showMessageDialog(this, message);
        }
    }

    private void drawBoard(Graphics g) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                g.setColor(Color.GREEN);
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
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OthelloGUI::new);
    }
}
