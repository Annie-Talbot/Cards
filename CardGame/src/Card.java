/**
 * Card class that hold the integer value of the card.
 */
public class Card {
	/**
	 * The value of this card
	 */
	private int value;

	/**
	 * Constructor for the Card object, assigning it's value.
	 * @param value int: The integer value this Card will hold
	 */
	public Card(int value) {
		// Ensures a card cannot hold a negative value
		if (value > -1) {
			this.value = value;
		} else {
			System.err.print("Error: the value to be assigned to a card cannot be negative");
			this.value = 0;
		}
		
	}

	/**
	 * Getter for the value of the card
	 * @return int: the value
	 */
	public int getValue() {
		return value;
	}
}
