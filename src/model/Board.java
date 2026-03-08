package model;

import exceptions.InvalidMoveException;
import exceptions.OccupiedSquareException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {

    // Initialize the board
    private final Piece[][] grid = new Piece[8][8];
    int BOARD_SIZE = 8;

    // Method for placing pieces on the board
    public void placePiece(Piece piece) {
        grid[piece.getRow()][piece.getCol()] = piece;
    }

    // Get piece at coordinates
    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }

    // Check if position exists in the board
    boolean isInsideBoard(int r, int c) {
        return r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE;
    }

    // Checks if selected piece is of the opposite color
    boolean isOpponentPiece(Piece p, Piece currentPiece) {
        if (p == null) return false;
        return !p.getColor().equals(currentPiece.getColor());
    }

    private void simulateCapture(int fromRow, int fromCol,
                                 int capturedRow, int capturedCol,
                                 int landingRow, int landingCol,
                                 Piece piece) {
        grid[fromRow][fromCol] = null;
        grid[capturedRow][capturedCol] = null;
        grid[landingRow][landingCol] = piece;
        piece.move(landingRow, landingCol);
    }

    private void undoSimulateCapture(int fromRow, int fromCol,
                                     int capturedRow, int capturedCol,
                                     int landingRow, int landingCol,
                                     Piece piece, Piece capturedPiece) {
        grid[fromRow][fromCol] = piece;
        grid[capturedRow][capturedCol] = capturedPiece;
        grid[landingRow][landingCol] = null;
        piece.move(fromRow, fromCol);
    }

    // find all captures for a piece
    public void findCaptures(int fromRow, int fromCol, int currRow, int currCol,
                             Piece piece, List<Position> capturedSoFar, List<Move> moves) {

        // Check for kings (they use different logic)
        boolean isKing = piece instanceof KingPiece;
        // Possible movement directions:
        int[][] directions = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };

        // Check in every direction
        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];

            // Capture logic for kings
            if (isKing) {
                // Check next space
                int scanRow = currRow + dx;
                int scanCol = currCol + dy;

                // Skip ahead until a piece is found, or until the end of the board is reached
                while (isInsideBoard(scanRow, scanCol) && grid[scanRow][scanCol] == null) {
                    scanRow += dx;
                    scanCol += dy;
                }

                if (isInsideBoard(scanRow, scanCol)) {
                    Piece target = grid[scanRow][scanCol];
                    if (isOpponentPiece(target, piece)) {
                        // First possible space where the king piece would land after capture:
                        int landingRow = scanRow + dx;
                        int landingCol = scanCol + dy;

                        // If it's a clear space:
                        while (isInsideBoard(landingRow, landingCol) && grid[landingRow][landingCol] == null) {
                            // Check if the piece has already been captured during this move:
                            boolean alreadyCaptured = false;
                            for (Position cap : capturedSoFar) {
                                if (cap.row == scanRow && cap.col == scanCol) {
                                    alreadyCaptured = true;
                                    break;
                                }
                            }
                            if (alreadyCaptured) break;

                            // If it's a new capture, continue:

                            Piece capturedPiece = grid[scanRow][scanCol];

                            // Temporarily make the move:
                            simulateCapture(currRow, currCol, scanRow, scanCol, landingRow, landingCol, piece);

                            // Add the position to list of captures
                            List<Position> newCaptured = new ArrayList<>(capturedSoFar);
                            newCaptured.add(new Position(scanRow, scanCol));

                            // Recursively find all captures from that point on (for multi captures)
                            findCaptures(fromRow, fromCol, landingRow, landingCol, piece, newCaptured, moves);

                            final int finalLandingRow = landingRow;
                            final int finalLandingCol = landingCol;

                            // Only add the move to the list of move if there are no duplicates
                            if (moves.stream().noneMatch(m ->
                                    m.toRow == finalLandingRow && m.toCol == finalLandingCol &&
                                            m.capturedPositions.size() == newCaptured.size())) {
                                Move move = new Move(fromRow, fromCol, landingRow, landingCol);
                                move.capturedPositions.addAll(newCaptured);
                                moves.add(move);
                            }

                            // Undo the simulated capture
                            undoSimulateCapture(currRow, currCol, scanRow, scanCol, landingRow, landingCol, piece, capturedPiece);

                            // check the next space
                            landingRow += dx;
                            landingCol += dy;
                        }
                    }
                }
            } else { // Logic for normal pieces

                // enemy will be next square, landing space will be the next after that
                int enemyRow = currRow + dx;
                int enemyCol = currCol + dy;
                int landingRow = enemyRow + dx;
                int landingCol = enemyCol + dy;

                // Check if the landing space is valid
                if (isInsideBoard(landingRow, landingCol) && grid[landingRow][landingCol] == null) {
                    Piece enemy = grid[enemyRow][enemyCol];
                    // Check if the piece is an opponent
                    if (isOpponentPiece(enemy, piece)) {
                        boolean alreadyCaptured = false;
                        // Check if the piece has already been captured
                        // (to avoid an endless loop in checking multi captures)
                        for (Position cap : capturedSoFar) {
                            if (cap.row == enemyRow && cap.col == enemyCol) {
                                alreadyCaptured = true;
                                break;
                            }
                        }
                        if (alreadyCaptured) continue;

                        // Simulate the capture
                        Piece capturedPiece = grid[enemyRow][enemyCol];
                        simulateCapture(currRow, currCol, enemyRow, enemyCol, landingRow, landingCol, piece);

                        // Add captured position to array
                        List<Position> newCaptured = new ArrayList<>(capturedSoFar);
                        newCaptured.add(new Position(enemyRow, enemyCol));

                        // Recursively find captures from the landing point
                        findCaptures(fromRow, fromCol, landingRow, landingCol, piece, newCaptured, moves);

                        // only add the move to the array if there are no duplicates
                        if (moves.stream().noneMatch(m ->
                                m.toRow == landingRow && m.toCol == landingCol &&
                                        m.capturedPositions.size() == newCaptured.size())) {
                            Move move = new Move(fromRow, fromCol, landingRow, landingCol);
                            move.capturedPositions.addAll(newCaptured);
                            moves.add(move);
                        }

                        // Undo simulated capture
                        undoSimulateCapture(currRow, currCol, enemyRow, enemyCol, landingRow, landingCol, piece, capturedPiece);
                    }
                }
            }
        }
    }

    // Runs findCaptures for every piece of a certain color:
    public List<Move> getAllCapturesForPlayer(String playerColor) {
        List<Move> allMoves = new ArrayList<>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Piece p = grid[r][c];
                if (p != null && p.getColor().equals(playerColor)) {
                    findCaptures(r, c, r, c, p, new ArrayList<>(), allMoves);
                }
            }
        }
        return allMoves;
    }

    public List<Move> getMaxCaptureMovesForPlayer(String playerColor) {
        // Get all possible captures
        List<Move> allCaptures = getAllCapturesForPlayer(playerColor);
        int maxCaptureCount = 0;
        for (Move m : allCaptures) {
            // Find the highest number of captures possible in one move
            maxCaptureCount = Math.max(maxCaptureCount, m.capturedPositions.size());
        }
        final int maxCount = maxCaptureCount;

        // Create an array of moves that have the highest number of captures
        List<Move> maxMoves = new ArrayList<>();
        for (Move m : allCaptures) {
            if (m.capturedPositions.size() == maxCount) {
                maxMoves.add(m);
            }
        }
        return maxMoves;
    }

    public void applyMove(Move move) {
        Piece piece = grid[move.fromRow][move.fromCol];
        grid[move.fromRow][move.fromCol] = null;
        grid[move.toRow][move.toCol] = piece;

        for (Position cap : move.capturedPositions) {
            grid[cap.row][cap.col] = null;
        }

        if (piece instanceof NormalPiece) {
            if ((piece.getColor().equals("white") && move.toRow == 0) ||
                    (piece.getColor().equals("black") && move.toRow == 7)) {
                grid[move.toRow][move.toCol] = new KingPiece(piece.getColor(), move.toRow, move.toCol);
            }
        }
    }


    public boolean movePiece(Piece piece, int toRow, int toCol) throws InvalidMoveException, OccupiedSquareException {
        int fromRow = piece.getRow();
        int fromCol = piece.getCol();

        if (grid[toRow][toCol] != null) {
            throw new OccupiedSquareException("O espaço está ocupado!");
        }

        String playerColor = piece.getColor();

        // Get all mandatory capture moves for this player
        List<Move> captureMoves = getMaxCaptureMovesForPlayer(playerColor);

        // Check if this move matches any mandatory capture move
        Move matchedCaptureMove = null;
        for (Move m : captureMoves) {
            if (m.fromRow == fromRow && m.fromCol == fromCol && m.toRow == toRow && m.toCol == toCol) {
                matchedCaptureMove = m;
                break;
            }
        }

        boolean capturou = false;

        if (matchedCaptureMove != null) {
            // Esta é uma captura válida (pode ser múltipla)

            // Remove todas as peças capturadas no caminho
            for (Position capPos : matchedCaptureMove.capturedPositions) {
                grid[capPos.row][capPos.col] = null;
            }
            capturou = true;

        } else {
            // Nenhuma captura correspondente

            // Se há capturas obrigatórias, não aceita movimento simples
            if (!captureMoves.isEmpty()) {
                int maxCaptures = captureMoves.get(0).capturedPositions.size();
                boolean hasMultiCapture = maxCaptures > 1;

                if (hasMultiCapture) {
                    throw new InvalidMoveException("Captura múltipla disponível!");
                } else {
                    throw new InvalidMoveException("Você deve efetuar uma captura.");
                }
            }

            // Verifica validade do movimento simples
            if (!piece.isValidMove(this, fromRow, fromCol, toRow, toCol)) {
                throw new InvalidMoveException("Movimento inválido.");
            }
        }

        // Move a peça
        grid[toRow][toCol] = piece;
        grid[fromRow][fromCol] = null;
        piece.move(toRow, toCol);

        // Promoção
        if (piece instanceof NormalPiece) {
            if ((playerColor.equals("white") && toRow == 0) || (playerColor.equals("black") && toRow == 7)) {
                grid[toRow][toCol] = new KingPiece(playerColor, toRow, toCol);
            }
        }

        return capturou;
    }


    // Return a string representation of the board
    public String toBoardString() {
        StringBuilder sb = new StringBuilder();

        // Checks square by square and writes the corresponding characters
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = getPiece(row, col);
                if (p == null) sb.append(".");
                else if (p instanceof KingPiece) sb.append(p.getColor().equals("white") ? "W" : "B");
                else sb.append(p.getColor().equals("white") ? "w" : "b");
            }
        }
        return sb.toString();
    }


    // check if a player has any possible moves left
    public boolean hasAnyMoves(String playerColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = getPiece(row, col);
                if (p != null && p.getColor().equals(playerColor)) {
                    if (canPieceMove(p)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Checks if a piece can move
    private boolean canPieceMove(Piece piece) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int fromRow = piece.getRow();
        int fromCol = piece.getCol();

        for (int[] dir : directions) {
            int toRow = fromRow + dir[0];
            int toCol = fromCol + dir[1];
            if (isValidPosition(toRow, toCol) && grid[toRow][toCol] == null) {
                if (piece.isValidMove(this, fromRow, fromCol, toRow, toCol)) {
                    return true;
                }
            }
            // Check for capture move
            int jumpRow = fromRow + 2 * dir[0];
            int jumpCol = fromCol + 2 * dir[1];
            if (isValidPosition(jumpRow, jumpCol) && grid[jumpRow][jumpCol] == null) {
                int midRow = fromRow + dir[0];
                int midCol = fromCol + dir[1];
                Piece middlePiece = grid[midRow][midCol];
                if (middlePiece != null && !middlePiece.getColor().equals(piece.getColor())) {
                    if (piece.isValidMove(this, fromRow, fromCol, jumpRow, jumpCol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }


    public Piece[][] getGrid() { return grid; }
}
