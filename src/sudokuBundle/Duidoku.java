package sudokuBundle;

import player.Player;
import puzzle.State;

public class Duidoku extends Sudoku {

    public Duidoku(Player player) {
        super(4,player);
    }

    public boolean doMove(int x, int i, int j) {
        if(!super.doMove(x,i,j))
            return false;
        checkNullCells();
        puzzle.setState(State.NEGATED,i,j);
        return true;
    }

    public boolean pcMove() {
        checkNullCells();
        for(int i = 0; i < length; i++)
            for(int j = 0; j < length; j++)
                if(puzzle.getTable()[i][j] == 0)
                    for(int x = 1; x <= length; x++)
                        if(super.doMove(x,i,j)) {
                            puzzle.setState(State.NEGATED,i,j);
                            checkNullCells();
                            return true;
                        }
        return false;
    }

    private void checkNullCells() {
        for(int i = 0; i < length; i++)
            for(int j = 0; j < length; j++)
                if(puzzle.getTable()[i][j] == 0) {
                    int x;
                    for(x = 1; x <= length; x++)
                        if(isLegalMove(x,i,j))
                            break;
                    if(x == (length+1)) {
                        puzzle.setValue(x,i,j);
                        puzzle.setState(State.NEGATED,i,j);
                        puzzle.increaseFilledCells();
                    }
                }
    }
}
