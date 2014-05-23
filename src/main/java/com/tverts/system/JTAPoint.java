package com.tverts.system;

/* Java */

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/* Java Naming + Transactions */

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Provides access to Java EE JTA environment
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

	public InitialContext  namingContext()
	{
		InitialContext result = initialContext.get();

		if(result == null) try
		{
			result = buildContext();
			initialContext.set(result);
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public InitialContext  buildContext()
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

	public UserTransaction tx()
	{
		try
		{
			//~: get the context
			InitialContext ctx = this.namingContext();

			//~: lookup
			return (UserTransaction) ctx.lookup("java:comp/UserTransaction");
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
		finally
		{
			this.clean();
		}
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


	/* thread + static variables */

	private ThreadLocal<InitialContext> initialContext =
	  new ThreadLocal<InitialContext>();

	private static Hashtable contextEnvironment;
}