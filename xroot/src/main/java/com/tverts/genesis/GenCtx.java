package com.tverts.genesis;

/* standard Java classes */

import java.util.Random;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Session;


/**
 * The context of generation call.
 *
 * @author anton.baukin@gmail.com
 */
public interface GenCtx
{
	/* public: GenCtx interface */

	/**
	 * Returns the Genesis unit that had created
	 * this context instance.
	 */
	public Genesis getOwner();

	public GenCtx  getOuter();

	/**
	 * The random numbers generator that may
	 * be applied by the generator.
	 */
	public Random  gen();

	/**
	 * Duplicates this context replacing
	 * the outer context with {@code this}
	 * and resetting the owner.
	 *
	 * Note that the new context shares
	 * the same parameters set and the
	 * generator {@link #gen()}. This
	 * allows to pass data between the
	 * Genesis Spheres.
	 */
	public GenCtx  stack(Genesis owner);

	/**
	 * Returns the names of all the parameters.
	 */
	public Set     params();

	/**
	 * Returns the names of the context state
	 * parameters exported to the Genesis clients
	 * (via events subsystem).
	 *
	 * Note that the key and the value of the
	 * parameter must be a serializable Java Beans
	 * or standard types, such as Strings and Classes.
	 */
	public Set     exported();

	/**
	 * Returns the context parameter.
	 */
	public Object  get(Object p);

	/**
	 * Returns the parameter checking it's type.
	 * The result is also checked to be defined.
	 */
	public <T> T   get(Object p, Class<T> cls);

	/**
	 * Returns instance of the class given.
	 */
	public <T> T   get(Class<T> cls);

	/**
	 * Rewrites the context parameter with
	 * the new value returning the old one.
	 */
	public Object  set(Object p, Object v);

	/**
	 * Sets the object using it's class as the
	 * key parameter. Returns old instance.
	 */
	public <T> T   set(T obj);

	/**
	 * Exports the context parameter key.
	 */
	public void    export(Object key);

	/**
	 * Returns the default logging destination
	 * that generator may use.
	 *
	 * Note that the result is always defined.
	 */
	public String  log();

	public Session session();
}