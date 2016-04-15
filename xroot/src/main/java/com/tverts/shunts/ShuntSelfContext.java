package com.tverts.shunts;

/* JUnit */

import static org.junit.Assert.assertNotNull;


/**
 * Tests Shunt Subsystem and the {@link SelfShuntCtx}
 * context binding.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"system", "system:shunts", "system:shunts:context"})
@SelfShuntDescr("Tests Shunt Subsystem: " +
  "tests SelfShuntCtx and Domain binding.")
public class ShuntSelfContext extends ShuntPlain
{
	@SelfShuntMethod(order = 0, critical = true, descrEn =
	  "tests SelfShuntCtx is bound")
	public void testContextBound()
	{
		assertNotNull("SelfShuntCtx is not bound!",
		  SelfShuntPoint.getInstance().context());

		assertNotNull(ctx());
	}

	@SelfShuntMethod(order = 1, critical = false, descrEn =
	  "Domain to shunt was given")
	public void testDomainGiven()
	{
		assertNotNull("Domain to shunt is not given! ",
		  ctx().getDomain() );

		try
		{
			domain();
		}
		catch(Throwable e)
		{
			throw new AssertionError("Domain to shunt not found!", e);
		}
 	}
}