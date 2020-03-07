package player;

import java.io.Serializable;
import java.util.HashMap;

public class PlayerProgress implements Serializable {
    HashMap<Integer,int[][]> data;

    public PlayerProgress() {
        data = new HashMap<>();
    }

    public void updateData(int id, int[][] puzzle) {
        data.put(id,puzzle);
    }

    public HashMap<Integer,int[][]> getData() { return data; }
}
