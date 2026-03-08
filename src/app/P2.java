package app;

import model.Board;
import io.Serializer;
import ui.GameGUI;

public class P2 {
    public static void main(String[] args) {
        String binaryFile = "board.dat";

        // Load board data from the .dat
        try {
            Board board = (Board) Serializer.load(binaryFile);
            System.out.println("Board loaded from " + binaryFile);

            // Open the game GUI
            new GameGUI(board);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
