package com.tverts.shunts;

/**
 * Marks test to be a self-shunts test. Such tests
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
 * @author anton baukin (abaukin@mail.ru)
 */
public interface SelfShunt extends Cloneable
{
	/* public: SelfShunt interface */

	/**
	 * Returns the name of the shunt. This name may be
	 * not a unique within the system, but is better to be.
	 *
	 * Basic implementation returns the simple name of the
	 * shunt unit class (with first letter lowercased).
	 *
	 * Note that this name has no direct relation with
	 * Spring Beans names.
	 */
	public String getShuntUnitName();

	public void   runShunt(SelfShuntUnitReport report);
}