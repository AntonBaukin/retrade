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
}