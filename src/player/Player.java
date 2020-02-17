package player;

import auxiliary.*;

import java.io.*;

public class Player implements Serializable {
    private String nickname;
    private long password; //TODO: CHALLENGE.
    private boolean[] hasPlayedSudoku;
    private boolean[] hasPlayedKillerSudoku;
    private int duidokuWins;
    private int duidokuLoses;
    private String filepath;

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
        readFromFile();
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

    public void increaseDuidokuWins() { duidokuWins++; }
    public void increaseDuidokuLoses() { duidokuLoses++; }
}
