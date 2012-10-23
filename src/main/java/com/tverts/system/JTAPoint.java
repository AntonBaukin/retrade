package com.tverts.system;

/* Java Naming */

import javax.naming.InitialContext;


/**
 * Provides access to JavaEE-JTA environment
 * bound to the local thread.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class JTAPoint
{
	/* JTAPoint Singleton */

	public static JTAPoint getInstance()
	{
		return INSTANCE;
	}

	public static JTAPoint jta()
	{
		return INSTANCE;
	}

	private static final JTAPoint INSTANCE =
	  new JTAPoint();

	protected JTAPoint()
	{}


	/* public: JTAPoint interface */

	public InitialContext namingContext()
	{
		InitialContext result = initialContext.get();

		if(result == null) try
		{
			result = new InitialContext();
			initialContext.set(result);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		return result;
	}


	/* public: JTAPoint (system) interface */

	/**
	 * Cleans the thread-bound variables.
	 * They would be filled on demand.
	 *
	 * Invoke this method as the first action
	 * of the thread income, and as the last one
	 * before exiting.
	 *
	 * Multiple invocations are allowed.
	 */
	public void clean()
	{
		initialContext.remove();
	}

	/* thread bound variables */

	private ThreadLocal<InitialContext> initialContext =
	  new ThreadLocal<InitialContext>();
}