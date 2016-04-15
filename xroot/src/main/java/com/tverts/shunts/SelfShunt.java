package com.tverts.shunts;

/**
 * Marks test to be a Self-Shunt test. Such tests
 * are run on the system activation after all
 * subsystems and services are activated.
 *
 * The shunt requests go through all HTTP handling phases
 * and are serviced by {@code ShuntServlet}. HTTP session
 * is also available. It is the same for all shunt units.
 *
 * Note that the instances of this class are not intended
 * to be thread-safe. They are not reused. The are cloned
 * before invoking. If the class is a Serializable, deep
 * clone via serialization would be done.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SelfShunt extends Cloneable
{
	/* public: SelfShunt interface */

	/**
	 * Returns the name of the shunt. It may be not unique
	 * within the system, and Shunt System would assign
	 * the unique ones, but some mess is possible when
	 * configuring shunts with parameters.
	 *
	 * Basic implementation returns the simple name of the
	 * shunt unit class (with first letter lower-cased).
	 *
	 * Note that this name has no direct relation with
	 * Spring Beans names.
	 */
	public String    getName();

	/**
	 * Names the shunt groups this unit belongs to.
	 * The order of the groups has no effect.
	 * The names are case sensitive.
	 */
	public String[]  getGroups();

	public SelfShunt clone();

	/**
	 * Invoked on the cloned instance of the unit.
	 */
	public void      shunt(SelfShuntCtx ctx);
}