import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

public class CardGameTest {
	int timeout = 10000;
	CardGame initialWinGame;
	CardGame normalGame;
	LinkedList<Card> INITIAL_WIN_PACK;
	LinkedList<Card> NORMAL_GAME_PACK;
	final int DEFAULT_TEST_PLAYER_NUMBER = 2;
	MockFileSystem fs = new MockFileSystem();
	
	@Before
	public void setup() {
		// Initial win pack ensures player 1 always wins on the 0th round(without even playing a round)
	    INITIAL_WIN_PACK = new LinkedList<Card>();
	    for (int i = 0; i < 16; i++) {
	        if (i % 2 == 0) {
	            INITIAL_WIN_PACK.addLast(new Card(1));
	        }else {
	            INITIAL_WIN_PACK.addLast(new Card(i));
	        }
	    }
	    initialWinGame = new CardGame(fs, DEFAULT_TEST_PLAYER_NUMBER, INITIAL_WIN_PACK);
	    
	    NORMAL_GAME_PACK = new LinkedList<Card>();
	    // normal pack ensures player 1 always wins on the 3rd round
	    for (int i = 0; i < 8; i++) {
	    	if (i % 2 == 0) {
	            NORMAL_GAME_PACK.addLast(new Card(1));
	            NORMAL_GAME_PACK.addLast(new Card(1));
	        }else {
	            NORMAL_GAME_PACK.addLast(new Card(2));
	            NORMAL_GAME_PACK.addLast(new Card(2));
	        }
	    }
	    normalGame = new CardGame(fs, DEFAULT_TEST_PLAYER_NUMBER, NORMAL_GAME_PACK);

	}
	
	@After
	public void tearDown() {
		fs = null;
		timeout = 1000;
	}
	
	@Test
	public void testInitialWinCase() {
	    initialWinGame.startGame();
	    while(timeout > 0 && !initialWinGame.isFinished()) {
	        try {
	            Thread.sleep(10);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        timeout -= 10;
	    }
	    assertEquals(fs.fakeFS.keySet().size(), 4);
	    assertEquals(fs.fakeFS.get("/player1_output.txt"),
	        "initial hand 1 1 1 1\n" +
	        "player 1 wins\n" + 
	        "player 1 exiting game\n" +
	        "final hand: 1 1 1 1\n");
		assertEquals(fs.fakeFS.get("/player2_output.txt"),
		        "initial hand 1 3 5 7\n" +
		        "player 1 has informed player 2 that they have won\n" +
		        "player 2 exiting game\n" +
		        "final hand: 1 3 5 7\n");
		assertEquals(fs.fakeFS.get("/deck1_output.txt"), "deck1contents: 1 1 1 1 \n");
		assertEquals(fs.fakeFS.get("/deck2_output.txt"), "deck2contents: 9 11 13 15 \n");
	}
	
	@Test
	public void testNormalGameCase() {
		normalGame.startGame();
	    while(timeout > 0 && !normalGame.isFinished()) {
	        try {
	            Thread.sleep(10);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        timeout -= 10;
	    }
	    assertEquals(fs.fakeFS.keySet().size(), 4);
	    assertEquals(fs.fakeFS.get("/player1_output.txt"),
	        "initial hand 1 2 1 2\n" +
	        "discards a 2 to deck 2\n" +
	        "draws a 1 from deck 1\n" +
	        "hand is now 1 1 1 2\n" + 
	        "discards a 2 to deck 2\n" +
	        "draws a 2 from deck 1\n" +
	        "hand is now 1 1 1 2\n" + 
	        "discards a 2 to deck 2\n" +
	        "draws a 1 from deck 1\n" + 
	        "hand is now 1 1 1 1\n" +
	        "player 1 wins\n" +
	        "player 1 exiting game\n" +
	        "final hand: 1 1 1 1\n");
		assertEquals(fs.fakeFS.get("/player2_output.txt"),
				"initial hand 1 2 1 2\n" +
		        "discards a 1 to deck 1\n" +
		        "draws a 1 from deck 2\n" +
		        "hand is now 1 2 1 2\n" + 
		        "discards a 1 to deck 1\n" +
		        "draws a 2 from deck 2\n" +
		        "hand is now 1 2 2 2\n" + 
		        "discards a 1 to deck 1\n" +
		        "draws a 1 from deck 2\n" + 
		        "hand is now 1 2 2 2\n" +
		        "player 1 has informed player 2 that they have won\n" +
		        "player 2 exiting game\n" +
		        "final hand: 1 2 2 2\n");
		assertEquals(fs.fakeFS.get("/deck1_output.txt"), "deck1contents: 2 1 1 1 \n");
		assertEquals(fs.fakeFS.get("/deck2_output.txt"), "deck2contents: 2 2 2 2 \n");
	}
	
	class MockFileSystem implements FileSystemInterface {
		// fakseFS represents an in-memory file system. It is a simple map keyed by the file
		// path and has the file content as the value for the path.
		final HashMap<String, String> fakeFS = new HashMap<String, String>();
		
		public MockFileSystem() {
		}
		
		public void writeToFile(String path, String content) {
		    String existingContent = "";
	        if (fakeFS.containsKey(path)) {
	            existingContent = fakeFS.get(path);
	        }
	        content = existingContent + content;
	        fakeFS.put(path,content);
		}
    }
}


