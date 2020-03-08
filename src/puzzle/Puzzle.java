package puzzle;

import java.io.Serializable;

public class Puzzle implements Serializable {
    private State[][] state;
    private int[][] table;
    private int filledCells;
    private final int dimension;
    private final int length;
    private final int numOfCells;

    public Puzzle(int length) {
        this.dimension = (int) Math.sqrt(length);
        this.length = length;
        this.numOfCells = (int) Math.pow(length,2);
        this.table = new int[length][length];
        this.state = new State[length][length];
        this.filledCells = 0;

        for(int i = 0; i< length; i++){
            for(int j = 0; j< length; j++){
                this.table[i][j] = 0;
                this.state[i][j] = State.ACCESSIBLE;
            }
        }
    }

    /**
     * Sets content of (i,j) table's position to value.
     * @param value is the new content of table[i][j] (if this is valid move).
     * @param i indicates table's row.
     * @param j indicates table's column.
     * @return true if the assignment was valid, false otherwise.
     */
    public boolean setValue(int value, int i, int j) {
        table[i][j] = value;
        return true;
    }

    public boolean isFull() { return filledCells == numOfCells; }
    public void increaseFilledCells() { filledCells++; }
    public void decreaseFilledCells() { filledCells--; }

    public int[][] getTable() { return table; }
    public State[][] getState() { return state; }
    public int getDimension() { return dimension; }
    public int getLength() { return length; }
    public int getNumOfCells() { return numOfCells; }

    public void setState(State s, int i, int j) {state[i][j] = s;}
}
