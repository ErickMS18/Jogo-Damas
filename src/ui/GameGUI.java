package ui;

import model.*;
import io.CsvLogger;
import exceptions.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class GameGUI extends JFrame {
    private Board board;
    private CsvLogger logger;
    private String currentPlayer = "white";

    private int selectedRow = -1;
    private int selectedCol = -1;

    private JLabel whiteCountLabel = new JLabel();
    private JLabel blackCountLabel = new JLabel();

    private JPanel boardPanel;
    private int turnsWithoutCapture = 0;


    public GameGUI(Board board) throws IOException {
        this.board = board;
        this.logger = new CsvLogger("game_log.csv");

        setTitle("Jogo de Damas");
        setSize(600, 650);
        setLayout(new BorderLayout());

        JPanel statusPanel = new JPanel();
        statusPanel.add(whiteCountLabel);
        statusPanel.add(blackCountLabel);

        JButton resetButton = new JButton("Reiniciar Jogo");
        resetButton.addActionListener(e -> resetGame());
        statusPanel.add(resetButton);

        add(statusPanel, BorderLayout.SOUTH);

        boardPanel = new JPanel(new GridLayout(8, 8));
        add(boardPanel, BorderLayout.CENTER);

        updatePieceCounts();
        drawBoard();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }



    private boolean verificarEmpate() {
        // Regra 1: 20 turnos sem captura
        if (turnsWithoutCapture >= 20 && restamSomenteDamas()) return true;


        // Regra 2: repetição de estado 3 vezes
        String estadoAtual = board.toBoardString();
        int repeticoes = Collections.frequency(historicoTabuleiro, estadoAtual);
        if (repeticoes >= 3) return true;

        // Regra 3: 3 peças vs 1 + 20 turnos sem captura
        if (turnsWithoutCapture >= 20 && verificarTresContraUm()) return true;

        return false;
    }


    private boolean verificarTresContraUm() {
        int brancas = 0, pretas = 0;
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Piece p = board.getPiece(linha, coluna);
                if (p != null) {
                    if (p.getColor().equals("white")) brancas++;
                    else pretas++;
                }
            }
        }
        return (brancas == 3 && pretas == 1) || (pretas == 3 && brancas == 1);
    }





    private void drawBoard() {
        boardPanel.removeAll();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                CellPanel cell = new CellPanel(row, col);
                boardPanel.add(cell);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private class CellPanel extends JPanel {
        private final int row, col;

        public CellPanel(int row, int col) {
            this.row = row;
            this.col = col;
            setPreferredSize(new Dimension(75, 75));
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    handleClick(row, col);
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Fundo
            if ((row + col) % 2 == 0) {
                g.setColor(Color.LIGHT_GRAY);
            } else {
                g.setColor(Color.DARK_GRAY);
            }
            g.fillRect(0, 0, getWidth(), getHeight());

            // Destaque de seleção
            if (row == selectedRow && col == selectedCol) {
                g.setColor(Color.YELLOW);
                g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                g.drawRect(3, 3, getWidth() - 6, getHeight() - 6);
            }

            // Desenhar peça
            Piece p = board.getPiece(row, col);
            if (p != null) {
                if (p.getColor().equals("white")) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.BLACK);
                }

                int margin = 10;
                g.fillOval(margin, margin, getWidth() - 2 * margin, getHeight() - 2 * margin);

                // Contorno da peça
                g.setColor(Color.GRAY);
                g.drawOval(margin, margin, getWidth() - 2 * margin, getHeight() - 2 * margin);

                // Dama
                if (p instanceof KingPiece) {
                    g.setColor(Color.GRAY);
                    g.setFont(new Font("Arial", Font.BOLD, 16));
                    g.drawString("K", getWidth() / 2 - 5, getHeight() / 2 + 6);
                }
            }
        }
    }

    private final java.util.List<String> historicoTabuleiro = new java.util.ArrayList<>();



    private void handleClick(int row, int col) {
        if (selectedRow == -1) {
            Piece p = board.getPiece(row, col);
            if (p != null && p.getColor().equals(currentPlayer)) {
                selectedRow = row;
                selectedCol = col;
                System.out.println("Selected: " + row + "," + col);
                drawBoard();  // redraw board to update highlight here
            }
        } else {
            try {
                Piece piece = board.getPiece(selectedRow, selectedCol);

                // Tenta mover a peça, movePiece deve retornar true se houve captura
                boolean capturaEfetuada = board.movePiece(piece, row, col);

                logger.log(currentPlayer + ": " + selectedRow + "," + selectedCol + " -> " + row + "," + col);
                updatePieceCounts();

                // Atualiza histórico e turnos sem captura
                String estadoAtual = board.toBoardString();
                historicoTabuleiro.add(estadoAtual);

                if (capturaEfetuada) {
                    turnsWithoutCapture = 0;
                } else {
                    turnsWithoutCapture++;
                }

                // Corrige a lógica: só conta quando só houver damas
                if (!restamSomenteDamas()) {
                    turnsWithoutCapture = 0;
                }


                // Troca jogador
                currentPlayer = currentPlayer.equals("white") ? "black" : "white";

                // Verifica empate
                if (verificarEmpate()) {
                    JOptionPane.showMessageDialog(this, "O jogo terminou em empate!", "Empate", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        logger.log("Empate");
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(this, "Erro ao registrar empate: " + ioException.getMessage());
                    }
                    resetGame(); // reinicia o jogo após empate
                    return; // evita continuar execução após empate
                }

                // Verifica vitória
                else if (!board.hasAnyMoves(currentPlayer)) {
                    String winner = currentPlayer.equals("white") ? "Black" : "White";

                    Timer timer = new Timer(200, e -> {
                        JOptionPane.showMessageDialog(this, "Game Over! " + winner + " venceu!");
                        try {
                            logger.log("Game Over! Winner: " + winner);
                        } catch (IOException ioException) {
                            JOptionPane.showMessageDialog(this, "Error logging game result: " + ioException.getMessage());
                        }
                        disableBoard();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }

            } catch (InvalidMoveException | OccupiedSquareException | IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            selectedRow = -1;
            selectedCol = -1;
            drawBoard();
        }
    }


    private boolean checkDrawCondition() {
        // Critério simples: empate após 20 turnos sem captura
        if (turnsWithoutCapture >= 20) {
            return true;
        }
        return false;
    }



    private void disableBoard() {
        for (Component comp : boardPanel.getComponents()) {
            comp.setEnabled(false);
        }
    }

    private void updatePieceCounts() {
        int whiteCount = 0, blackCount = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board.getPiece(row, col);
                if (p != null) {
                    if (p.getColor().equals("white")) whiteCount++;
                    else blackCount++;
                }
            }
        }
        whiteCountLabel.setText("White: " + whiteCount);
        blackCountLabel.setText("Black: " + blackCount);
    }

    private void resetGame() {
        try {
            board = (Board) io.Serializer.load("board.dat");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to reset from saved board. Starting new game.");
            board = new Board(); // fallback para novo tabuleiro vazio
        }

        selectedRow = -1;
        selectedCol = -1;
        currentPlayer = "white";

        turnsWithoutCapture = 0;
        historicoTabuleiro.clear();

        updatePieceCounts();
        drawBoard();
        enableBoard();
    }


    private void enableBoard() {
        for (Component comp : boardPanel.getComponents()) {
            comp.setEnabled(true);
        }
    }



    private boolean restamSomenteDamas() {
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Piece p = board.getPiece(linha, coluna);
                if (p != null && !(p instanceof KingPiece)) {
                    return false; // Caso ainda existem peças comuns
                }
            }
        }
        return true; // Se todas as peças restantes são damas
    }


}


