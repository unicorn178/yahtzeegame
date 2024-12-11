package classFinal;

/**
 * Runs each round of the Yahtzee game.
 * @author Owen Tang
 */

public class YahtzeeGame implements YathzeeScoreCard {

    public static int GAMES = 3;
    public static int ROUNDS = 13;
    public static int DICES = 5;

    public static int HANDS = 14;
    private YahtzeeScore score;
    private int rerolls = 0;
    private int round = 0;
    private int gameCount = 1;

    private int grandTotal = 0;
    private YahtzeeHand hand;
    private YahtzeeGuiScoreCard guiCard;

    public YahtzeeHand getHand() {
        return hand;
    }

    public YahtzeeGame(){
        this.hand = new YahtzeeHand();
        hand.rollAll();
        this.score = new YahtzeeScore(this.hand.getDice());
        this.guiCard = new YahtzeeGuiScoreCard(this);
    }
    public int getRerolls() {
        return rerolls;
    }
    public int incrementRerolls(){
        return ++rerolls;
    }
    public void setRerolls(int r){
        rerolls = r;
    }
    public int incrementRounds(){
        return ++round;
    }
    public void setRound(int r){
        round = r;
    }
    public int incrementGameCount(){
        return ++gameCount;
    }

    public YahtzeeScore getScore(){
        return this.score;
    }
    // Initializes the GUI.
    public void playRound() {
        guiCard.displayRoundScores(score.getRoundScores(), true);
        score.setDice(hand.getDice());
    }

    @Override
    public void scoreHand(YahtzeeHand yahtzee, int game) {
        if (gameCount > GAMES) {
            return;
        }

        Integer[] recordedScores = null;
        if(game != -1 && !score.getIsScoreFilled()[game]) {
            score.setDice(yahtzee.getDice());
            score.getIsScoreFilled()[game] = true;
            score.recordScore(game);
            recordedScores = score.getRecordedScores();
            incrementRounds();
            setRerolls(0);
            yahtzee.rollAll();
            score.setDice(yahtzee.getDice());
            score.setRoundScores();
            guiCard.displayRecordedScores(score);
            guiCard.setRollCounterText("Roll " + (rerolls + 1));
            guiCard.setRoundCounterText("Round " + (round + 1));
            guiCard.setGameCounterText("Game " + this.gameCount);
        }

        if (round == ROUNDS) {
            grandTotal += score.getFinalScore();
            for(int i = 0; i < recordedScores.length; i++){
                recordedScores[i] = null;
            }
            score.resetScores();
            recordedScores[13] = 0;
            recordedScores[recordedScores.length-1] = 0;
            if(this.gameCount == YahtzeeGame.GAMES){
                guiCard.showMessageDialog("Thank you for playing.\nYour grand total score is " + grandTotal);
                guiCard.setRoundCounterText("");
                guiCard.setGameCounterText("Done");
                gameCount++;
            } else {
                guiCard.showMessageDialog("Next game");
                guiCard.displayRecordedScores(score);
                setRound(0);
                incrementGameCount();
                setRerolls(0);
                grandTotal += score.getFinalScore();
                guiCard.setRollCounterText("Roll " + (rerolls + 1));
                guiCard.setRoundCounterText("Round " + (round + 1));
                guiCard.setGameCounterText("Game " + this.gameCount);
            }
        }
    }

    public static void main(String[] args){
        try{
            YahtzeeGame game = new YahtzeeGame();
            game.playRound();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}