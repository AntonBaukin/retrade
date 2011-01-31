package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.assertEquals;

/**
 * Tests Shunt Subsystem: all the methods are successful.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
@SelfShuntUnit
@SelfShuntGroups({"system:shunts:success", "system:shunts:all"})
@SelfShuntDescr(en = "Tests Shunt Subsystem: all the methods are successful.")
public class ShuntSelfSuccess
{
	@SelfShuntMethod(
	  order = 0, critical = true, inherit = true,
	  descrEn = "1 = 1 success (first)")
	public void testFirstSuccess()
	{
		assertEquals(1, 1);
	}

	@SelfShuntMethod(
	  order = 1, critical = true, inherit = true,
	  descrEn = "2 = 1 + 1 success (second)")
	public void testSecondSuccess()
	{
		assertEquals(2, 1 + 1);
	}

	@SelfShuntMethod(
	  order = 2, critical = true, inherit = true,
	  descrEn = "2 = 5 / 2 success (third)")
	public void testThirdSuccess()
	{
		assertEquals(2, 5 / 2);
	}
}