
import java.util.LinkedList;

/**
 * Player class that inherits the Thread class. This represents a player
 * in the game and runs through drawing and discarding cards until the game
 * is finished.
 */
public class Player extends Thread {
    /**
     * Controls the write to file operations for the player
     */
    private FileSystemInterface fs;
    /**
     * The ID of the player
     */
    private int playerID;
    /**
     * The game this player belongs to
     */
    private CardGame game;
    /**
     * The hand of cards this player currently has
     */
    private Card[] hand = new Card[4];
    /**
     * The indices of the player's hand that hold cards, this player does not
     * want
     */
    private LinkedList<Integer> incorrectCards;
    /**
     * The deck this player will draw from
     */
    private CardDeck beforeDeck;
    /**
     * The deck with player will discard to
     */
    private CardDeck afterDeck;
    /**
     * Whether this player has finished the game
     */
    private boolean exit = false;
    /**
     * The path of this player's output file
     */
    private String writePath;

    /**
     * Constructor for the player that assigns its ID and connects it to
     * the game.
     * @param playerID int: ID of this player
     * @param game  CardGame: The game this player belongs to
     * @param fs    FileSystemInterface: The write to file controller
     */
    public Player(int playerID, CardGame game, FileSystemInterface fs) {
        this.playerID = playerID;
        this.game = game;
        this.fs = fs;
        this.writePath = "/player" + playerID + "_output.txt";
    }

    /**
     * Adds a card to the players hand at the specified index
     * @param card Card: card to be added
     * @param index int: Index in hand to put this card
     */
    protected void addCard(Card card, int index) {
        hand[index] = card;
    }

    /**
     * Getter for the player ID
     * @return int: the playerID
     */
    public int getPlayerID() {
        return playerID;
    }


    /**
     * Assigns the decks placed before and after this player so that they can
     * draw and discard
     * @param beforeDeck CardDeck
     * @param afterDeck CardDeck
     */
    public void assignDecks(CardDeck beforeDeck, CardDeck afterDeck) {
        this.beforeDeck = beforeDeck;
        this.afterDeck = afterDeck;
    }

    /**
     * Method called when the game has ended
     * @param winnerID int: The playerID of the winning player
     */
    public void finish(int winnerID) {
        if (playerID != winnerID) {
            writeToFile("player " + winnerID + " has informed player " + playerID +
                    " that they have won");
        }
        exit = true;
    }

    @Override
    public void run() {
        writeToFile("initial hand " + getHand());

        // Creates a list containing all the Card indices that do not match the playerID
        incorrectCards = new LinkedList<Integer>();
        for (int i = 0; i < 4; i++) {
            if (hand[i].getValue() != playerID) {
                incorrectCards.addLast(i);
            }
        }
        // round "0", check the initial cards.
        game.finishTurn(playerID, hand);
        while (!exit) {
            exchangeCards();
            // check if all the cards match or game has ended
            game.finishTurn(playerID, hand);
        }
        writeToFile("player " + playerID + " exiting game\nfinal hand: " +
                getHand());
        game.playerLeaving();
    }

    /**
     * Getter for the integer value of each card in this player's hand -
     * separated by a whitespace character.
     * @return the hand in string form e.g. "1 2 3 4"
     */
    private String getHand() {
        return hand[0].getValue() + " " + hand[1].getValue() +
                " " + hand[2].getValue() + " " + hand[3].getValue();
    }

    /**
     * Writes the given string to this player's output file
     * @param text the text to be written
     */
    public void writeToFile(String text) {
        fs.writeToFile(writePath, text + "\n");
    }
    
    /**
     * To be used in a turn, this completes a discard and a draw operation
     * for this player.
     */
    private void exchangeCards() {
    	// make space in hand
        int newHandPosition = incorrectCards.removeFirst();
        afterDeck.addCard(hand[newHandPosition]);
        writeToFile("discards a " + hand[newHandPosition].getValue() +
                " to deck " + afterDeck.getDeckID());

        // take card
        hand[newHandPosition] = beforeDeck.takeCard();
        writeToFile("draws a " + hand[newHandPosition].getValue() +
                " from deck " + beforeDeck.getDeckID() + "\nhand is now " + getHand());

        // check if card matches set
        if (hand[newHandPosition].getValue() != playerID) {
            incorrectCards.addLast(newHandPosition);
        }
    }
}
