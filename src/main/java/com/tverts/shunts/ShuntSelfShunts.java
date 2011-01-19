package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.fail;

/**
 * Shunts the self-shunting subsystem itself.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
@SelfShuntUnit
@SelfShuntDescr(en = "Shunts the self-shunting subsystem itself.")
public class ShuntSelfShunts
{
	/* public: shunts */

	@SelfShuntMethod(critical = false, order = 0,
	  descrEn = "here assertion failure is raised")
	public void testFailure()
	{
		fail("the generated failure");
	}

	@SelfShuntMethod(order = 1,
	  descrEn = "must be a success run")
	public void testSuccess()
	{}
}