package com.tverts.exec;

/**
 * Executor is a strategy to process income
 * (request) object into arbitrary result
 * object (depending on the request).
 *
 * Executor supports a number of request
 * classes. It may also decide whether
 * to execute the request depending on
 * the object state.
 *
 * If executor receives unknown request
 * class, it must skip it without error.
 *
 * Executor must be thread-safe (fully
 * reentable).
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Executor
{
	/* public: Executor interface */

	/**
	 * Returns the configured name of the
	 * Executor unique within the system.
	 */
	public String getName();

	/**
	 * Executes the request object if it is known.
	 * Must return a defined object as the result.
	 * Undefined results means the request is
	 * not supported by this executor.
	 */
	public Object execute(Object request)
	  throws ExecError;
}