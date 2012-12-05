package com.tverts.exec.service;

/**
 * When Execution Task fails because of the
 * database access error, the access session
 * may be corrupted, and it is not possible
 * to save resulting object (with error).
 *
 * Execution Services considers this problem.
 * When it catches this exception, it first
 * tries to save the result object in the
 * context of initial transaction, and if
 * this attempt fails, opens new connection
 * to the database and saves there.
 *
 * Exceptions of other types got by the
 * service would cause no result object to
 * be written, but the execution flag still set.
 * (The user would receive empty result.)
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ExecError extends RuntimeException
{
	/* public: constructors */

	public ExecError(String message)
	{
		super(message);
	}

	public ExecError(Throwable cause, String message)
	{
		super(message, cause);
	}

	public ExecError(Throwable cause)
	{
		super(cause);
	}


	/* public: ExecError interface */

	/**
	 * The object to save to the database.
	 */
	public Object    getResult()
	{
		return result;
	}

	public ExecError setResult(Object result)
	{
		this.result = result;
		return this;
	}

	/* private: resulting object */

	private Object result;
}
