package com.tverts.genesis;

/* standard Java classes */

import java.util.List;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;


/**
 * Genesis defines an abstraction that is intended
 * to generate something in the database in a more
 * complex manner than just invoke {@link Runnable}.
 *
 * Genesis unit has an optional condition that defines
 * whether the instance may be actually invoked.
 *
 * Genesis unit may be a stateful object. It is allowed
 * to store invocation-dependent state in it.
 *
 * To bypass the possible concurrent problems the
 * prototype instance is cloned. Note that condition
 * check and the generation are on the same instance.
 *
 * @author anton.baukin@gmail.com
 */
public interface Genesis extends Cloneable
{
	/* public: Genesis interface */

	/**
	 * Generates the data.
	 */
	public void    generate(GenCtx ctx)
	  throws GenesisError;

	/**
	 * Returns an operational copy of the original
	 * (prototype) genesis unit.
	 */
	public Genesis clone();

	/**
	 * Returns the name of this Genesis unit. The name
	 * may be not unique. It is always defined.
	 */
	public String  getName();

	/**
	 * Tells what this genesis actually generates.
	 */
	public String  getAbout(String lang);

	/**
	 * Collects the parameters of this Genesis,
	 * and aggregated in composites.
	 *
	 * Note that parameters call on an original
	 * prototype Genesis will affect that prototype.
	 * It is common to call it for read-only purposes.
	 *
	 * Call this method on a clone before invoking it
	 * to assign (write) the parameters.
	 */
	public void    parameters(List<ObjectParam> params);
}