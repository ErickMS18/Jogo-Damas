package io;

import java.io.*;

public class CsvLogger {
    private BufferedWriter writer;

    public CsvLogger(String filename) throws IOException {
        writer = new BufferedWriter(new FileWriter(filename));
    }

    public void log(String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush(); // Makes sure the file is always up to date, even if the app crashes
    }

    public void close() throws IOException {
        writer.close();
    }
}
