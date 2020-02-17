package sudokuBundle;

import player.*;
import puzzle.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Sudoku {
    protected Player player;
    protected Puzzle puzzle;
    protected Representation representation;
    protected int dimension;
    protected DisplayType type;
    protected int puzzleIndex;

    protected String filepath;


    public Sudoku(int dimension, Player player) {
        this.player = player;
        this.puzzle = new Puzzle(dimension);
        this.representation = new Representation(dimension,DisplayType.NUMBERS);
        this.type = DisplayType.NUMBERS;
        this.dimension = dimension;
        this.puzzleIndex = 1;
    }

    /**
     * Checks whether the move that player attempts to do is valid
     * according to the sudoku rules.
     * @param x the value player wants to place.
     * @param i the row index in table.
     * @param j the column index in table.
     * @return true if the move is valid, otherwise false.
     */
    public boolean isLegalMove(int x, int i, int j) {
        int dim = puzzle.getDimention();
        //Check row
        for(int indJ = 0; indJ < dim; indJ++)
            if(indJ != j)
                if(x == puzzle.getTable()[i][indJ])
                    return false;

        //Check column
        for(int indI = 0; indI < dim; indI++)
            if(indI != i)
                if(x == puzzle.getTable()[indI][j])
                    return false;

        //Check square
        int divisor = (int) (Math.sqrt(dim));
        int iStartPoint = (i / divisor) * divisor;
        int jStartPoint = (j / divisor) * divisor;

        for(int indI = iStartPoint; indI < (iStartPoint+divisor); indI++)
            for(int indJ = jStartPoint; indJ < (jStartPoint+divisor); indJ++)
                if(!(indI == i && indJ == j))
                    if(x == puzzle.getTable()[indI][indJ])
                        return false;
        return true;
    }

    /**
     * If player's move is valid, method places it inside the table
     * in (i,j) position.
     * @param x the value player wants to place.
     * @param i the row index in table.
     * @param j the column index in table.
     * @return true if the move was placed, otherwise false.
     */
    public boolean doMove(int x, int i, int j) {
        if(!(isInsideLimits(i) && isInsideLimits(j) && isInsideLimits((x-1))))
            return false;
        if(puzzle.getState()[i][j] == State.NEGATED)
            return false;
        if (isLegalMove(x, i, j)) {
            puzzle.setValue(x, i, j);
            puzzle.increaseFilledCells();
            return true;
        }
        return false;
    }

    /**
     * Removes the value of (i,j) table's position if it can be removed.
     * @param i the row index in table.
     * @param j the column index in table.
     * @return true if the value got removed, otherwise false.
     */
    public boolean undoMove(int i, int j) {
        if(!(isInsideLimits(i) && isInsideLimits(j)))
            return false;
        if(puzzle.getState()[i][j] == State.NEGATED)
            return false;
        puzzle.setValue(0,i,j);
        puzzle.decreaseFilledCells();
        return true;
    }

    /**
     * Shows every possible move in every empty cell.
     * @return a 3d ArrayList with all the possible moves.
     * e.g. the available moves of (3,4) cell are in
     * allPossibleMoves.get(3).get(4);
     */
    public ArrayList<ArrayList<ArrayList<Integer>>> help() {
        ArrayList<ArrayList<ArrayList<Integer>>> allPossibleMoves = new ArrayList<>();
        for(int i = 0; i < dimension; i++) {
            ArrayList<ArrayList<Integer>> linePossibleMoves = new ArrayList<>();
            for (int j = 0; j < dimension; j++) {
                ArrayList<Integer> cellPossibleMoves = new ArrayList<>();
                if (puzzle.getTable()[i][j] == 0) {
                    StringBuilder movesText = new StringBuilder();
                    for (int x = 1; x < (dimension + 1); x++) {
                        if (isLegalMove(x, i, j)) {
                            movesText.append(representation.getFormat()[x]).append(" ");
                            cellPossibleMoves.add(x);
                        }
                    }
                    System.out.println("(" + (i + 1) + "," + (j + 1) + ") : " + movesText);
                }
                linePossibleMoves.add(cellPossibleMoves);
            }
            allPossibleMoves.add(linePossibleMoves);
        }
        return allPossibleMoves;
    }

    /**
     * Changes the representation type (DisplayType)
     * @param type the new Display Type.
     */
    public void changeRepresentation(DisplayType type) {
        representation.changeType(type);
        this.type = type;
    }

    /**
     * Updates players' file adding this.player or updating his/her stats.
     * @throws IOException
     */
    public void updatePlayersFile() throws IOException {
        HashMap<String,Player> players = new HashMap<>();
        readPlayersFile(players);
        writePlayersFile(players);
    }

    /**
     * Reads all players from players' file and places them in a HashMap
     * with their name as a key.
     * @param players a HashMap where players from playersFile (plus this.player) will be stored. The key is their name.
     */
    private void readPlayersFile(HashMap<String,Player> players) {
        ObjectInputStream in = null;
        Player p;
        try{
            //Loads all players from file.
            in = new ObjectInputStream(new FileInputStream(player.getFilepath()));
            while(true) {
                p = (Player) in.readObject();
                players.put(p.getNickname(),p);
            }
        } catch (EOFException e) {
            players.put(this.player.getNickname(),this.player);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("IO/ClassNotFound in updatePlayersFile()");
        } finally {
            if(in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("Error closing players.dat");
                }
        }
    }

    /**
     * Writes all players (including this.player) to players' file.
     * @param players a HashMap that keeps stored players (plus this.player) with their name as a key.
     */
    private void writePlayersFile(HashMap<String,Player> players) {
        ObjectOutputStream out = null;
        try {
            //Write all players to file plus this.player.
            out = new ObjectOutputStream(new FileOutputStream(player.getFilepath()));
            for(Player player : players.values())
               out.writeObject(player);
        } catch (IOException ioe) {
            System.out.println("Error in updatePlayersFile()");
        } finally {
            if(out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    System.out.println("Error closing players.dat");
                }
        }
        System.out.println("EOF in updatePlayersFile()");
    }

    /**
     * Checks whether the parameter's value is inside some limits.
     * @param x the variable we want to check.
     * @return true if x is in corresponding interval, otherwise false.
     */
    private boolean isInsideLimits(int x) {
        return x >= 0 && x < puzzle.getDimention();
    }

    protected int findPuzzle() { return -1; };
    public Puzzle getPuzzle() { return puzzle; }
    public DisplayType getType() { return type; }
    public Representation getRepresentation() { return representation; }
    public String getFilepath() { return filepath; }
    public int getPuzzleIndex() { return puzzleIndex; }
}
