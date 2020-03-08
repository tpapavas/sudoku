package gui;

import auxiliary.Duplet;
import auxiliary.GameType;
import com.sun.tools.javac.Main;
import player.*;
import puzzle.*;
import sudokuBundle.*;

import javax.swing.*;
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

    private int lastMoveLinear;

    private boolean aButtonIsCurrentlyActive;
    private boolean aGameIsCurrentlyActive;
    private int currentSelection;
    private boolean displayHints;

    //GUI elements
    private JFrame frame;
    private JPanel panelButtons,panelGameContainer,panelUserInfo;
    private JLabel labelInstructions,lbl1,lbl2,lbl3,lbl4;
    private JButton buttonEnter,buttonWordoku,buttonHelp,buttonSudoku,buttonKillerSudoku,buttonDuidoku,buttonUndo,buttonExitGame,buttonLang;
    private CellButton[] cells;
    private JTextField fieldUsername;

    private Sudoku game;
    private Player player;
    private GameType gameType;

    private Locale locale;
    private ResourceBundle messages;

    public GUI() {
        lastMoveLinear = -1;
        aButtonIsCurrentlyActive = false;
        aGameIsCurrentlyActive = false;
        currentSelection = -1;
        displayHints = false;

        locale = new Locale("en","UK");
        messages = ResourceBundle.getBundle("i18n.MessageListBundle",locale);

        frame = new JFrame("Sudoku Game Suite");

        JPanel panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());

        //setup panelButtons
        panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(13,1));
        panelButtons.setBorder(new EmptyBorder(12,7,12,7));

        //setup panelGameContainer
        panelGameContainer = new JPanel(new GridLayout(1,1));
        panelGameContainer.setBorder(new EmptyBorder(10,10,10,10));
        panelGameContainer.setBackground(COLOR_MEDIUM_GREY);

        //setup labelInstructions
        labelInstructions = new JLabel(messages.getString("welcome"));
        labelInstructions.setBorder(new EmptyBorder(5,5,5,5));

        panelMain.add(labelInstructions,BorderLayout.PAGE_START);
        panelMain.add(panelButtons,BorderLayout.LINE_START);
        panelMain.add(panelGameContainer,BorderLayout.CENTER);

        //initialize buttons
        buttonHelp = new JButton(messages.getString("help"));
        buttonWordoku = new JButton(messages.getString("wordoku"));
        buttonSudoku = new JButton(messages.getString("sudoku"));
        buttonKillerSudoku = new JButton(messages.getString("killerSudoku"));
        buttonDuidoku = new JButton(messages.getString("duidoku"));
        buttonUndo = new JButton(messages.getString("undo"));
        buttonExitGame = new JButton(messages.getString("exit"));
        buttonLang = new JButton(messages.getString("language"));

        //setup labels
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

        //setup panelUserInfo
        panelUserInfo = new JPanel(new BorderLayout());
        panelUserInfo.setBorder(new EmptyBorder(120,10,235,10));
        panelUserInfo.setBackground(COLOR_DEFAULT);

        fieldUsername = new JTextField("",15);
        JLabel labelUserInfo = new JLabel(messages.getString("username"));
        buttonEnter = new JButton("Enter");

        panelUserInfo.add(buttonEnter,BorderLayout.LINE_END);
        panelUserInfo.add(labelUserInfo,BorderLayout.NORTH);
        panelUserInfo.add(fieldUsername,BorderLayout.CENTER);

        panelGameContainer.add(panelUserInfo);

        toggleChildButtons(panelButtons,false);

        setupActionListeners();

        //setup menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener((ActionEvent e) -> {
            if(player != null) {
                Duplet duplet = new Duplet(game.getPuzzleIndex(),gameType);
                player.getProgress().updateData(duplet, game.getPuzzle());
                System.out.println("Saved");
            }
        });

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener((ActionEvent e)->{
            JOptionPane.showMessageDialog(frame,"Sudoku Game" + "\n" + "Version 2.2","INFO",JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem statsItem = new JMenuItem("Show Stats");
        statsItem.addActionListener((ActionEvent e)->{
            StringBuilder stats = new StringBuilder();
            StringBuilder classics = new StringBuilder();
            StringBuilder killers = new StringBuilder();

            ArrayList<ArrayList<Integer>> indexes = player.puzzlePlayed();

            for(Integer x : indexes.get(GameType.CLASSIC_SUDOKU.getCode()))
                classics.append(x).append(' ');
            for(Integer x : indexes.get(GameType.KILLER_SUDOKU.getCode()))
                killers.append(x).append(' ');
            
            stats.append("Classic Puzzles played").append('\n');
            stats.append("  ").append(classics).append('\n');
            stats.append("Killer Puzzles played").append('\n');
            stats.append("  ").append(killers).append('\n');
            stats.append("Duidoku").append('\n');
            stats.append("  wins:  ").append(player.getDuidokuWins()).append('\n');
            stats.append("  loses: ").append(player.getDuidokuLoses());
            JOptionPane.showMessageDialog(frame,stats,player.getNickname()+ "'s stats",JOptionPane.INFORMATION_MESSAGE);
        });

        fileMenu.add(saveItem);
        viewMenu.add(statsItem);
//        statsItem.setEnabled(false);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);
        ////////


        frame.add(panelMain);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(650,500);
        frame.setResizable(false);
        frame.setVisible(true);

        fieldUsername.requestFocusInWindow();
    }

    private class CellButton {
        private JButton button;
        private boolean isClicked;
        private boolean enteredIllegalNumber;

        private int displayValue;
        private int coordI;
        private int coordJ;
        private int coordLinear;

        private Color defaultColor;

        public CellButton() {
            button = new JButton();
            isClicked = false;
            enteredIllegalNumber = true;
            displayValue = 0;
            coordI = -1;
            coordJ = -1;
            coordLinear = -1;
            defaultColor = COLOR_DEFAULT;

            button.addActionListener((ActionEvent e) -> {
                if (isClicked && aButtonIsCurrentlyActive)
                    release();
                else if (!isClicked && !aButtonIsCurrentlyActive && (game.getPuzzle().getState()[coordI][coordJ] == State.ACCESSIBLE)) {
                    select();
                    //cells[15].button.setEnabled(false);
                }
            });

            button.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    int inputUpperLimit, inputLowerLimit;
                    if (game.getType() == DisplayType.NUMBERS) {
                        inputUpperLimit = game instanceof Duidoku ? KeyEvent.VK_4 : KeyEvent.VK_9;
                        inputLowerLimit = KeyEvent.VK_1;
                    } else {
                        inputUpperLimit = game instanceof Duidoku ? KeyEvent.VK_D : KeyEvent.VK_I;
                        inputLowerLimit = KeyEvent.VK_A;
                    }

                    int offset = game.getType() == DisplayType.NUMBERS ? 0 : 16;
                    int keyCode = keyEvent.getKeyCode();

                    if (isClicked) {
                        if (keyCode == KeyEvent.VK_ENTER)
                            release();

                        if (keyCode >= inputLowerLimit && keyCode <= inputUpperLimit) {
                            displayValue = keyCode - 48 - offset;

                            if (!game.isLegalMove(displayValue, coordI, coordJ)) {
                                labelInstructions.setText(messages.getString("illegal"));
                                enteredIllegalNumber = true;

                                /*
                                //timer to change to warning bg cell colour
                                Timer timer = new Timer(100, new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent actionEvent) {
                                        button.setBackground(COLOR_WRONG);
                                    }
                                });
                                timer.setRepeats(false);
                                timer.start(); */

                                button.setBackground(COLOR_WRONG);

                                game.getPuzzle().setValue(0, coordI, coordJ);
                            } else {
                                labelInstructions.setText(messages.getString("label"));
                                enteredIllegalNumber = false;
                                button.setBackground(COLOR_HIGHLIGHTED);
                                lastMoveLinear = coordLinear;
                            }

                            button.setText(String.valueOf(game.getRepresentation().getFormat()[displayValue]));
                        }
                    }
                }
            });

            if (game instanceof KillerSudoku) {
                button.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (!aButtonIsCurrentlyActive)
                            if (!displayHints)
                                labelInstructions.setText(messages.getString("regionSum") + ((KillerSudoku) game).getRegionSum(coordI, coordJ));
                    }
                });
            }
        }

        private void release() {
            if (enteredIllegalNumber)
                button.setText(" ");
            else {
                game.doMove(displayValue, coordI, coordJ);
                if (game.getPuzzle().isFull()) {
                    labelInstructions.setText(messages.getString("congrats"));
                    updateStats(true);
                }
                if (game instanceof Duidoku) {
                    if (((Duidoku) game).pcMove()) {
                        if (game.getPuzzle().isFull()) {
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

            if (displayHints) {
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
        public JButton getButton() { return button; }
        public int getCoordI() { return coordI; }
        public int getCoordJ() { return coordJ; }

        //setters
        public void setCoordI(int c) { coordI = c; }
        public void setCoordJ(int c) { coordJ = c; }
        public void setCoordLinear(int c) { coordLinear = c; }
        public void setDefaultColor(Color c) { defaultColor = c; }
    }

    private void setupActionListeners() {
        class GameListener implements ActionListener {
            GameType gameType;

            GameListener(GameType gameType) { this.gameType = gameType; }

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!aGameIsCurrentlyActive) {
                    setupGame(gameType);
                    labelInstructions.setText(messages.getString("label"));
                    aGameIsCurrentlyActive = true;
                }
            }
        }

        //---BUTTON SUDOKU
        buttonSudoku.addActionListener(new GameListener(GameType.CLASSIC_SUDOKU));
        //---BUTTON KILLER SUDOKU
        buttonKillerSudoku.addActionListener(new GameListener(GameType.KILLER_SUDOKU));
        //---BUTTON DUIDOKU
        buttonDuidoku.addActionListener(new GameListener(GameType.DUIDOKU));

        //---BUTTON UNDO
        buttonUndo.addActionListener((ActionEvent e) -> {
            int ind = lastMoveLinear;
            game.undoMove(cells[ind].getCoordI(),cells[ind].getCoordJ());
        });

        //---BUTTON HELP
        buttonHelp.addActionListener((ActionEvent e) -> {
            if(currentSelection != -1) {
                displayHints = true;

                ArrayList<Integer> hints;
                hints = game.help().get(cells[currentSelection].getCoordI()).get(cells[currentSelection].getCoordJ());

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
                        //if (game.getPuzzle().isFull()) {
                            game.updatePlayersFile();
                            System.out.println("Loses: " + player.getDuidokuLoses());
                            System.out.println("Wins: " + player.getDuidokuWins());
                        //}

                        //free memory
                        game = null;
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
            if (game.getType() == DisplayType.NUMBERS)
                newType = DisplayType.CAPITALS;
            else
                newType = DisplayType.NUMBERS;

            game.changeRepresentation(newType);
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

    private void setupGame(GameType gameType) {
        initializeGame(gameType);

        JPanel currentSquare = null;
        JPanel[] squares;

        int currentOriginI = 0;
        int currentOriginJ = 0;
        int[] coordinates = {0,0};  //helper array to pass by reference and store the coordinates from getCoordinates
        int squaresCount = 0;
        int dimension = game.getPuzzle().getDimension();
        int length = game.getPuzzle().getLength();
        int numOfCells = game.getPuzzle().getNumOfCells();

        cells = new CellButton[numOfCells];
        squares = new JPanel[length];

        JPanel panelGame = new JPanel(new GridLayout(dimension,dimension,3,3));
        panelGame.setBackground(COLOR_MEDIUM_GREY);
        panelGameContainer.add(panelGame);

        //create cell (JButton) and assign coordinates (from linear 0-80 to cartesian [0,9]x[0,9])
        //coordinates are calculated at separate levels. First at table-level and then at square-level
        //square-level coordinates offest the table-level coordinates ('current origins')
        for(int k = 0; k < numOfCells; k++) {
            //create the cell
            cells[k] = new CellButton();
            cells[k].setCoordLinear(k);

            //create square, set current origin
            if((k % length) == 0) {
                //helper variable to avoid calculating k%9 every time
                squaresCount++;

                //calculate table-level coordinates
                getCoordinates(squaresCount-1,dimension,1,coordinates);
                currentOriginI = coordinates[0];
                currentOriginJ = coordinates[1];

                //create square container (JPanel)
                squares[squaresCount-1] = new JPanel();

                //helper variable to use within these two loops
                currentSquare = squares[squaresCount-1];

                //set square appearence and add to game container (JPanel)
                currentSquare.setLayout(new GridLayout(dimension,dimension,0,0));
                currentSquare.setBorder(BorderFactory.createEmptyBorder());
                panelGame.add(currentSquare);
            }

            //calculate square-level coordinates and use them to offset the table-level coordinates (current origins)
            getCoordinates(squaresCount == 1 ? k : (k % (length*(squaresCount-1))),dimension,0,coordinates);

            cells[k].setCoordI(currentOriginI + coordinates[0]);
            cells[k].setCoordJ(currentOriginJ + coordinates[1]);

            //set cell appearance and add to square
            if(game.getPuzzle().getState()[cells[k].getCoordI()][cells[k].getCoordJ()] == State.ACCESSIBLE)
                cells[k].getButton().setBackground(COLOR_DEFAULT);
            else
                cells[k].getButton().setBackground(new Color(185, 185, 185));
            cells[k].getButton().setText(Character.toString(game.getRepresentation().getFormat()[game.getPuzzle().getTable()[cells[k].getCoordI()][cells[k].getCoordJ()]]));

            currentSquare.add(cells[k].getButton());
        }

        if(gameType == GameType.KILLER_SUDOKU) {
            int numberOfRegions = ((KillerSudoku) game).getNumberOfRegions();
            Color[] regionsColorsMap = new Color[numberOfRegions];

            Random r = new Random();
            for(int i=0; i<numberOfRegions; i++) {
                regionsColorsMap[i] = new Color(r.nextInt(120)+120, r.nextInt(120)+120, r.nextInt(120)+120);
            }

            //set cell appearance and add to square
            for(int k = 0; k < game.getPuzzle().getNumOfCells(); k++) {
                cells[k].getButton().setBackground(regionsColorsMap[((KillerSudoku) game).getCellRegion(cells[k].getCoordI(), cells[k].getCoordJ())]);
                cells[k].setDefaultColor(regionsColorsMap[((KillerSudoku) game).getCellRegion(cells[k].getCoordI(), cells[k].getCoordJ())]);
            }
        }

        panelGame.setVisible(true);
    }

    /**
     * Creates a new Sudoku, based on value of gameType variable.
     * @param gameType the type of Sudoku.
     */
    private void initializeGame(GameType gameType) {
        switch (gameType) {
            case CLASSIC_SUDOKU:
                game = new ClassicSudoku(player);
                this.gameType = GameType.CLASSIC_SUDOKU;
                break;

            case KILLER_SUDOKU:
                game = new KillerSudoku(player);
                this.gameType = GameType.KILLER_SUDOKU;
                break;

            default:
                game = new Duidoku(player);
                this.gameType = GameType.DUIDOKU;
        }
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
        int numOfCells = game.getPuzzle().getNumOfCells();
        for(int k = 0; k < numOfCells; k++)
            cells[k].getButton().setText(Character.toString(game.getRepresentation().getFormat()[game.getPuzzle().getTable()[cells[k].getCoordI()][cells[k].getCoordJ()]]));
    }

    private void updateStats(boolean isWin) {
        if (game instanceof Duidoku)
            if (isWin)
                player.increaseDuidokuWins();
            else
                player.increaseDuidokuLoses();
        else if(game instanceof ClassicSudoku)
            player.getHasPlayedSudoku()[game.getPuzzleIndex() - 1] = true;
        else
            player.getHasPlayedKillerSudoku()[game.getPuzzleIndex()-1] = true;
    }

    private void refreshTable() {
        int numOfCells = game.getPuzzle().getNumOfCells();
        for(int k = 0; k < numOfCells; k++)
            cells[k].getButton().setText(String.valueOf(game.getRepresentation().getFormat()[game.getPuzzle().getTable()[cells[k].getCoordI()][cells[k].getCoordJ()]]));
    }
}