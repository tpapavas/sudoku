package sudokuBundle;

import auxiliary.Duplet;
import auxiliary.GameType;
import player.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class KillerSudoku extends Sudoku {
    ArrayList<Integer> regionSums;
    int[][] regionMap;
    boolean[][] gotCrossed;
    int sum;
    boolean regionIsCompleted;

    public KillerSudoku(Player player) {
        super(9,player);
        regionSums = new ArrayList<>();
        regionMap = new int[length][length];
        gotCrossed = new boolean[length][length];
        sum = 0;
        filepath = (new File("").getAbsolutePath()) + "\\src\\resources\\killerPuzzles\\puzzle";
        readFromFile();
    }

    private void readFromFile() {
        puzzleIndex = findPuzzle();
        filepath += puzzleIndex + ".txt";
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filepath));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                Scanner tokenizer = new Scanner(line);
                if (tokenizer.hasNextInt()) {
                    regionSums.add(tokenizer.nextInt());
                    while (tokenizer.hasNextInt()) {
                        int indexes = tokenizer.nextInt();
                        int row = indexes / 10;
                        int column = indexes % 10;
                        regionMap[row][column] = i;
                    }
                    i++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error within KillerSudoku's readFromFile()");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing file in KillerSudoku's readFromFile()");
                }
            }
        }

        Duplet duplet = new Duplet(puzzleIndex, GameType.KILLER_SUDOKU);
        if(player.getProgress().getData().containsKey(duplet)) {
            System.out.println("YEP");
            setPuzzle(player.getProgress().getData().get(duplet));
        }
    }

    public boolean isLegalMove(int x, int i, int j) {
        if(!super.isLegalMove(x,i,j))
            return false;

        sum = 0;
        regionIsCompleted = true;
        for(int iInd = 0; iInd < length; iInd++)
            for(int jInd = 0; jInd < length; jInd++)
                gotCrossed[iInd][jInd] = false;
        findNext(i,j,regionMap[i][j],true);
        if(regionIsCompleted)
            return (sum + x) == regionSums.get(regionMap[i][j]);
        else
            return (sum + x) < regionSums.get(regionMap[i][j]);
    }

    private void findNext(int i, int j, int regionIndex, boolean firstCall) {
        gotCrossed[i][j] = true;
        sum += puzzle.getTable()[i][j];
        if(puzzle.getTable()[i][j] == 0 && !firstCall)
            regionIsCompleted = false;
        //go left
        if((j-1) >= 0)
            if(regionMap[i][j-1] == regionIndex && !gotCrossed[i][j-1])
                findNext(i, (j-1),regionIndex,false);
        //go up
        if((i-1) >= 0)
            if(regionMap[i-1][j] == regionIndex && !gotCrossed[i-1][j])
                findNext((i-1), j,regionIndex,false);
        //go right
        if((j+1) <= 8)
            if(regionMap[i][j+1] == regionIndex && !gotCrossed[i][j+1])
                findNext(i,(j+1),regionIndex,false);
        //go down
        if((i+1) <= 8)
            if(regionMap[i+1][j] == regionIndex && !gotCrossed[i+1][j])
                findNext((i+1), j,regionIndex,false);
    }

    protected int findPuzzle() {
        for(int i = 0; i < player.getNumOfKillerSudokuPuzzles(); i++) {
            if(!player.getHasPlayedKillerSudoku()[i])
                return i+1;
        }
        System.out.println("Something not working well");
        Random r = new Random();
        return (r.nextInt(player.getNumOfKillerSudokuPuzzles()) + 1);
    }

    public int getRegionSum(int i, int j){return this.regionSums.get(this.regionMap[i][j]);}
    public int getNumberOfRegions() { return regionSums.size(); }
    public int getCellRegion(int i, int j){return this.regionMap[i][j];}
}
