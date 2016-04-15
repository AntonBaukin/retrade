package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.fail;


/**
 * Tests Shunt Subsystem: has not critical failure.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"system:shunts:failure"})
@SelfShuntDescr("Tests Shunt Subsystem: has not critical failure.")
public class ShuntSelfFailure extends ShuntSelfSuccess
{
	/* shunt entries */

	@SelfShuntMethod(critical = false, descrEn =
	  "here assertion failure is raised")
	public void testFailure()
	{
		fail("the generated failure, but not critical");
	}
}