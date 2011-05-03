package com.tverts.genesis;

/* standard Java classes */

/* com.tverts: system services */

import com.tverts.system.services.QueueExecutorServiceBase;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LO;
import com.tverts.support.LU;

/**
 * Genesis Service allows to run {@link GenesisSphere}
 * instances as a {@link Runnable} tasks placed in the
 * execution queue {@link QueueExecutorServiceBase}.
 *
 * @author anton.baukin@gmail.com
 */
public class   GenesisService
       extends QueueExecutorServiceBase
{
	/* public: GenesisService interface */

	public void    enqueueSphere(GenesisSphere sphere)
	{
		((DequeProvider)getTasksProvider()).
		  appendTask(createGenesisTask(sphere));
	}

	/**
	 * {@link GenesisSpheres} installed into the service creates
	 * the initial {@link GenesisSphere} instances to add to the
	 * service's queue before it starts. These instances would
	 * be run further.
	 */
	public GenesisSpheres
	               getGenesisSpheres()
	{
		return genesisSpheres;
	}

	public void    setGenesisSpheres(GenesisSpheres spheres)
	{
		this.genesisSpheres = spheres;
	}

	/* public: ServiceBase interface */

	public boolean isActiveService()
	{
		return GenesisPoint.getInstance().isActive();
	}

	/* protected: genesis sphere tasks execution */

	protected Runnable createGenesisTask(GenesisSphere sphere)
	{
		return new GenesisTaskWrapper(sphere);
	}

	protected void     handleGenesisError(Throwable e)
	{
		logSphereError(EX.xrt(e));
	}

	/* protected: Genesis Task Wrapper */

	protected class GenesisTaskWrapper implements Runnable
	{
		/* public: constructor */

		public GenesisTaskWrapper(GenesisSphere sphereTask)
		{
			if(sphereTask == null)
				throw new IllegalArgumentException();

			this.sphereTask = sphereTask;
		}

		/* public: Runnable interface */

		public void run()
		{
			try
			{
				this.sphereTask.run();
			}
			catch(Throwable e)
			{
				handleGenesisError(e);
			}
		}

		/* protected: the task */

		protected final GenesisSphere sphereTask;
	}

	/* protected: StatefulServiceBase (state control) */

	/**
	 * Adds the initial protocols to the queue.
	 */
	protected void   afterInitService()
	{
		super.afterInitService();

		GenesisSpheres spheres = getGenesisSpheres();
		if(spheres == null) return;

		for(GenesisSphere sphere : spheres.createGenesisSpheres())
			enqueueSphere(sphere);
	}

	/* protected: logging */

	protected String getLog()
	{
		return GenesisPoint.LOG_SERVICE;
	}

	protected String logsig(String lang)
	{
		String one = LO.LANG_RU.equals(lang)?
		  ("Сервис Генеизса"):("Genesis Service");

		String two = getServiceName();
		if(two == null) two = "???";

		return String.format("%s '%s'", one, two);
	}

	protected void   logSphereError(Throwable e)
	{
		LU.E(getLog(), e, logsig(),
		  "ERROR ocurred in Genesis Sphere Unit! ",
		  "The service continues it's work...");
	}

	/* private: default genesis references */

	private GenesisSpheres genesisSpheres;
}