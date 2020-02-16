package gui;

import player.Player;
import puzzle.DisplayType;
import sudokuBundle.ClassicSudoku;
import sudokuBundle.KillerSudoku;
import sudokuBundle.Sudoku;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
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

        public cellButton() {}

        private void release() {}
        private void select() {}

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
        ActionListener gameListener = (ActionEvent e) -> {
            if(aGameIsCurrentlyActive)
                try {
                    if (currentGame instanceof ClassicSudoku)
                        setupClassicSudoku();
                    if (currentGame instanceof KillerSudoku)
                        setupKillerSudoku();
                    else
                        setupDuidoku();
                } catch (Exception ex) {
                    System.out.println("Error in actionListener.");
                }
        };

        //---BUTTON SUDOKU
        buttonSudoku.addActionListener(gameListener);
        //---BUTTON KILLER SUDOKU
        buttonKillerSudoku.addActionListener(gameListener);
        //---BUTTON DUIDOKU
        buttonDuidoku.addActionListener(gameListener);

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
                        if (currentGame.getPuzzle().isFull())
                            currentGame.updatePlayersFile();

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
    }

    private void setupClassicSudoku() throws Exception {}
    private void setupKillerSudoku() throws Exception {}
    private void setupDuidoku() throws Exception {}

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
        coords[1] = (int) (pos%dimension)*(int)Math.pow(dimension,level);
    }

    /**
     * This method receives a Jcomponent and either disables or enaables its children that are JButtons
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

    private void changeLabelsLanguage() {}
    private void updateCellsRepresentation() {}
}