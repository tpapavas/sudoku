package player;

import auxiliary.*;

import java.io.*;
import java.util.ArrayList;

public class Player implements Serializable {
    private String nickname;
    private long password; //TODO: CHALLENGE.
    private boolean[] hasPlayedSudoku;
    private boolean[] hasPlayedKillerSudoku;
    private int duidokuWins;
    private int duidokuLoses;
    private String filepath;
    private PlayerProgress progress;

    private final int numOfSudokuPuzzles = 10;
    private final int numOfKillerSudokuPuzzles = 10;

    private boolean existInFile;

    public Player(String name, String filepath) {
        this.nickname = name;
        this.hasPlayedSudoku = new boolean[numOfSudokuPuzzles];
        this.hasPlayedKillerSudoku = new boolean[numOfKillerSudokuPuzzles];
        this.filepath = filepath;
        this.duidokuWins = 0;
        this.duidokuLoses = 0;
        this.progress = new PlayerProgress();
        readFromFile();
    }

    public ArrayList<ArrayList<Integer>> puzzlePlayed() {
        ArrayList<ArrayList<Integer>> indexes = new ArrayList<>();
        ArrayList<Integer> classics = new ArrayList<>();
        ArrayList<Integer> killers = new ArrayList<>();

        for(int i = 0; i < numOfSudokuPuzzles; i++)
            if(hasPlayedSudoku[i]) classics.add(i+1);
        for(int i = 0; i < numOfKillerSudokuPuzzles; i++)
            if(hasPlayedKillerSudoku[i]) killers.add(i+1);

        indexes.add(classics);
        indexes.add(killers);

        return indexes;
    }

    private void readFromFile() {
        existInFile = false;
        Player p;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath));
            while(true) {
                p = (Player) in.readObject();
                if (p.getNickname().equals(nickname)) {
                    System.out.println("FOUND");
                    existInFile = true;
                    copy(p);
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Error within player's readFromFile()");
        }
    }

    private void copy(Player p) {
        this.hasPlayedSudoku = p.hasPlayedSudoku;
        this.hasPlayedKillerSudoku = p.hasPlayedKillerSudoku;
        this.duidokuWins = p.duidokuWins;
        this.duidokuLoses = p.duidokuLoses;
        this.progress = p.progress;
    }

    public String getNickname() { return nickname; }
    public boolean[] getHasPlayedSudoku() { return hasPlayedSudoku; }
    public boolean[] getHasPlayedKillerSudoku() { return  hasPlayedKillerSudoku; }
    public int getDuidokuWins() { return duidokuWins; }
    public int getDuidokuLoses() { return duidokuLoses; }
    public String getFilepath() { return filepath; }
    public boolean isExistsInFile() { return existInFile; }
    public int getNumOfSudokuPuzzles() { return numOfSudokuPuzzles; }
    public int getNumOfKillerSudokuPuzzles() { return numOfKillerSudokuPuzzles; }
    public PlayerProgress getProgress() { return progress; }

    public void increaseDuidokuWins() { duidokuWins++; }
    public void increaseDuidokuLoses() { duidokuLoses++; }
}
