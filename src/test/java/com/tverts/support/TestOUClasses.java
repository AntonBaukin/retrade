package com.tverts.support;

/* JUnit library */

import static org.junit.Assert.assertEquals;

/**
 * Tests 'classes and interfaces' section of
 * object utilities collection {@link OU}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class TestOUClasses
{
	/* test entries */

	@org.junit.Test
	public void testAllInterfacesA()
	{
		final String S = "";

		tai(S, Ca.class);
	}

	@org.junit.Test
	public void testAllInterfacesB()
	{
		final String S = "Ia Ib";

		tai(S, Cb.class);
	}

	@org.junit.Test
	public void testAllInterfacesC()
	{
		final String S = "Id Ia Ic Ib";

		tai(S, Cc.class);
	}

	@org.junit.Test
	public void testAllInterfacesD()
	{
		final String S = "Ia";

		tai(S, Cd.class);
	}

	@org.junit.Test
	public void testSelectClassOrInterfaceA()
	{
		final String S = "Ca";

		tsci(S, Ca.class);
	}

	@org.junit.Test
	public void testSelectClassOrInterfaceD()
	{
		final String S = "Cd Ia Cc Id Ia Ic Ib";

		tsci(S, Cd.class);
	}

	@org.junit.Test
	public void testSelectClassOrInterfaceE()
	{
		final String S = "Ce Ie Id Ic Ib Ia Cd Ia Cc Id Ia Ic Ib";

		tsci(S, Ce.class);
	}

	/* fixture for */

	private static interface Ia
	{}

	private static interface Ib
	{}

	private static interface Ic extends Ia, Ib
	{}

	private static interface Id extends Ic, Ib
	{}

	private static interface Ie extends Id
	{}

	private static class Ca
	{}

	private static class Cb implements Ia, Ib
	{}

	private static class Cc implements Id, Ia
	{}

	private static class Cd extends Cc implements Ia
	{}

	private static class Ce extends Cd implements Ie
	{}

	/* test support */

	private void   tai(String s, Class c)
	{
		assertEquals(s, c2s(OU.getAllInterfaces(c)));
	}

	private void   tsci(String s, Class c)
	{
		Tracer tracer = new Tracer();
		Class  selres = OU.selectClassOrInterface(c, tracer);

		assertEquals(Object.class, selres);
		assertEquals(s, tracer.getTrace());
	}

	private String c2s(Class[] classes)
	{
		StringBuilder sb = new StringBuilder(16);

		for(Class c : classes) sb.
		  append((sb.length() != 0)?(" "):("")).
		  append(c.getSimpleName());

		return sb.toString();
	}

	private static class Tracer
	        implements   OU.ClassPredicate
	{
		/* public: ClassPredicate interface */

		public boolean isThatClass(Class c)
		{
			if(Object.class.equals(c))
				return true;

			if(trace.length() != 0)
				trace.append(' ');
			trace.append(c.getSimpleName());
			return false;
		}

		/* public: Tracer interface */

		public String  getTrace()
		{
			return trace.toString();
		}

		/* private: the trace */

		private StringBuilder trace =
		  new StringBuilder(16);
	}
}