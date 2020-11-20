
public class IncorrectNumberOfCardsException extends Exception {
	public IncorrectNumberOfCardsException () {
		super("The number of cards in this pack does not match"
				+ "the number of players given.");
	}
}
