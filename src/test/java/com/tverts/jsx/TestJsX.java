package com.tverts.jsx;

/**
 * Tests {@link JsX} implementation.
 *
 * @author anton.baukin@gmail.com.
 */
public class TestJsX
{
	@org.junit.BeforeClass
	public static void prepareJsX()
	{
		JsX.INSTANCE.setRoots("com.tverts.jsx com.tverts.jsx.tests");
	}

	@org.junit.Test
	public void testHelloWorld()
	{

	}
}