package com.tverts.system;

/* standard Java classes */

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/* Java Naming */

import javax.naming.InitialContext;


/**
 * Provides access to JavaEE-JTA environment
 * bound to the local thread.
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
			result = buildContext();
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


	/* protected: initial context */

	@SuppressWarnings("unchecked")
	protected InitialContext buildContext()
	  throws Exception
	{
		Hashtable env;

		//?: {has environment}
		synchronized(this.getClass())
		{
			if(contextEnvironment == null)
			{
				InputStream jp = Thread.currentThread().getContextClassLoader().
				  getResourceAsStream("jndi.properties");

				if(jp == null)
					contextEnvironment = new Hashtable();
				else try
				{
					Properties p = new Properties();
					p.load(jp);
					contextEnvironment = new Hashtable(p);
				}
				finally
				{
					jp.close();
				}
			}

			env = new Hashtable(contextEnvironment);
		}

		return new InitialContext(env);
	}

	private static Hashtable contextEnvironment;


	/* thread bound variables */

	private ThreadLocal<InitialContext> initialContext =
	  new ThreadLocal<InitialContext>();
}