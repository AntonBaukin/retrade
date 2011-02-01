package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.fail;

/**
 * Tests Shunt Subsystem: raises critical failure.
 * Inlcude this test only when debugging the subsystem.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
@SelfShuntUnit
@SelfShuntGroups({"system:shunts:killme"})
@SelfShuntDescr(en = "Tests Shunt Subsystem: raises critical failure.")
public class ShuntSelfCriticalFailure extends ShuntSelfSuccess
{
	/* shunt entries */

	@SelfShuntMethod(critical = true,
	  descrEn = "here critical failure is raised")
	public void testCriticalFailure()
	{
		fail("we generate here CRITICAL failure");
	}
}