package com.tverts.jsx;

/* Java */

import java.io.StringWriter;

/* JUnit */

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Tests {@link JsX} implementation.
 *
 * @author anton.baukin@gmail.com.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJsX
{
	@BeforeClass
	public static void prepareJsX()
	{
		JsX.INSTANCE.setRoots("com.tverts.jsx com.tverts.jsx.tests");
	}

	@Test
	public void test00HelloWorld()
	{
		StringWriter s = new StringWriter();
		final String T = String.format("%s%n", "Hello, World!");

		JsX.invoke("HelloWorld", "helloWorld", s);
		EX.assertx(CMP.eq(T, s.toString()));
	}

	@Test
	public void test01Checks()
	{
		JsX.invoke("TestZeT", "testChecks");
	}

	@Test
	public void test02Asserts()
	{
		JsX.invoke("TestZeT", "testAsserts");
	}

	@Test
	public void test03Arrays()
	{
		JsX.invoke("TestZeT", "testArrays");
	}

	@Test
	public void test04BasicsObject()
	{
		JsX.invoke("TestZeT", "testBasicsObject");
	}

	@Test
	public void test05BasicsFunction()
	{
		JsX.invoke("TestZeT", "testBasicsFunction");
	}

	@Test
	public void test06BasicsHelper()
	{
		JsX.invoke("TestZeT", "testBasicsHelper");
	}

	@Test
	public void test07Strings()
	{
		JsX.invoke("TestZeT", "testStrings");
	}

	@Test
	public void test08Classes()
	{
		JsX.invoke("TestZeT", "testClasses");
	}

	@Test
	public void test09Console()
	{
		StringWriter o = new StringWriter();
		StringWriter e = new StringWriter();

		JsX.invoke("TestZeT", "testConsole",
		  new JsStreams().output(o).error(e)
		);

		final String O = "This is 0-sample! Did you here 1,2,3?\n";
		EX.assertx(O.equals(o.toString()));

		final String E = "This is a sound of error...\n";
		EX.assertx(E.equals(e.toString()));
	}
}