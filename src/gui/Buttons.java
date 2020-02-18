package gui;

public enum Buttons {
    ENTER(0),
    WORDOKU(1),
    HELP(2),
    LANGUAGE(3),
    SUDOKU(4),
    KILLER_SUDOKU(5),
    DUIDOKU(6),
    UNDO(7),
    EXIT_GAME(8);

    int code;

    Buttons(int code) { this.code = code;}
}
