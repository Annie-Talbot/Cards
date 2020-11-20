import java.util.LinkedList;

/**
 * CardDeck class. This represents a deck of cards between 2 players
 * and provides functionality for players to take and add cards.
 */
public class CardDeck {
	/**
	 * The current list of cards this deck is holding
	 */
	private LinkedList<Card> cards;
	/**
	 * The id of this deck
	 */
	private int deckID;

	/**
	 * Constructor for an instance of a CardDeck, initialising the list
	 * of cards and the ID assigned to the deck
	 * @param deckID int: The ID of this deck
	 */
	public CardDeck(int deckID) {
		cards = new LinkedList<Card>();
		this.deckID = deckID;
	}

	/**
	 * Appends a card to the end of the deck
	 * @param card Card: The card to be added
	 */
	public synchronized void addCard(Card card) {
		cards.addLast(card);
	}

	/**
	 * Removes and returns a card from the top of the deck
	 * @return Card: The card removed
	 */
	public synchronized Card takeCard() {
		return cards.removeFirst();
	}

	/**
	 * Getter for the ID of this deck instance
	 * @return int: the deckID
	 */
	public int getDeckID() {
		return deckID;
	}

	/**
	 * Turns the current contents of the deck into a string
	 * @return	String: holding this deck's ID and card list
	 */
	public String toString() {
		return "deck" + deckID + "contents: " + getDeck() + "\n";
	}

	/**
	 * Creates a string representing the value of every card in the deck
	 * @return String
	 */
	private String getDeck() {
		String txt = "";
		for (Card c : cards) {
			txt += c.getValue() + " ";
		}
		return txt;
	}
	
}
