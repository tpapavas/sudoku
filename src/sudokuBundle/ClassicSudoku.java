package sudokuBundle;

import player.Player;
import puzzle.State;

import java.io.*;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class ClassicSudoku extends Sudoku {
    //String folderpath;

    public ClassicSudoku(Player player) {
        super(9,player);
        filepath = (new File("").getAbsolutePath()) + "\\src\\resources\\classicPuzzles\\puzzle";
        readFromFile();

        //String path = "/resources";
        //InputStream in = getClass().getResourceAsStream(path);

        //folderpath = Paths.get("\\files\\classicPuzzles").toString();
    }

    /**
     * Opens the puzzleN.txt file (N is a number) and fills the puzzle's table with initial cell values.
     * It creates a sudoku table that's ready to be solved.
     */
    private void readFromFile() {
        int index = findPuzzle();
        filepath += index + ".txt";
        System.out.println(filepath);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filepath));
            String line;
            while((line = reader.readLine()) != null) {
                Scanner tokenizer = new Scanner(line);
                if(tokenizer.hasNextInt()) {
                    int indexes = tokenizer.nextInt();
                    if(tokenizer.hasNextInt()) {
                        int value = tokenizer.nextInt();
                        int row = indexes / 10;  //First digit is row index
                        int column = indexes % 10;  //Second digit is column index

                        puzzle.setValue(value,row,column);
                        puzzle.setState(State.NEGATED,row,column);  //Player cannot modify this cell
                        puzzle.increaseFilledCells();
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Error within ClassicSudoku's readFromFile()");
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing file in ClassicSudoku's readFromFile()");
                }
            }
        }
    }

    /**
     * Searches for a puzzle that player hasn't played before.
     * @return the index (1,2,...) of the first puzzle (in the row) that player hasn't played.
     */
    protected int findPuzzle() {
        for(int i = 0; i < player.getNumOfSudokuPuzzles(); i++) {
            if(!player.getHasPlayedSudoku()[i])
                return i + 1;
        }

        Random r = new Random();
        return (r.nextInt(player.getNumOfSudokuPuzzles()) + 1);
    }
}
