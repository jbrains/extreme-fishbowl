package org.agile2010.war;

import static org.junit.Assert.*;
import org.junit.Test;

public class CardTest {
	
	@Test
	public void test3IsGreaterThan2()
	{
		assertEquals(true, Card.Three.beats(Card.Two));
	}

	@Test
	public void testQIsNotGreaterThanK()
	{
		assertEquals(false, Card.Queen.beats(Card.King));
	}

}
