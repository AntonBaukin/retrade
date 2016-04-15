package com.tverts.support;

/* standard Java classes */

import java.util.Date;

/* JUnit library */

import static org.junit.Assert.assertEquals;


/**
 * Test various dates routines. Note that
 * there is a leap year problem, and the
 * problem of time shifting in winter-summer.
 *
 * @author anton.baukin@gmail.com
 */
public class TestDates
{
	@org.junit.Test
	public void testAddDays()
	{
		//!: wrong days addition as of time shifting
		//assertEquals("30.10.2011", addDaysWrong("30.10.2011", +1));
		//~: but...
		assertEquals("30.10.2011", addDaysWrong("29.10.2011", +1));


		//~: correct variant...
		assertEquals(d("30.10.2011"), DU.addDays(d("29.10.2011"), +1));
		//~: ... and that...
		assertEquals(d("31.10.2011"), DU.addDays(d("30.10.2011"), +1));
	}

	@org.junit.Test
	public void testDiffDays()
	{
		//~: that time shifting...
		assertEquals(1, diffDays("31.10.2011", "30.10.2011"));

		//~: and other...
		assertEquals(2, diffDays("31.10.2011", "29.10.2011"));

		//~: and on month border...
		assertEquals(3, diffDays("01.11.2011", "29.10.2011"));

		//~: and on year border...
		assertEquals(65, diffDays("02.01.2012", "29.10.2011"));
	}



	/* private: date utilities */

	private static Date   d(String s)
	{
		return DU.str2date(s);
	}

	private static String d(Date d)
	{
		return DU.date2str(d);
	}

	private static String addDaysWrong(String d, int days)
	{
		return d(addDaysWrong(d(d), days));
	}

	private static Date addDaysWrong(Date d, int days)
	{
		return new Date(d.getTime() + 1000L * 60 * 60 * 24 *  days);
	}

	private static int    diffDays(String done, String dtwo)
	{
		return DU.diffDays(d(done), d(dtwo));
	}
}