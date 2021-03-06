package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.fail;


/**
 * Tests Shunt Subsystem: raises critical failure.
 * Include this test only when debugging the subsystem.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"system:shunts:critical-failure"})
@SelfShuntDescr("Tests Shunt Subsystem: raises critical failure.")
public class ShuntSelfCriticalFailure extends ShuntSelfSuccess
{
	/* shunt entries */

	@SelfShuntMethod(critical = true, descrEn =
	  "here critical failure is raised")
	public void testCriticalFailure()
	{
		fail("we generate here CRITICAL failure");
	}
}