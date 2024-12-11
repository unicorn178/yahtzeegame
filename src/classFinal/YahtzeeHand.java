package classFinal;
/**
 * Used to deal with the rolling of each round. Resets after each round.
 * @author Owen Tang
 */
import java.util.Scanner;

public class YahtzeeHand {
    private Die[] dice = new Die[YahtzeeGame.DICES];
    /*
     * Returns array of integers with current values of the dice
     */
    public YahtzeeHand(){
        for(int i = 0; i < dice.length; i++){
            dice[i] = new Die();
        }
    }

    public Die[] getDice(){
        return dice;
    }

    /* Roll all dice */
    public void rollAll() {
        for(Die d: dice){
            d.roll();
        }
    }

    /* Rolls specific dice */
    public void roll(int number) {
        dice[number].roll();
    }
    public void changeHand(boolean[] choices){
        for(int i = 0; i < choices.length; i++){
            if(choices[i]){
                roll(i);
            }
        }
    }

    /* Returns value of specified dice */
    public Integer get(int number) {
        return dice[number].getValue();
    }
}
