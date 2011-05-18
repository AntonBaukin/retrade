package com.tverts.genesis;

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;

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
	 * Returns the predicate that controls the execution
	 * of this generation unit. If it is not {@code null},
	 * it is invoked with the context instance of this
	 * genesis unit. The genesis is allowed if the predicate
	 * returns {@code true}.
	 */
	public Predicate getCondition();

	/**
	 * Actually generates the data. Returns the optional task
	 * that would be invoked as a cleanup task after all the
	 * genesis parts of the same {@link GenesisSphere} instance
	 * are called.
	 *
	 * The tasks are invoked in the opposite order to the genesis
	 * parts. If an {@link GenesisError} is invoked, it's cleanup
	 * task also added (to the top of the stack).
	 */
	public Runnable  generate()
	  throws GenesisError;

	/**
	 * Returns an operational copy of the original
	 * (prototype) genesis unit.
	 */
	public Genesis   clone();

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
	public boolean   isTransactional();

	/**
	 * Returns the name of this Genesis unit. The name
	 * may be not unique.
	 */
	public String    getName();

	/**
	 * Tells what this genesis actually generates.
	 */
	public String    getAbout(String lang);
}