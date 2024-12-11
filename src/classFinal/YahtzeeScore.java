package classFinal;

/**
 * Used for calculating all the scores of YahtzeeGame.
 * @author Owen Tang
 */
public class YahtzeeScore {

    private int[] dice;
    private int sumOfDice;
    private boolean[] isScoreFilled; // 0-5 = upper section, 6 = chance, 7 = 3s, 8 = 4s, 9 = full house, 10 = small straight, 11 = full straight, 12 = yahtzee
    private Integer[] recordedScores; // 13 = bonus yahtzee
    private int upperSectionTotal;
    private int lowerSectionTotal;
    private Integer[] roundScores;

    // public YahtzeeScore(Die[] input) {
    public YahtzeeScore(Die[] input) {
        isScoreFilled = new boolean[YahtzeeGame.ROUNDS];
        recordedScores = new Integer[YahtzeeGame.ROUNDS + 1];
        recordedScores[YahtzeeGame.ROUNDS] = 0;
        roundScores = new Integer[YahtzeeGame.ROUNDS + 1];
        recordedScores[YahtzeeGame.ROUNDS] = 0; // bonus yahtzee score is force-set to 0
        setDice(input);
    }

    public Integer[] getRecordedScores() {
        return recordedScores;
    }

    public boolean[] getIsScoreFilled() {
        return isScoreFilled;
    }
    public int getUpperSectionTotal() {
        return upperSectionTotal;
    }
    public int getLowerSectionTotal() {
        return lowerSectionTotal;
    }

    public int getFinalScore() {
        return upperSectionTotal + lowerSectionTotal + scoreUpperSectionBonus();
    }
    public void calculateSectionTotals(){
        upperSectionTotal = 0;
        lowerSectionTotal = 0;
        for(int i = 0; i < 6; i++){
            upperSectionTotal += (recordedScores[i] == null ? 0 : recordedScores[i]);
        }
        for(int i = 6; i < recordedScores.length; i++){
            lowerSectionTotal += (recordedScores[i] == null ? 0 : recordedScores[i]);
        }
    }
    public void resetScores(){
        for (int i = 0; i < isScoreFilled.length; i++){
            recordedScores[i] = null;
            isScoreFilled[i] = false;
        }
        recordedScores[YahtzeeGame.ROUNDS] = 0;
    }

    // Used to set or reset dice according to the game's hand.
    public void setDice(Die[] input){
        dice = new int[YahtzeeGame.DICES];
        for (int i = 0; i < dice.length; i++) {
            dice[i] = input[i].getValue();
        }
        // sort the sorted dice array
        selectionSort();
        sumOfDice = 0;
        for (int i : dice) {
            sumOfDice += i;
        }
    }
    public Integer[] getRoundScores(){
        return roundScores;
    }

    public void setRoundScores(){
        int[] upperSection = getUpperScores();
        roundScores = new Integer[]{
                upperSection[0], upperSection[1], upperSection[2], upperSection[3], upperSection[4], upperSection[5],
                scoreChance(), scoreThreeOfAKind(), scoreFourOfAKind(), scoreFullHouse(), scoreSmallStraight(),
                scoreFullStraight(), scoreYahtzee(), scoreBonusYahtzee()};
    }

    public void recordScore(int record) {
        // fill in the player's score
        isScoreFilled[record] = true;
        if (0 <= record && record <= 5) { // upper section
            recordedScores[record] = getUpperScores()[record];
        } else { // lower section
            switch (record) {
                case 6:
                    recordedScores[6] = scoreChance();
                    break;
                case 7:
                    recordedScores[7] = scoreThreeOfAKind();
                    break;
                case 8:
                    recordedScores[8] = scoreFourOfAKind();
                    break;
                case 9:
                    recordedScores[9] = scoreFullHouse();
                    break;
                case 10:
                    recordedScores[10] = scoreSmallStraight();
                    break;
                case 11:
                    recordedScores[11] = scoreFullStraight();
                    break;
                case 12:
                    recordedScores[12] = scoreYahtzee();
                    break;
            }
        }
    }

    // Calculates the scores for the upper section.
    public int[] getUpperScores() {
        int[] returnMe = new int[6];
        for(int i: dice){
            returnMe[i-1] += i;
        }
        return returnMe;
    }
    public int scoreUpperSectionBonus() {
        int upperSectionTotal = 0;
        for(int i = 0; i < 6; i++){
            if(recordedScores[i] != null){
                upperSectionTotal += recordedScores[i];
            }
        }
        if(upperSectionTotal >= 63){
            return 35;
        }
        return 0;
    }
    // Calculates the 3-of-a-kind score.
    public int scoreThreeOfAKind() {
        if(maxDupes() >= 3){
            return sumOfDice;
        }
        return 0;
    }
    // Calculates the 4-of-a-kind score.
    public int scoreFourOfAKind(){
        if(maxDupes() >= 4) {
            return sumOfDice;
        }
        return 0;
    }
    // Calculates the full house score.
    public int scoreFullHouse() {
        if((maxDupes() == 3 && uniqueValues() == 2) || (maxDupes() == 5 && isScoreFilled[12])){ // implies there must be 3 of one number and 2 of another
            return 25;
        }
        return 0;
    }
    // Calculates the small straight score.
    public int scoreSmallStraight() {
        // If you get a bonus Yahtzee, you can record it as a full house or straight.
        if(maxStraight() >= 4 || (maxDupes() == 5 && isScoreFilled[12])){
            return 30;
        }
        return 0;
    }
    // Calculates the full straight score.
    public int scoreFullStraight() {
        if(maxStraight() == 5 || (maxDupes() == 5 && isScoreFilled[12])){
            return 40;
        }
        return 0;
    }

    // Calculates the chance score (really just sum of the dice regardless).
    public int scoreChance() {
        return sumOfDice;
    }
    // Calculates the Yahtzee score.
    public int scoreYahtzee() {
        if(maxDupes() == 5) {
            return 50;
        }
        return 0;
    }
    // Calculates the bonus Yahtzee score.
    public int scoreBonusYahtzee() {
        if(maxDupes() == 5 && recordedScores[12] != null && recordedScores[12] == 50){
            // not a truly recorded score, the 100 points go straight into the box without anything else
            recordedScores[13] += 100;
            return 100;
        }
        return 0;
    }
    // Finds the number of duplicates in the hand. Used to check 3-of-a-kind, 4-of-a-kind, full house, and Yahtzee.
    public int maxDupes(){
        int dupeCount = 1;
        int maxDupeCount = 1;
        for(int i = 1; i < 5; i++){
            if(dice[i] == dice[i-1]){
                dupeCount++;
            } else {
                if(dupeCount > maxDupeCount){
                    maxDupeCount = dupeCount;
                }
                dupeCount = 1;
            }
        }
        if(dupeCount > maxDupeCount){
            maxDupeCount = dupeCount;
        }
        return maxDupeCount;
    }
    // Finds the number of unique values. Used to check full house.
    public int uniqueValues(){
        int returnMe = 1;
        for(int i = 1; i < YahtzeeGame.DICES; i++){
            if(dice[i] != dice[i-1]){
                returnMe++;
            }
        }
        return returnMe;
    }
    // Finds the length of the longest straight of the hand. Used to check small and full straights.
    public int maxStraight(){
        int currentCount = 1;
        int maxCount = 1;
        for(int i = 1; i < YahtzeeGame.DICES; i++){
            if(dice[i] == dice[i-1] + 1){
                currentCount++;
            } else if (dice[i] != dice[i-1]) {
                if(currentCount > maxCount){
                    maxCount = currentCount;
                }
                currentCount = 1;
            }
        }
        if(currentCount > maxCount){
            maxCount = currentCount;
        }
        return maxCount;
    }
    // selection sort to sort the list
    public void selectionSort(){
        for (int i = 0; i < dice.length; i++) {
            int currentMin = dice[i];
            int currentMinIndex = i;
            for (int j = i+1; j< dice.length; j++) {
                if (currentMin > dice[j]) {
                    currentMin = dice[j];
                    currentMinIndex = j;
                }
            }
            if (currentMinIndex != i) {
                dice[currentMinIndex] = dice[i];
                dice[i] = currentMin;
            }
        }
    }
}