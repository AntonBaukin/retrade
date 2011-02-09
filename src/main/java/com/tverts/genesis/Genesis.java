package com.tverts.genesis;

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;

/**
 * Genesis defines an abstraction that is intended
 * to generate something in the database in a more
 * complex manner than just invoke {@link Runnable}.
 *
 * Genesis unit must be stateless. It is forbidden
 * to store in the instance the data related to the
 * generation process being no the parameters.
 *
 * Genesis unit has a condition that defines whether
 * the unit would be actually invoked.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface Genesis
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
	 * genesis modules of the system are invoked. The undefined
	 * result means no cleanup task.
	 *
	 * For the details see {@link GenesisSphere} class.
	 */
	public Runnable  generate()
	  throws GenesisError;

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