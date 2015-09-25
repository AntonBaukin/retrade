package com.tverts.jsx;

/* Java */

import java.io.StringWriter;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


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
		StringWriter s = new StringWriter();
		final String T = "Hello, World!\n";

		JsX.invoke("HelloWorld", "helloWorld", s);
		EX.assertx(CMP.eq(T, s.toString()));
	}
}