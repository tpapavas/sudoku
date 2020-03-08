import auxiliary.*;
import gui.GUI;
import sudokuBundle.*;
import puzzle.*;
import player.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author Stergios Stamatiou
 * @author Theodoros Papavasileiou
 */
public class Game {
    public static final double version = 2.2;

    public static void main(String[] args) throws Throwable {
        //setDataFile();

        GUI gui = new GUI();

        /*
        String file = new File("").getAbsolutePath() +
                "\\src\\resources\\players.dat";
        Player player = new Player("Panos",file);
        ClassicSudoku sudoku = new ClassicSudoku(9,player); */
    }

    public void play() {
        String path = getClass().getResourceAsStream("/resources").toString();
        System.out.println(path);
    }

    private static void setDataFile() throws Exception {
        String file = new File("").getAbsolutePath() +
                "\\src\\resources\\players.dat";
        Player p1 = new Player("Panos",file);
        p1.getHasPlayedSudoku()[0] = true;
        p1.increaseDuidokuWins();
        p1.increaseDuidokuWins();
        Player p2 = new Player("Christina",file);
        Player p3 = new Player("Eleni",file);

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(p1);
        out.writeObject(p2);
        out.writeObject(p3);
        out.close();
    }

    private static void loadPLayer() throws Throwable {
        String file = "players.dat";
        Player p = new Player("Panos",file);

        System.out.println(p.getHasPlayedSudoku()[0]);
    }
}
