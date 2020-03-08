package player;

import auxiliary.Duplet;
import puzzle.Puzzle;

import java.io.Serializable;
import java.util.HashMap;

public class PlayerProgress implements Serializable {
    HashMap<Duplet, Puzzle> data;

    public PlayerProgress() {
        data = new HashMap<>();
    }

    public void updateData(Duplet duplet, Puzzle puzzle) { data.put(duplet,puzzle); }

    public HashMap<Duplet,Puzzle> getData() { return data; }
}
