import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {
	@Test
	public void testCard() {
		Card c = new Card(5);
		assertEquals(5, c.getValue());
		
		c = new Card(-1);
		assertEquals(0, c.getValue());
	}

}
