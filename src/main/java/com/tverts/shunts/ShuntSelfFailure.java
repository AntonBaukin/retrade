package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.fail;

/**
 * Tests Shunt Subsystem: has not critical failure.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"system:shunts:success"})
@SelfShuntDescr(en = "Tests Shunt Subsystem: has not critical failure.")
public class ShuntSelfFailure extends ShuntSelfSuccess
{
	/* shunt entries */

	@SelfShuntMethod(descrEn = "here assertion failure is raised")
	public void testFailure()
	{
		fail("the generated failure, but not critical");
	}
}