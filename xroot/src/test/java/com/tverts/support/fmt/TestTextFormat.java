package com.tverts.support.fmt;

/* JUnit library */

import static org.junit.Assert.assertEquals;


/**
 * Tests {@link TextFormat}.
 *
 * @author anton.baukin@gmail.com
 */
public class TestTextFormat
{
	@org.junit.Test
	public void  testTextFormat()
	{
		test("A is B", "$0 is $1", "A", "B");

		test("$A is B", "$A is $0", "B");

		test("got $A, but needed B!",
		  "got $$0, but needed $1!", "A", "B");
	}

	private void test(String r, String f, Object... ps)
	{
		assertEquals(r, new TextFormat(f).format((Object[])ps));
	}
}