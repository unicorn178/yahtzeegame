package classFinal;
/**
 * Runs all thirteen rounds of Yahtzee along with introduction and retry loop.
 * @author Owen Tang
 */

public class YahtzeeDriver {
    public static void main(String[] args){
        try{
            YahtzeeGame game = new YahtzeeGame();
            game.playRound();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
