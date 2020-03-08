package auxiliary;

import java.io.Serializable;

public enum GameType implements Serializable {
    CLASSIC_SUDOKU(0),
    KILLER_SUDOKU(1),
    DUIDOKU(2);

    private int code;

    GameType(int code){ this.code = code; }

    public int getCode(){ return code; }
}
