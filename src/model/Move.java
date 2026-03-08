package model;

import java.util.ArrayList;
import java.util.List;

public class Move {
    public int fromRow, fromCol;
    public int toRow, toCol;
    public List<Position> capturedPositions;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.capturedPositions = new ArrayList<>();
    }
}
