package model;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private String color; // "white" or "black"

    public Player(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
}
