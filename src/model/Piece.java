package model;

import java.io.Serializable;

public abstract class Piece implements Serializable {
    protected String color; // "white" or "black"
    protected int row;
    protected int col;

    public Piece(String color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public abstract boolean isValidMove(Board board, int fromRow, int fromCol, int toRow, int toCol);

    public void move(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    public String getColor() { return color; }
    public int getRow() { return row; }
    public int getCol() { return col; }
}
