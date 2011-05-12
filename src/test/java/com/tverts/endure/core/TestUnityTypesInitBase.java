package com.tverts.endure.core;

/* JUnit library */

import static org.junit.Assert.assertEquals;

/**
 * Tests unity types list parsing implemented in
 * {@link UnityTypesInitBase}.
 *
 * @author anton.baukin@gmail.com
 */
public class TestUnityTypesInitBase
{
	/* test entries */

	@org.junit.Test
	public void testNoEntries()
	{
		UnityTypesInitTest test; String TSTR, RSTR;

		test = new UnityTypesInitTest();
		TSTR = "";
		RSTR = "";

		assertEquals(0,    test.parseWhole(TSTR).size());
		assertEquals(RSTR, test.texture.toString());
	}

	@org.junit.Test
	public void testSimpleName()
	{
		UnityTypesInitTest test; String TSTR, RSTR;

		test = new UnityTypesInitTest();
		TSTR = "UnityLink JustPlainType";
		RSTR = "?<UnityLink>'JustPlainType'";

		assertEquals(1,    test.parseWhole(TSTR).size());
		assertEquals(RSTR, test.texture.toString());
	}

	@org.junit.Test
	public void testSimpleTypedName()
	{
		UnityTypesInitTest test; String TSTR, RSTR;

		test = new UnityTypesInitTest();
		TSTR = "E UnityLink  Just Plain Type";
		RSTR = "E<UnityLink>'Just Plain Type'";

		assertEquals(1,    test.parseWhole(TSTR).size());
		assertEquals(RSTR, test.texture.toString());

		test = new UnityTypesInitTest();
		TSTR = "X  JustPlainType";
		RSTR = "?<X>'JustPlainType'";

		assertEquals(1,    test.parseWhole(TSTR).size());
		assertEquals(RSTR, test.texture.toString());
	}

	@org.junit.Test
	public void testFullName()
	{
		UnityTypesInitTest test; String TSTR, RSTR;

		test = new UnityTypesInitTest();
		TSTR = "com.tverts.endure.core.UnityLink  Just Plain Type";
		RSTR = "?[com.tverts.endure.core.UnityLink]<UnityLink>'Just Plain Type'";

		assertEquals(1,    test.parseWhole(TSTR).size());
		assertEquals(RSTR, test.texture.toString());
	}

	@org.junit.Test
	public void testTypedName()
	{
		UnityTypesInitTest test; String TSTR, RSTR;

		test = new UnityTypesInitTest();
		TSTR = "E com.tverts.endure.core.UnityLink  Just Plain Type";
		RSTR = "E[com.tverts.endure.core.UnityLink]<UnityLink>'Just Plain Type'";

		assertEquals(1,    test.parseWhole(TSTR).size());
		assertEquals(RSTR, test.texture.toString());
	}

	@org.junit.Test
	public void testCombined()
	{
		UnityTypesInitTest test; String TSTR, RSTR;

		test = new UnityTypesInitTest();

		TSTR = "E  com.tverts.endure.core.Entity  Just: Plain: Type ; " +
		       "L UnityLink Advanced: Type; " +
		       "A java.lang.Integer Integer: Attribute";

		RSTR = "E[com.tverts.endure.core.Entity]<Entity>'Just: Plain: Type'\n" +
		       "L<UnityLink>'Advanced: Type'\n" +
		       "A[java.lang.Integer]<Integer>'Integer: Attribute'";

		assertEquals(3,    test.parseWhole(TSTR).size());
		assertEquals(RSTR, test.texture.toString());
	}


	/* private: test implementation */

	private static class UnityTypesInitTest extends UnityTypesInitBase
	{
		protected void handleEntry(ParseEntry pe)
		{
			//!: don't call super

			if(texture.length() != 0)
				texture.append('\n');

			//type flag
			if(pe.typeFlag == null)
				texture.append('?');
			else
				texture.append(pe.typeFlag);

			//?: {full class name}
			if(pe.className != null)
				texture.append('[').append(pe.className).append(']');

			//?: {simple class name}
			if(pe.simpleName != null)
				texture.append('<').append(pe.simpleName).append('>');

			//?: {type name}
			if(pe.typeName != null)
				texture.append('\'').append(pe.typeName).append('\'');
		}

		public StringBuilder texture = new StringBuilder(256);
	}
}