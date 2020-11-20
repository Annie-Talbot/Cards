import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	@Test
	public void testGetPlayerID() {
		Player p = new Player(5, null, null);
		assertEquals(5, p.getPlayerID());
	}

}
