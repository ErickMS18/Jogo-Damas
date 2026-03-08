package model;

public class NormalPiece extends Piece {

    public NormalPiece(String color, int row, int col) {
        super(color, row, col);
    }

    @Override
    public boolean isValidMove(Board board, int fromRow, int fromCol, int toRow, int toCol) {
        // returns true if the move is valid, false if it isn't

        // Direction is -1 (up) if white, 1 (down) if black
        int dir = getColor().equals("white") ? -1 : 1;
        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        // Regular move - forward by 1 diagonally
        if (rowDiff == dir && colDiff == 1) {
            // Ensure destination is empty
            return board.getPiece(toRow, toCol) == null;
        }

        // Capture move - must be 2 diagonal steps and an opponent piece between
        if (Math.abs(rowDiff) == 2 && colDiff == 2) {
            int midRow = fromRow + rowDiff / 2;
            int midCol = fromCol + (toCol - fromCol) / 2;
            Piece midPiece = board.getPiece(midRow, midCol);

            // Check opponent piece in the middle and landing square empty
            if (midPiece != null && !midPiece.getColor().equals(this.getColor()) &&
                    board.getPiece(toRow, toCol) == null) {
                return true;
            }
        }

        return false;
    }


}
