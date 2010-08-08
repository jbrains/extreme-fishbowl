package org.agilealliance.agile2010.learning.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class ArraysAsListTest {
	@Test
	public void withIntegerObjects() throws Exception {
		assertEquals(2, Arrays.asList(new Integer[] { 2 }).get(0).intValue());
	}

	@Test
	public void withPrimitiveInts() throws Exception {
		// Definitely not the behavior I expected!
		// I expected the first element to be 2 because of autoboxing
		assertArrayEquals(new int[] { 2 }, Arrays.asList(new int[] { 2 })
				.get(0));
	}
	
}
