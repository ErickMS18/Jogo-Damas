package app;

import io.Serializer;
import model.Board;
import model.KingPiece;
import model.NormalPiece;
import model.Piece;

import java.io.*;

public class P1 {
    public static void main(String[] args) {

        // Declare file paths
        String csvFile = "data/pieces.csv";
        String binaryFile = "board.dat";

        // Create board
        Board board = new Board();

        // Read file:
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip the header

            // Read each line:
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                String color = parts[2];
                String type = parts[3];

                // Create a piece
                Piece piece;
                if (type.equalsIgnoreCase("king")) {
                    piece = new KingPiece(color, row, col);
                } else {
                    piece = new NormalPiece(color, row, col);
                }

                // Place the piece on the board
                board.placePiece(piece);
            }

            // Save the board to a .dat file
            Serializer.save(board, binaryFile);
            System.out.println("Board saved to " + binaryFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
