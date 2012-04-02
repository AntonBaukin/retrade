package com.tverts.genesis;

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
	public void    generate()
	  throws GenesisError;

	/**
	 * Returns an operational copy of the original
	 * (prototype) genesis unit.
	 */
	public Genesis clone();

	/**
	 * Tells whether the genesis unit works in the
	 * context of a transaction.
	 *
	 * If unit is a part of a Genesis Sphere, all
	 * the transactional parts are invoked in the
	 * context of the same transaction.
	 *
	 * Note that this method must not depend on the
	 * generation state of the unit. (But may depend
	 * on constant parameters.) The method may be
	 * invoked on the original prototype instance.
	 */
	public boolean isTransactional();

	/**
	 * Returns the name of this Genesis unit. The name
	 * may be not unique.
	 */
	public String  getName();

	/**
	 * Tells what this genesis actually generates.
	 */
	public String  getAbout(String lang);
}