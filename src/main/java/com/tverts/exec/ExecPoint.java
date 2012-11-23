package com.tverts.exec;

/**
 * Point to invoke execution subsystem.
 *
 * @author anton.baukin@gmail.com
 */
public class ExecPoint
{
	/* ExecPoint Singleton */

	public static ExecPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ExecPoint INSTANCE =
	  new ExecPoint();

	protected ExecPoint()
	{}


	/* public: ExecPoint (singleton) interface */

	public static Object   execute(Object request)
	  throws ExecError
	{
		return getInstance().executor.execute(request);
	}

	public static Executor getExecutor(String name)
	{
		return getInstance().executor.getExecutor(name);
	}


	/* public: ExecPoint (instance) interface */

	public void setExecutor(RootExecutor executor)
	{
		if(executor == null) throw new IllegalArgumentException();
		this.executor = executor;
	}

	public void activate()
	{
		this.executor.registerExecutors();
	}


	/* private: root executor */

	private volatile RootExecutor executor =
	  new RootExecutor();
}