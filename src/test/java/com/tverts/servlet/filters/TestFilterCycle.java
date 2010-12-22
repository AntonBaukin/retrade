package com.tverts.servlet.filters;

/* JUnit library */

import static org.junit.Assert.assertEquals;

public class TestFilterCycle
{
	/* test entries */

	@org.junit.Test
	public void testNoFilters()
	{
		assertEquals("<~>", test());
	}

	@org.junit.Test
	public void testOneFilter()
	{
		final String T = "[A<~>A]";

		assertEquals(T, test(

		  filter("A")

		));
	}

	@org.junit.Test
	public void testPlainA()
	{
		final String T = "[A[B<~>B]A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B")

		));
	}

	@org.junit.Test
	public void testPlainB()
	{
		final String T = "[A[B[C[D<~>D]C]B]A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B"),
		  filter("C"),
		  filter("D")

		));
	}

	@org.junit.Test
	public void testReqursiveA()
	{
		final String T = "[A{B[C[D<~>D]C]B}A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B").setReqursive(true),
		  filter("C"),
		  filter("D")

		));
	}

	@org.junit.Test
	public void testReqursiveB()
	{
		final String T = "[A[B[C{D<~>D}C]B]A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B"),
		  filter("C"),
		  filter("D").setReqursive(true)

		));
	}

	@org.junit.Test
	public void testReqursiveC()
	{
		final String T = "[A{B[C[D{E<~>E}D]C]B}A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B").setReqursive(true),
		  filter("C"),
		  filter("D"),
		  filter("E").setReqursive(true)

		));
	}

	@org.junit.Test
	public void testReqursiveD()
	{
		final String T = "[A{B[C[D{E[F<~>F]E}D]C]B}A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B").setReqursive(true),
		  filter("C"),
		  filter("D"),
		  filter("E").setReqursive(true),
		  filter("F")

		));
	}

	@org.junit.Test
	public void testReqursiveE()
	{
		final String T = "{A{B{C{D{E{F<~>F}E}D}C}B}A}";

		assertEquals(T, test(

		  filter("A").setReqursive(true),
		  filter("B").setReqursive(true),
		  filter("C").setReqursive(true),
		  filter("D").setReqursive(true),
		  filter("E").setReqursive(true),
		  filter("F").setReqursive(true)

		));
	}

	@org.junit.Test
	public void testBreakingA()
	{
		final String T = "[A[B!B]A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B").setBreaking(true),
		  filter("C"),
		  filter("D")

		));
	}

	@org.junit.Test
	public void testBreakingB()
	{
		final String T = "[A{B[C[D{E!E}D]C]B}A]";

		assertEquals(T, test(

		  filter("A"),
		  filter("B").setReqursive(true),
		  filter("C"),
		  filter("D"),
		  filter("E").setReqursive(true).setBreaking(true),
		  filter("F")

		));
	}

	@org.junit.Test
	public void testBreakingC()
	{
		final String T = "{A{B{C{D{E{F!F}E}D}C}B}A}";

		assertEquals(T, test(

		  filter("A").setReqursive(true),
		  filter("B").setReqursive(true),
		  filter("C").setReqursive(true),
		  filter("D").setReqursive(true),
		  filter("E").setReqursive(true),
		  filter("F").setReqursive(true).setBreaking(true)

		));
	}

	@org.junit.Test
	public void testBreakingD()
	{
		final String T = "{A{B!B}A}";

		assertEquals(T, test(

		  filter("A").setReqursive(true),
		  filter("B").setReqursive(true).setBreaking(true),
		  filter("C").setReqursive(true),
		  filter("D").setReqursive(true),
		  filter("E").setReqursive(true),
		  filter("F").setReqursive(true)

		));
	}

	/* test state */

	private TestFilterTask task    = new TestFilterTask();
	private StringBuilder  testure = new StringBuilder();

	/* support filters */

	private String     test(Filter... filters)
	{
		testure.delete(0, testure.length());
		task.test(filters);
		return testure.toString();
	}

	private TestFilter filter(String name)
	{
		return (new TestFilter()).
		  setName(name);
	}

	private class TestFilter implements Filter
	{
		/* public: Filter interface */

		public void openFilter(FilterTask task)
		{
			if(reqursive)
				testure.append('{').append(name);
			else
				testure.append('[').append(name);

			if(breaking)
			{
				testure.append('!');
				task.setBreaked();
				return;
			}

			if(reqursive)
				task.continueCycle();
		}

		public void closeFilter(FilterTask task)
		{
			if(reqursive)
				testure.append(name).append('}');
			else
				testure.append(name).append(']');
		}

		/* public: TestFilter interface */

		public TestFilter setName(String name)
		{
			this.name = name;
			return this;
		}

		public TestFilter setBreaking(boolean breaking)
		{
			this.breaking = breaking;
			return this;
		}

		public TestFilter setReqursive(boolean reqursive)
		{
			this.reqursive = reqursive;
			return this;
		}

		/* public: filter parameters */

		public String  name;
		public boolean breaking;
		public boolean reqursive;
	}

	private class TerminalFilter implements Filter
	{
		/* public: Filter interface */

		public void openFilter(FilterTask task)
		{
			testure.append("<~");
		}

		public void closeFilter(FilterTask task)
		{
			testure.append('>');
		}
	}

	private class TestFilterTask extends FilterTaskBase
	{
		/* public: constructor */

		public TestFilterTask()
		{
			super(null);
		}

		/* public: TestFilterTask interface */

		public TestFilterTask test(Filter... filters)
		{
			filterCycle = new FilterCycle(this, filters);
			filterCycle.setTerminal(new TerminalFilter());
			continueCycle();
 			return this;
		}

		/* public: FilterTask interface */

		public void continueCycle()
		{
			filterCycle.continueCycle();
		}

		/* private: the cycle */

		private FilterCycle filterCycle;
	}
}