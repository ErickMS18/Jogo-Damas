package model;

public class KingPiece extends Piece {

    public KingPiece(String color, int row, int col) {
        super(color, row, col);
    }

    @Override
    public boolean isValidMove(Board board, int fromRow, int fromCol, int toRow, int toCol) {
        // returns true if the move is valid, false if it isn't

        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // Ensure straight diagonal moves
        if (Math.abs(rowDiff) != Math.abs(colDiff)) return false;

        // Movement direction for row and column
        int rowStep = rowDiff > 0 ? 1 : -1;
        int colStep = colDiff > 0 ? 1 : -1;

        // Check each space between the piece and the destination
        // Start with the space directly ahead
        int r = fromRow + rowStep;
        int c = fromCol + colStep;
        int opponentCount = 0;

        while (r != toRow && c != toCol) {
            // check if there's a piece
            Piece p = board.getPiece(r, c);
            if (p != null) {
                if (p.getColor().equals(this.getColor())) {
                    return false; // blocked by own piece
                }
                opponentCount++;
                if (opponentCount > 1) {
                    return false; // cannot jump multiple opponents in one move
                }
            }
            r += rowStep;
            c += colStep;
        }

        // Landing square must be empty
        if (board.getPiece(toRow, toCol) != null) return false;

        // Valid if either no opponent pieces (simple move) or exactly one (capture)
        return true;
    }


}
