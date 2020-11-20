import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * CardGame class that controls the creation of the game and management of its
 * players and decks.
 */
public class CardGame extends Thread {
    /**
     * The players participating in the game
     */
    private Player[] players;
    /**
     * The decks holding the remaining cards after the players have their cards
     */
    private CardDeck[] decks;
    /**
     * The manager for all file read and write operations
     */
    private final FileSystemInterface fs;
    /**
     * The number of players waiting to for the round to end, so that they can
     * select another card.
     */
    private int waitingPlayer = 0;
    /**
     * The ID of the winning player
     */
    private int winner = -1;
    /**
     * The number of players left still in the game
     */
    private int playerLeft = 0;

    /**
     * Constructor for a CardGame object. The Player and Deck objects are created and pack
     * of cards input handed to each of them in a round robin fashion.
     * @param fs    FileSystemInterface: the file system for writing to file during the game
     * @param noPlayers int: The number of players participating in the game
     * @param pack  LinkedList<Card>: The pack of cards to hand out to the players and decks
     */
    public CardGame(FileSystemInterface fs, int noPlayers, LinkedList<Card> pack) {
        // Set up game folder for player and deck outputs
        // Initialise players and decks
        this.fs = fs;
        this.players = new Player[noPlayers];
        this.decks = new CardDeck[noPlayers];

        for (int i = 0; i < noPlayers; i++) {
            this.players[i] = new Player(i + 1, this, fs);
            this.decks[i] = new CardDeck(i + 1);
        }

        // Deal out player hands
        for (int i = 0; i < 4; i++) {
            for (Player p : players) {
                p.addCard(pack.removeFirst(), i);
            }
        }

        // Assign decks to players
        for (int i = 0; i < noPlayers; i++) {
            int afterDeck = (i == noPlayers - 1) ? 0 : i + 1;
            players[i].assignDecks(decks[i], decks[afterDeck]);
        }

        // Deal out decks
        int counter = 0;
        while (!pack.isEmpty()) {
            decks[counter%noPlayers].addCard(pack.removeFirst());
            counter++;
        }
    }

    /**
     * Increments the counter for number of players in the game when a player leaves.
     */
    public void playerLeaving() {
        playerLeft++;
    }

    /**
     * Checks if the game is finished (all players have finished)
     * @return Bool: True if the game is finished
     */
    public boolean isFinished() {
        return playerLeft == players.length;
    }

    /**
     * Creates the path specifying the directory for all file write save operations in this
     * game using the local date and time.
     * @return  String: The directory path to save all files into.
     */
    public static String generateOutputPath() {
        return "assets/CardGame_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yy__HH_mm"));
    }

    /**
     * Starts all the Player threads to set the game off.
     */
    public void startGame() {
        for (Player p : players) {
            p.start();
        }
    }

    /**
     * When a player finishes their turn (a draw and a discard) this checks if the player
     * has all matching cards and declares the winner (and game over) if they do and
     * stops the player if another player has won.
     * @param player int: ID of the player finishing their turn
     * @param playerHand    Card[]: The hand of the player in question
     */
    public synchronized void finishTurn(int player, Card[] playerHand) {
        waitingPlayer++;
        boolean winningHand = true;
        if (winner > 0) {
            // if there is already a winner you can not win.
            winningHand = false;
        }
        if (winningHand) {
        	// tests if all this player's cards match
            int firstCard = playerHand[0].getValue();
            for (int i = 1; i < playerHand.length; i++) {
                if (playerHand[i].getValue() != firstCard) {
                    winningHand = false;
                    break;
                }
            }
        }
        if (winningHand) {
            winner = player;
        }
        if (waitingPlayer == players.length) {
            waitingPlayer = 0;
            // if we have a winner at the end of the round, we declare the winner.
            if (winner > 0) {
                for (Player p : players) {
                    p.finish(winner);
                }
                for (CardDeck d : decks) {
                    fs.writeToFile("/deck" + d.getDeckID() + "_output.txt", d.toString());
                }
                players[winner-1].writeToFile("player " + winner + " wins");
                System.out.println("player " + winner + " wins.");
            }
            this.notifyAll();
        } else {
        	// forces the all players to wait until every player has played a turn
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Getter for the ID of the winning player
     * @return  int: the ID of the winning player
     */
    public int getWinner() {
        return winner;
    }

    /**
     * Turns an input file into a valid pack of cards, or throws the necessary error for
     * the problem with the pack.
     * @param filename  String: path of the pack file
     * @param noCards   int: the number of cards that should be in the pack
     * @return  LinkedList<Card>: The pack of cards
     * @throws IOException If the filename given is invalid
     * @throws IncorrectNumberOfCardsException If the pack has too many or too few cards
     * @throws NumberFormatException    If the pack contains invalid characters (anything
     * that is not an integer)
     */
    private static LinkedList<Card> validatePack(String filename, int noCards) throws IOException,
            IncorrectNumberOfCardsException, NumberFormatException {
        LinkedList<Card> pack = new LinkedList<Card>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String line = reader.readLine();
        while (line != null) {
            pack.addLast(new Card(Integer.parseInt(line)));
            line = reader.readLine();
        }
        reader.close();

        if (pack.size() != noCards) {
            throw new IncorrectNumberOfCardsException();
        }
        return pack;
    }

    /**
     * Takes the user inputs for number of players and pack file location, then
     * creates the game of cards.
     * @param args
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the number of players");
        int noPlayers = Integer.parseInt(input.nextLine());

        LinkedList<Card> pack = null;
        System.out.println("Please enter the filename of the pack");
        while (pack == null) {
            String packfilename = input.nextLine();
            try {
                pack = CardGame.validatePack(packfilename, noPlayers * 8);
            } catch (FileNotFoundException e) {
                System.out.print("That pack file is invalid.\nPlease enter the filename for a valid pack");
                pack = null;
            } catch (IncorrectNumberOfCardsException e) {
                System.out.print("That pack has an incorrect number of cards for the "
                        + "number of players enterred.\nPlease enter the filename for a" 
                		+ " valid pack");
                pack = null;
            } catch (NumberFormatException e) {
                System.out.print("That pack file is in an incorrect format.\nPlease enter a valid pack");
                pack = null;
            } catch (IOException e) {
                e.printStackTrace();
                pack = null;
            }
        }
        input.close();

        CardGame cg = new CardGame(
                new FileSystemInterface.DefaultImplementation(CardGame.generateOutputPath()),
                noPlayers,
                pack);

        cg.startGame();
    }
}
