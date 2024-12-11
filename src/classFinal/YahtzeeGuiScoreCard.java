package classFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class YahtzeeGuiScoreCard extends JComponent {


    // Define how big the score card will be
    public static final int xSize = 400;
    public static final int ySize = 700;
    // Define initial position where we will draw the score
    public static final int xPosition = 10;
    public static final int yPosition = 30;
    // Define size of the line drawn as a delimeter
    public static final int horizontalLineLength = (xSize*19)/20;
    public static final int verticalLineSpacing = 20;
    // Define font used for drawing
    public static final String font = "Arial";
    public static final int fontSize = 16;
    private JLabel[] roundScores = new JLabel[YahtzeeGame.HANDS];

    // Plus 1 to include the Final Score
    private JLabel[] recScores = new JLabel[YahtzeeGame.HANDS + 1];
    private JLabel upperSectionBonus;
    private YahtzeeGame yahtzeeGame;
    private JFrame f;
    private JLabel roundCounter = new JLabel("Round 1");
    private JLabel gameCounter = new JLabel("Game 1");
    private JLabel rollCounter = new JLabel("Roll 1");
    private DiceButton[] clickDice = new DiceButton[5];
    private boolean[] isReroll = new boolean[5];
    private JLabel[] diceLabels = new JLabel[5];
    private JLabel upperSectionTotal = new JLabel();
    private JLabel lowerSectionTotal = new JLabel();

    public void setRoundCounterText(String text) {
        roundCounter.setText(text);
    }

    public void setRollCounterText(String text) {
        rollCounter.setText(text);
    }

    public void setGameCounterText(String text) {
        gameCounter.setText(text);
    }

    public YahtzeeGuiScoreCard(YahtzeeGame yahtzeeGame){

        this.yahtzeeGame = yahtzeeGame;

        for (int i = 0; i < YahtzeeGame.HANDS; i++) {
            roundScores[i] = new JLabel();
            recScores[i] = new JLabel();
        }
        recScores[14] = new JLabel();

        for(int i = 0; i < diceLabels.length; i++){
            diceLabels[i] = new JLabel();
        }
        upperSectionBonus = new JLabel();

        gameCounter.setBounds(xPosition + 225, yPosition - 60, 100, 100);
        this.add(gameCounter);
        roundCounter.setBounds(xPosition + 325, yPosition - 60, 100, 100);
        this.add(roundCounter);
        rollCounter.setBounds(xPosition + 50, yPosition + 415, 100, 100);
        this.add(rollCounter);

        for(int i = 0; i < 5; i++){
            DiceButton diceButton = new DiceButton(i);
            clickDice[i] = diceButton;
            diceButton.setBounds(xPosition + 75 + 50 * i, yPosition + 530, 40, 20);
            diceButton.addActionListener(getDiceSelectActionListener());
            diceButton.setText("Keep");
            this.add(diceButton);
        }

        JButton rerollButton=new JButton("Reroll");
        rerollButton.setBounds(xPosition + 150, yPosition + 565, 95, 30);
        rerollButton.addActionListener(getRerollActionListener());
        this.add(rerollButton);

        JButton quitButton = new JButton("Quit");
        quitButton.setBounds(xPosition + 285, yPosition + 600, 95, 30);
        quitButton.addActionListener(getQuitActionListener());
        this.add(quitButton);

        displayRecordedScores(yahtzeeGame.getScore());
        yahtzeeGame.getScore().setRoundScores();

        this.create();
    }

    private ActionListener getDiceSelectActionListener(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DiceButton button = (DiceButton) e.getSource();
                int id = button.diceIndex;
                isReroll[id] = !isReroll[id];
                button.setText(isReroll[id] ? "Roll" : "Keep");
            }
        };
    }

    private ActionListener getQuitActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
    }

    private ActionListener getRerollActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (yahtzeeGame.getRerolls() < 2){
                    yahtzeeGame.getHand().changeHand(isReroll);
                    yahtzeeGame.incrementRerolls();
                }
                yahtzeeGame.getScore().setDice(yahtzeeGame.getHand().getDice());
                yahtzeeGame.getScore().setRoundScores();

                rollCounter.setText("Roll " + (yahtzeeGame.getRerolls() + 1));
                displayRoundScores(yahtzeeGame.getScore().getRoundScores(), false);
            }
        };
    }

    public void displayRoundScores(Integer[] scores, boolean isReset){
        for(int i = 0; i < 14; i++){
            roundScores[i].setText(isReset ? "" : scores[i].toString());
        }
    }

    public void displayRecordedScores(YahtzeeScore yahtzeeScore){
        Integer[] recordedScores = yahtzeeScore.getRecordedScores();
        for(int i = 0; i < YahtzeeGame.HANDS; i++){
            recScores[i].setText(recordedScores[i] == null ? "" : recordedScores[i].toString());
        }
        yahtzeeGame.getScore().calculateSectionTotals();
        upperSectionTotal.setText(String.valueOf(yahtzeeScore.getUpperSectionTotal()));
        upperSectionBonus.setText(String.valueOf(yahtzeeScore.scoreUpperSectionBonus()));
        lowerSectionTotal.setText(String.valueOf(yahtzeeScore.getLowerSectionTotal()));
        recScores[14].setText(String.valueOf(yahtzeeScore.getFinalScore()));
    }

    @Override
    public void paintComponent(Graphics g) {

        // Fill the background and title
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, xSize, ySize);
        g.setFont(new Font(font, Font.BOLD, fontSize * 2)); g.setColor(Color.RED);
        g.drawString("Yahtzee!", xPosition, yPosition-3);

        // Print table headers
        int numberOfLines = 1;
        g.setFont(new Font(font, Font.BOLD, fontSize)); g.setColor(Color.BLACK);
        drawNewLine(g, numberOfLines, "Upper Section");
        g.drawString("Round Scores", xPosition + 125, yPosition + verticalLineSpacing);
        g.drawString("Recorded Scores", xPosition + 245, yPosition + verticalLineSpacing);
        numberOfLines++;

        // Print Upper Section
        g.setFont(new Font(font, Font.PLAIN, fontSize)); g.setColor(Color.BLACK);
        String[] upperSectionLabels = {"Aces: ", "Twos: ", "Threes: ", "Fours: ", "Fives: ", "Sixes: ", "Section Total: ", "Section Bonus: "};
        for(String s: upperSectionLabels){
            drawNewLine(g, numberOfLines, s);
            numberOfLines++;
        }

        // Print Lower Section
        g.setFont(new Font(font, Font.BOLD, fontSize)); g.setColor(Color.BLACK);
        drawNewLine(g, numberOfLines, "Lower Section");
        numberOfLines++;
        g.setFont(new Font(font, Font.PLAIN, fontSize)); g.setColor(Color.BLACK);
        String[] lowerSectionLabels = {"Chance: ", "3-of-a-kind: ", "4-of-a-kind: ", "Full House: ", "Small Straight: ", "Full Straight: ", "Yahtzee: ", "Bonus Yahtzee: ", "Section Total"};
        for(String s: lowerSectionLabels){
            drawNewLine(g, numberOfLines, s);
            numberOfLines++;
        }
        numberOfLines++;

        // Print Final Score
        g.setFont(new Font(font, Font.BOLD, fontSize)); g.setColor(Color.BLACK);
        drawNewLine(g, numberOfLines, "Final Score:");

        // Print table lines
        g.drawLine(xPosition + 120, yPosition + 3, xPosition + 120, yPosition + 19 * verticalLineSpacing + 2);
        g.drawLine(xPosition + 240, yPosition + 3, xPosition + 240, yPosition + 19 * verticalLineSpacing + 2);

        // Set score positions
        for(int i = 0; i < 6; i++){
            roundScores[i].setBounds(xPosition + 122,yPosition + verticalLineSpacing * (i + 1) - 3, 100,30);
            this.add(roundScores[i]);
        }

        for(int i = 6; i < 14; i++){
            roundScores[i].setBounds(xPosition + 122,yPosition + verticalLineSpacing * (i + 4) - 3, 100,30);
            this.add(roundScores[i]);
        }

        for(int i = 0; i < 6; i++){
            recScores[i].setBounds(xPosition + 242,yPosition + verticalLineSpacing * (i + 1) - 3, 100,30);
            this.add(recScores[i]);
        }
        upperSectionBonus.setBounds(xPosition + 242,yPosition + verticalLineSpacing * 8 - 3, 100,30);
        this.add(upperSectionBonus);
        upperSectionTotal.setBounds(xPosition + 242,yPosition + verticalLineSpacing * 7 - 3, 100,30);
        this.add(upperSectionTotal);
        for(int i = 6; i < 14; i++){
            recScores[i].setBounds(xPosition + 242,yPosition + verticalLineSpacing * (i + 4) - 3, 100,30);
            this.add(recScores[i]);
        }
        lowerSectionTotal.setBounds(xPosition + 242,yPosition + verticalLineSpacing * 18 - 3, 100,30);
        this.add(lowerSectionTotal);
        recScores[14].setBounds(xPosition + 242,yPosition + (verticalLineSpacing * 20) - 3, 100,30);
        this.add(recScores[14]);

        // Draw the dice squares
        for(int i = 0; i < diceLabels.length; i++){
            g.drawRect(xPosition + 75 + 50 * i, yPosition + 480, 40, 40);
            diceLabels[i].setBounds(xPosition + 90 + 50 * i, yPosition + 490, 20, 20);
            diceLabels[i].setText(yahtzeeGame.getHand().get(i).toString());
            this.add(diceLabels[i]);
        }

        this.setVisible(true);

        // display round scores
        displayRoundScores(yahtzeeGame.getScore().getRoundScores(), false);
    }

    public void drawNewLine (Graphics g, int lineNumber, String lineText){
        g.drawLine(xPosition, yPosition + lineNumber * verticalLineSpacing + 2, xPosition + horizontalLineLength, yPosition + lineNumber * verticalLineSpacing + 2);
        g.drawString(lineText, xPosition, yPosition + lineNumber * verticalLineSpacing);
    }

    private void create() {
        // Example how to create GUI components
        f = new JFrame("Yahtzee Scorecard");
        f.add(this);
        f.setPreferredSize(new Dimension(xSize,ySize));
        f.pack();
        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        f.setLocation(screenSize.width - xSize, screenSize.height/2 - ySize/2);
        f.setVisible(true);

        f.addMouseListener(new MouseListener(){
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedBox = getClickedBox(e.getX(), e.getY());
                yahtzeeGame.scoreHand(yahtzeeGame.getHand(), clickedBox);
            }
        });
    }
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(f, message);
    }

    private static int getClickedBox(int x, int y) {
        if(x >= 250) {
            if (y >= 83 && y <= 83 + verticalLineSpacing * 6) {
                return (y - 83) / verticalLineSpacing;
            } else if (y >= 63 + verticalLineSpacing * 10 && y <= 63 + verticalLineSpacing * 17) {
                return (y - 143) / verticalLineSpacing;
            }
        }
        return -1;
    }

    private static class DiceButton extends JButton{
        private int diceIndex;

        DiceButton(int index){
            super();
            this.diceIndex = index;
        }
    }
}