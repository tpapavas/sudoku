package puzzle;

public class Puzzle {
    private State[][] state;
    private int[][] table;
    private int filledCells;
    private final int dimension;
    private final int numOfCells;

    public Puzzle(int dim) {
        this.dimension = dim;
        this.numOfCells = dim*dim;
        this.table = new int[dimension][dimension];
        this.state = new State[dimension][dimension];
        this.filledCells = 0;

        for(int i = 0; i< dimension; i++){
            for(int j = 0; j< dimension; j++){
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

    public boolean isFull(){ return filledCells == numOfCells; }
    public void increaseFilledCells(){ filledCells++; }
    public void decreaseFilledCells(){ filledCells--; }
    public int[][] getTable(){ return table; }
    public State[][] getState(){ return state; }
    public int getDimension(){ return dimension; }
    public void setState(State s, int i, int j) {state[i][j] = s;}
}
