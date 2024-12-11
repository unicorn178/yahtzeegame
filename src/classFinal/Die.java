package classFinal;

public class Die {
    private int value;
    /* Roll a die and return its numerical value */
    public int roll() {
        value = (int) (Math.random() * 6) + 1;
        return value;
    }

    /* Return numerical value of the die without re-rolling it */
    public int getValue() {
        return value;
    }
}
