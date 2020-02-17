package i18n;

import java.util.ListResourceBundle;

public class MessageListBundle_en_UK extends ListResourceBundle {

    protected Object[][] getContents() { return contents; }

    private Object[][] contents = {
            {"welcome", "Welcome. Select a game from the list at the bottom left corner."},
            {"wordoku", "Wordoku"},
            {"help", "Help"},
            {"sudoku", "Sudoku"},
            {"killerSudoku", "Killer Sudoku"},
            {"duidoku", "Duidoku"},
            {"tools", "Tools"},
            {"gameSelection", "Select a game"},
            {"clickToUndo", "Click to undo move"},
            {"undo", "Undo"},
            {"clickToExit", "Click to exit current game"},
            {"exit", "Exit Game"},
            {"language", "Ελληνικά"},
            {"exitWindow","Exit Game?"},
            {"exitMessage", "Are you sure you want to exit current game?"},
            {"exitClose", "Do you want to close the program?"},
            {"exitProgramm", "Close program?"},
            {"illegal", "Illegal move!"},
            {"username", "Enter your username here"},
            {"label", "Click to select. Enter a number. Click to deselect."},
            {"regionSum", "Current region sum: "},
            {"congrats", "Congratulations you win!"},
            {"lose", "Sorry you lost."}
    };
}
