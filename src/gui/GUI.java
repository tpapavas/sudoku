package gui;

import player.Player;
import puzzle.DisplayType;
import puzzle.State;
import sudokuBundle.ClassicSudoku;
import sudokuBundle.Duidoku;
import sudokuBundle.KillerSudoku;
import sudokuBundle.Sudoku;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class GUI {
    //Colors needed
    static final Color COLOR_MEDIUM_GREY = new Color(127,127,127);
    static final Color COLOR_DEFAULT = new Color(205,205,205);
    static final Color COLOR_HIGHLIGHTED = new Color(241,239,0);
    static final Color COLOR_WRONG = new Color(255,0,0,179);

    private int currentOriginI, currentOriginJ;
    private int lastMoveLinear;

    private String finishMessage;

    private boolean aButtonIsCurrentlyActive;
    private boolean aGameIsCurrentlyActive;
    private int currentSelection;
    private boolean displayHints;

    //GUI elements
    private JFrame frame;
    private JPanel panelMain, panelButtons, panelGameContainer, panelUserInfo, currentSquare;
    private JPanel panelGameClassic, panelGameKiller, panelGameDuidoku;
    private JLabel labelInstructions, labelUserInfo, lbl1, lbl2, lbl3, lbl4;
    private JButton buttonEnter, buttonWordoku, buttonHelp, buttonSudoku, buttonKillerSudoku, buttonDuidoku, buttonUndo, buttonExitGame, buttonLang;
    private cellButton[] cells;
    private JPanel[] squares;
    private JTextField fieldUsername;

    private Sudoku currentGame;

    private Locale locale;
    private ResourceBundle messages;

    private Player player;

    public GUI() {
        lastMoveLinear = -1;
        finishMessage = "";
        aButtonIsCurrentlyActive = false;
        aGameIsCurrentlyActive = false;
        currentSelection = -1;
        displayHints = false;

        locale = new Locale("en","UK");
        messages = ResourceBundle.getBundle("i18n.MessageListBundle",locale);

        frame = new JFrame("Sudoku Game Suite");

        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());

        panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(13,1));
        panelButtons.setBorder(new EmptyBorder(12,7,12,7));

        panelGameContainer = new JPanel(new GridLayout(1,1));
        panelGameContainer.setBorder(new EmptyBorder(10,10,10,10));
        panelGameContainer.setBackground(COLOR_MEDIUM_GREY);

        labelInstructions = new JLabel(messages.getString("welcome"));
        labelInstructions.setBorder(new EmptyBorder(5,5,5,5));

        panelMain.add(labelInstructions,BorderLayout.PAGE_START);
        panelMain.add(panelButtons,BorderLayout.LINE_START);
        panelMain.add(panelGameContainer,BorderLayout.CENTER);

        buttonHelp = new JButton(messages.getString("help"));
        buttonWordoku = new JButton(messages.getString("wordoku"));
        buttonSudoku = new JButton(messages.getString("sudoku"));
        buttonKillerSudoku = new JButton(messages.getString("killerSudoku"));
        buttonDuidoku = new JButton(messages.getString("duidoku"));
        buttonUndo = new JButton(messages.getString("undo"));
        buttonExitGame = new JButton(messages.getString("exit"));
        buttonLang = new JButton(messages.getString("language"));

        lbl1 = new JLabel(messages.getString("tools"));
        lbl1.setHorizontalAlignment(SwingConstants.CENTER);

        lbl2 = new JLabel(messages.getString("gameSelection"));
        lbl2.setHorizontalAlignment(SwingConstants.CENTER);

        lbl3 = new JLabel(messages.getString("clickToUndo"));
        lbl3.setHorizontalAlignment(SwingConstants.CENTER);

        lbl4 = new JLabel(messages.getString("clickToExit"));
        lbl4.setHorizontalAlignment(SwingConstants.CENTER);

        panelButtons.add(lbl1);
        panelButtons.add(buttonWordoku);
        panelButtons.add(buttonHelp);
        panelButtons.add(buttonLang);
        panelButtons.add(lbl2);
        panelButtons.add(buttonSudoku);
        panelButtons.add(buttonKillerSudoku);
        panelButtons.add(buttonDuidoku);
        panelButtons.add(lbl3);
        panelButtons.add(buttonUndo);
        panelButtons.add(lbl4);
        panelButtons.add(buttonExitGame);


        panelUserInfo = new JPanel(new BorderLayout());

        fieldUsername = new JTextField("",15);
        labelUserInfo = new JLabel(messages.getString("username"));

        buttonEnter = new JButton("Enter");

        panelUserInfo.add(buttonEnter,BorderLayout.LINE_END);
        panelUserInfo.add(labelUserInfo,BorderLayout.NORTH);
        panelUserInfo.add(fieldUsername,BorderLayout.CENTER);

        panelUserInfo.setBorder(new EmptyBorder(120,10,260,10));
        panelUserInfo.setBackground(COLOR_DEFAULT);

        panelGameContainer.add(panelUserInfo);

        toggleChildButtons(panelButtons,false);

        setupActionListeners();

        frame.add(panelMain);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(650,500);
        frame.setResizable(false);
        frame.setVisible(true);

        fieldUsername.requestFocusInWindow();
    }

    private class cellButton {
        private JButton button;
        private boolean isClicked;
        private boolean enteredIllegalNumber;

        private int coordI;
        private int coordJ;
        private int coordLinear;

        private Color defaultColor;

        public cellButton() {
            button = new JButton();
            isClicked = false;
            enteredIllegalNumber = true;
            coordI = -1;
            coordJ = -1;
            coordLinear = -1;
            defaultColor = COLOR_DEFAULT;

            button.addActionListener((ActionEvent e) -> {
                if(isClicked && aButtonIsCurrentlyActive)
                    release();
                else if(currentGame.getPuzzle().getState()[coordI][coordJ] == State.NEGATED || aButtonIsCurrentlyActive) {
                }
                else if(!isClicked)
                    select();
            });

            button.addKeyListener(new KeyListener() {
                @Override public void keyTyped(KeyEvent e) {}
                @Override public void keyReleased(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    int inputUpperLimit, inputLowerLimit;
                    if(currentGame.getType() == DisplayType.NUMBERS) {
                        inputUpperLimit = currentGame instanceof Duidoku ? KeyEvent.VK_4 : KeyEvent.VK_9;
                        inputLowerLimit = KeyEvent.VK_1;
                    } else {
                        inputUpperLimit = currentGame instanceof Duidoku ? KeyEvent.VK_D : KeyEvent.VK_I;
                        inputLowerLimit = KeyEvent.VK_A;
                    }

                    int offset = currentGame.getType() == DisplayType.NUMBERS ? 0 : 16;
                    int keyCode = keyEvent.getKeyCode();

                    if(isClicked) {
                        if(keyCode == KeyEvent.VK_ENTER)
                            release();

                        if(keyCode >= inputLowerLimit && keyCode <= inputUpperLimit) {
                            int displayValue = keyCode - 48 - offset;

                            if(!currentGame.isLegalMove(displayValue,coordI,coordJ)) {
                                labelInstructions.setText(messages.getString("illegal"));
                                enteredIllegalNumber = true;

                                //timer to change to warning bg cell colour
                                Timer timer = new Timer(100, new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent actionEvent) {
                                        button.setBackground(COLOR_WRONG);
                                    }
                                });
                                timer.setRepeats(false);
                                timer.start();

                                currentGame.getPuzzle().setValue(0,coordI,coordJ);
                            } else {
                                currentGame.doMove(displayValue,coordI,coordJ);
                                labelInstructions.setText(messages.getString("label"));
                                enteredIllegalNumber = false;
                                button.setBackground(COLOR_HIGHLIGHTED);
                                lastMoveLinear = coordLinear;
                            }

                            button.setText(String.valueOf(currentGame.getRepresentation().getFormat()[displayValue]));
                        }
                    }
                }
            });

            if(currentGame instanceof KillerSudoku) {
                button.addMouseMotionListener(new MouseMotionListener() {
                    @Override public void mouseDragged(MouseEvent e) {}

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if(!aButtonIsCurrentlyActive)
                            if(!displayHints)
                                labelInstructions.setText(messages.getString("regionSum") + ((KillerSudoku) currentGame).getRegionSum(coordI,coordJ));
                    }
                });
            }
        }

        private void release() {
            if(enteredIllegalNumber)
                button.setText(" ");
            else {
                if(currentGame.getPuzzle().isFull()) {
                    labelInstructions.setText(messages.getString("congrats"));
                    updateStats(true);
                }
                if(currentGame instanceof Duidoku) {
                    if (((Duidoku) currentGame).pcMove()) {
                        if (currentGame.getPuzzle().isFull()) {
                            labelInstructions.setText(messages.getString("lose"));
                            updateStats(false);
                        }
                        refreshTable();
                    }
                }
            }

            isClicked = false;
            aButtonIsCurrentlyActive = false;
            button.setBackground(defaultColor);
            currentSelection = -1;

            if(displayHints) {
                displayHints = false;
                labelInstructions.setText(messages.getString("label"));
                labelInstructions.setForeground(Color.black);
            }
        }

        private void select() {
            currentSelection = coordLinear;
            aButtonIsCurrentlyActive = true;
            isClicked = true;
            button.setBackground(COLOR_HIGHLIGHTED);
        }

        //getters
        private JButton getButton() { return button; }
        private int getCoordI() { return coordI; }
        private int getCoordJ() { return coordJ; }
        private int getCoordLinear() { return coordLinear; }

        //setters
        private void setCoordI(int c) { coordI = c; }
        private void setCoordJ(int c) { coordJ = c; }
        private void setCoordLinear(int c) { coordLinear = c; }
        private void setDefaultColor(Color c) { defaultColor = c; }
    }

    private void setupActionListeners() {
        //---BUTTON SUDOKU
        buttonSudoku.addActionListener((ActionEvent e) -> {
            if(!aGameIsCurrentlyActive) {
                setupClassicSudoku();
                labelInstructions.setText("label");
                aGameIsCurrentlyActive = true;
            }
        });

        //---BUTTON KILLER SUDOKU
        buttonKillerSudoku.addActionListener((ActionEvent e) -> {
            if(!aGameIsCurrentlyActive) {
                setupKillerSudoku();
                labelInstructions.setText("label");
                aGameIsCurrentlyActive = true;
            }
        });

        //---BUTTON DUIDOKU
        buttonDuidoku.addActionListener((ActionEvent e) -> {
            if(!aGameIsCurrentlyActive) {
                setupDuidoku();
                labelInstructions.setText("label");
                aGameIsCurrentlyActive = true;
            }
        });

        //---BUTTON UNDO
        buttonUndo.addActionListener((ActionEvent e) -> {
            int ind = lastMoveLinear;
            currentGame.undoMove(cells[ind].getCoordI(),cells[ind].getCoordJ());
        });

        //---BUTTON HELP
        buttonHelp.addActionListener((ActionEvent e) -> {
            if(currentSelection != -1) {
                displayHints = true;

                ArrayList<Integer> hints;
                hints = currentGame.help().get(cells[currentSelection].getCoordI()).get(cells[currentSelection].getCoordJ());

                StringBuilder text = new StringBuilder("");
                text.append("Hint: ");
                for (int possibleMove : hints)
                    text.append(possibleMove).append(" ");

                labelInstructions.setText(text.toString());
                labelInstructions.setForeground(Color.blue);
            }
        });

        //---BUTTON EXIT CURRENT GAME
        buttonExitGame.addActionListener((ActionEvent e) -> {
            if(aGameIsCurrentlyActive) {
                try {
                    String exitWindow = messages.getString("exitWindow");
                    String exitMessage = messages.getString("exitMessage");
                    int answer = JOptionPane.showConfirmDialog(frame, exitMessage,exitWindow,JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.YES_OPTION) {
                        aGameIsCurrentlyActive = false;

                        //get the sub-gamePanel and hide it and remove it
                        panelGameContainer.getComponent(0).setVisible(false);
                        panelGameContainer.remove(0);

                        //write to players file
                        if (currentGame.getPuzzle().isFull()) {
                            currentGame.updatePlayersFile();
                            System.out.println("Loses: " + player.getDuidokuLoses());
                            System.out.println("Wins: " + player.getDuidokuWins());
                        }

                        //free memory
                        currentGame = null;
                        aButtonIsCurrentlyActive = false;

                        labelInstructions.setText(messages.getString("gameSelection"));
                    }
                } catch (IOException ex) {
                    System.out.println("Error within buttonExitGame's ActionListener");
                }
            }
        });

        //---BUTTON LANG
        buttonLang.addActionListener((ActionEvent e) -> {
            if(locale.getCountry().equals("GR"))
                locale = new Locale("en","UK");
            else
                locale = new Locale("el","GR");
            messages = ResourceBundle.getBundle("i18n.MessageListBundle",locale);
            changeLabelsLanguage();
        });

        //---BUTTON WORDOKU
        buttonWordoku.addActionListener((ActionEvent e) -> {
            DisplayType newType;
            if (currentGame.getType() == DisplayType.NUMBERS)
                newType = DisplayType.CAPITALS;
            else
                newType = DisplayType.NUMBERS;

            currentGame.changeRepresentation(newType);
            updateCellsRepresentation();
        });

        //---BUTTON ENTER
        buttonEnter.addActionListener((ActionEvent e) -> {
            player = new Player(fieldUsername.getText(),"players.dat");
            toggleChildButtons(panelButtons,true);
            panelUserInfo.setVisible(true);
            panelGameContainer.removeAll();
        });

        //---FIELD USERNAME >> CLICK
        fieldUsername.addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int k = keyEvent.getKeyCode();
                if(k == KeyEvent.VK_ENTER) {
                    player = new Player(fieldUsername.getText(),"players.dat");
                    toggleChildButtons(panelButtons,true);
                    panelUserInfo.setVisible(false);
                    panelGameContainer.removeAll();
                }
            }
        });

        //--- FIELD USERNAME >> ENTER
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String exitClose = messages.getString("exitClose");
                String exitProgramm = messages.getString("exitProgramm");
                int answer = JOptionPane.showConfirmDialog(frame,exitClose,exitProgramm,JOptionPane.YES_NO_OPTION);
                if(answer == JOptionPane.YES_OPTION)
                    frame.dispose();
            }
        });
    }

    /**
     * This function cretes a new game. Game mode is Classic Sudoku. It creates a new classic sudoku object that holds the
     * puzzle array and performs the game logic.
     * It creates a panel that contains all the cells. At the same time it assigns the cartesian coordinates to each cell button.
     */
    private void setupClassicSudoku() {
        panelGameClassic = new JPanel(new GridLayout(3,3,3,3));
        panelGameClassic.setBackground(COLOR_MEDIUM_GREY);
        panelGameContainer.add(panelGameClassic);

        currentGame = new ClassicSudoku(player);

        cells = new cellButton[81];
        squares = new JPanel[9];
        currentSquare = null;
        currentOriginI = 0;
        currentOriginJ = 0;
        int[] coordinates = {0,0};  //helper array to pass by reference and store the coordinates from getCoordinates
        int squaresCount = 0;

        //create cell (JButton) and assign coordinates (from linear 0-80 to cartesian [0,9]x[0,9])
        //coordinates are calculated at separate levels. First at table-level and then at square-level
        //square-level coordinates offest the table-level coordinates ('current origins')
        for(int k = 0; k < 81; k++) {
            //create the cell
            cells[k] = new cellButton();
            cells[k].setCoordLinear(k);

            //create square, set current origin
            if((k%9) == 0) {
                //helper variable to avoid calculating k%9 every time
                squaresCount++;

                //calculate table-level coordinates
                getCoordinates(squaresCount-1,3,1,coordinates);
                currentOriginI = coordinates[0];
                currentOriginJ = coordinates[1];

                //create square container (JPanel)
                squares[squaresCount-1] = new JPanel();

                //helper variable to use within these two loops
                currentSquare = squares[squaresCount-1];

                //set square appearence and add to game container (JPanel)
                currentSquare.setLayout(new GridLayout(3,3,0,0));
                currentSquare.setBorder(BorderFactory.createEmptyBorder());
                panelGameClassic.add(currentSquare);
            }

            //calculate square-level coordinates and use them to offset the table-level coordinates (current origins)
            getCoordinates(squaresCount == 1 ? k : (k % (9 * (squaresCount-1))),3,0,coordinates);

            cells[k].setCoordI(currentOriginI + coordinates[0]);
            cells[k].setCoordJ(currentOriginJ + coordinates[1]);

            //set cell appearance and add to square
            if(currentGame.getPuzzle().getState()[cells[k].getCoordI()][cells[k].getCoordJ()] == State.ACCESSIBLE) {
                cells[k].getButton().setBackground(COLOR_DEFAULT);
                cells[k].getButton().setText(" ");
            }
            else {
                cells[k].getButton().setBackground(new Color(185, 185, 185));
                cells[k].getButton().setText(Character.toString(currentGame.getRepresentation().getFormat()[currentGame.getPuzzle().getTable()[cells[k].getCoordI()][cells[k].getCoordJ()]]));
            }
            currentSquare.add(cells[k].getButton());
        }

        panelGameClassic.setVisible(true);
    }

    /**
     * This function cretes a new game. Game mode is Killer Sudoku. It creates a new killer sudoku object that holds the
     * puzzle array and performs the game logic.
     * It creates a panel that contains all the cells. At the same time it assigns the cartesian coordinates to each cell button.
     * It also paints the cells randomly a different color.
     */
    private void setupKillerSudoku() {
        panelGameKiller = new JPanel(new GridLayout(3,3,3,3));
        panelGameKiller.setBackground(COLOR_MEDIUM_GREY);
        panelGameContainer.add(panelGameKiller);

        currentGame = new KillerSudoku(player);

        cells = new cellButton[81];
        squares = new JPanel[9];
        currentSquare = null;
        currentOriginI = 0;
        currentOriginJ = 0;
        int[] coordinates = {0,0};  //helper array to pass by reference and store the coordinates from getCoordinates
        int squaresCount = 0;
        int numberOfRegions = ((KillerSudoku)currentGame).getNumberOfRegions();
        Color[] regionsColorsMap = new Color[numberOfRegions];

        Random r = new Random();
        for(int i=0; i<numberOfRegions; i++) {
            regionsColorsMap[i] = new Color(r.nextInt(120)+120, r.nextInt(120)+120, r.nextInt(120)+120);
        }

        //create cell (JButton) and assign coordinates (from linear 0-80 to cartesian [0,9]x[0,9])
        //coordinates are calculated at separate levels. First at table-level and then at square-level
        //square-level coordinates offest the table-level coordinates ('current origins')
        for(int k = 0; k < 81; k++) {
            //create the cell
            cells[k] = new cellButton();
            cells[k].setCoordLinear(k);

            //create square, set current origin
            if((k%9) == 0) {
                //helper variable to avoid calculating k%9 every time
                squaresCount++;

                //calculate table-level coordinates
                getCoordinates(squaresCount-1,3,1,coordinates);
                currentOriginI = coordinates[0];
                currentOriginJ = coordinates[1];

                //create square container (JPanel)
                squares[squaresCount-1] = new JPanel();

                //helper variable to use within these two loops
                currentSquare = squares[squaresCount-1];

                //set square appearence and add to game container (JPanel)
                currentSquare.setLayout(new GridLayout(3,3,0,0));
                currentSquare.setBorder(BorderFactory.createEmptyBorder());
                panelGameKiller.add(currentSquare);
            }

            //calculate square-level coordinates and use them to offset the table-level coordinates (current origins)
            getCoordinates(squaresCount == 1 ? k : (k % (9 * (squaresCount-1))),3,0,coordinates);

            cells[k].setCoordI(currentOriginI + coordinates[0]);
            cells[k].setCoordJ(currentOriginJ + coordinates[1]);

            //set cell appearance and add to square
            cells[k].getButton().setBackground(regionsColorsMap[((KillerSudoku)currentGame).getCellRegion(cells[k].getCoordI(),cells[k].getCoordJ())]);
            cells[k].setDefaultColor(regionsColorsMap[((KillerSudoku)currentGame).getCellRegion(cells[k].getCoordI(),cells[k].getCoordJ())]);
            cells[k].getButton().setText(" ");
        }

        panelGameKiller.setVisible(true);
    }

    private void setupDuidoku() {
        panelGameDuidoku = new JPanel(new GridLayout(2,2,1,1));
        panelGameDuidoku.setBackground(COLOR_MEDIUM_GREY);
        panelGameContainer.add(panelGameDuidoku);

        currentGame = new Duidoku(player);

        cells = new cellButton[16];
        squares = new JPanel[4];
        currentSquare = null;
        currentOriginI = 0;
        currentOriginJ = 0;
        int[] coordinates = {0,0};  //helper array to pass by reference and store the coordinates from getCoordinates
        int squaresCount = 0;

        //create cell (JButton) and assign coordinates (from linear 0-80 to cartesian [0,9]x[0,9])
        //coordinates are calculated at separate levels. First at table-level and then at square-level
        //square-level coordinates offest the table-level coordinates ('current origins')
        for(int k = 0; k < 16; k++) {
            //create the cell
            cells[k] = new cellButton();
            cells[k].setCoordLinear(k);

            //create square, set current origin
            if((k%4) == 0) {
                //helper variable to avoid calculating k%9 every time
                squaresCount++;

                //calculate table-level coordinates
                getCoordinates(squaresCount-1,2,1,coordinates);
                currentOriginI = coordinates[0];
                currentOriginJ = coordinates[1];

                //create square container (JPanel)
                squares[squaresCount-1] = new JPanel();

                //helper variable to use within these two loops
                currentSquare = squares[squaresCount-1];

                //set square appearence and add to game container (JPanel)
                currentSquare.setLayout(new GridLayout(2,2,0,0));
                currentSquare.setBorder(BorderFactory.createEmptyBorder());
                panelGameDuidoku.add(currentSquare);
            }

            //calculate square-level coordinates and use them to offset the table-level coordinates (current origins)
            getCoordinates(squaresCount == 1 ? k : (k % (4 * (squaresCount-1))),2,0,coordinates);

            cells[k].setCoordI(currentOriginI + coordinates[0]);
            cells[k].setCoordJ(currentOriginJ + coordinates[1]);

            //set cell appearance and add to square
            cells[k].getButton().setBackground(COLOR_DEFAULT);
            cells[k].getButton().setText(" ");

            currentSquare.add(cells[k].getButton());
        }

        panelGameDuidoku.setVisible(true);
    }

    /**
     * A function that maps the set [1,9] to [0,2]x[0,2]. Linear to cartesian coordinates
     * Note that the coordinates can be calculated at many levels. This is used when the arrangement is fractal-like.
     *
     * @param pos    Linear coordinate.
     * @param coords Cartesian coordinates. 2-element array
     * @param level  The level on which we want to calculate the coordinates. The coordinates are offset accordingly.
     */
    private void getCoordinates(int pos, int dimension, int level, int[] coords){
        coords[0] = (pos/dimension)*(int)Math.pow(dimension,level);
        coords[1] = (pos%dimension)*(int)Math.pow(dimension,level);
    }

    /**
     * This method receives a JComponent and either disables or enaables its children that are JButtons
     *
     * @param c The panrent component
     * @param b The boolean value. On or off.
     */
    private void toggleChildButtons(JComponent c, boolean b){
        Component[] array = c.getComponents();
        int  len = array.length;
        for(int i =0; i< len; i++){
            if(array[i] instanceof JButton) {
                array[i].setEnabled(b);
                ((JButton) array[i]).setBorder(new EmptyBorder(0,0,0,0));
            }
        }
    }

    private void changeLabelsLanguage() {
        labelInstructions.setText(messages.getString("welcome"));
        lbl1.setText(messages.getString("tools"));
        lbl2.setText(messages.getString("gameSelection"));
        lbl3.setText(messages.getString("clickToUndo"));
        lbl4.setText(messages.getString("clickToExit"));
        buttonWordoku.setText(messages.getString("wordoku"));
        buttonHelp.setText(messages.getString("help"));
        buttonLang.setText(messages.getString("language"));
        buttonSudoku.setText(messages.getString("sudoku"));
        buttonKillerSudoku.setText(messages.getString("killerSudoku"));
        buttonDuidoku.setText(messages.getString("duidoku"));
        buttonUndo.setText(messages.getString("undo"));
        buttonExitGame.setText(messages.getString("exit"));
    }

    /**
     * Updates the display type. Letters or words
     */
    private void updateCellsRepresentation() {
        int numOfCells = (int) Math.pow(currentGame.getPuzzle().getDimention(),2);
        for(int k = 0; k < numOfCells; k++)
            cells[k].getButton().setText(Character.toString(currentGame.getRepresentation().getFormat()[currentGame.getPuzzle().getTable()[cells[k].getCoordI()][cells[k].getCoordJ()]]));
    }

    private void updateStats(boolean isWin) {
        if (currentGame instanceof Duidoku)
            if (isWin)
                player.increaseDuidokuWins();
            else
                player.increaseDuidokuLoses();
        else if(currentGame instanceof ClassicSudoku)
            player.getHasPlayedSudoku()[currentGame.getPuzzleIndex() - 1] = true;
        else
            player.getHasPlayedKillerSudoku()[currentGame.getPuzzleIndex()-1] = true;
    }

    private void refreshTable() {
        int numOfCells = (int) Math.pow(currentGame.getPuzzle().getDimention(),2);
        for(int k = 0; k < numOfCells; k++)
            cells[k].getButton().setText(String.valueOf(currentGame.getRepresentation().getFormat()[currentGame.getPuzzle().getTable()[cells[k].getCoordI()][cells[k].getCoordJ()]]));
    }
}