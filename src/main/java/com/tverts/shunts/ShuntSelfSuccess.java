package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.assertEquals;


/**
 * Tests Shunt Subsystem: all the methods are successful.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit(single = true)
@SelfShuntGroups({"system", "system:shunts", "system:shunts:success"})
@SelfShuntDescr("Tests Shunt Subsystem: all the methods are successful.")
public class ShuntSelfSuccess
{
	@SelfShuntMethod(
	  order = 0, critical = true, inherit = true,
	  descrEn = "first success")
	public void testFirstSuccess()
	{
		assertEquals(++i, 1);
	}

	@SelfShuntMethod(
	  order = 1, critical = true, inherit = true,
	  descrEn = "second success")
	public void testSecondSuccess()
	{
		assertEquals(++i, 2);
	}

	@SelfShuntMethod(
	  order = 2, critical = true, inherit = true,
	  descrEn = "third success")
	public void testThirdSuccess()
	{
		assertEquals(++i, 3);
	}

	private int i;
}