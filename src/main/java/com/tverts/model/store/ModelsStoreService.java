package com.tverts.model.store;

/* Java */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/* com.tverts: system (spring) */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (services) */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.events.SystemReady;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: models */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelsStore;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Service that stores UI Model Beans applying
 * in-memory caching and persistence strategies.
 *
 * @author anton.baukin@gmail.com.
 */
public class      ModelsStoreService
       extends    ServiceBase
       implements ModelsStore, ModelsStoreAccess
{
	/* Models Store Service Singletone */

	public static final ModelsStoreService INSTANCE =
	  new ModelsStoreService();

	public static ModelsStoreService getInstance()
	{
		return INSTANCE;
	}

	protected ModelsStoreService()
	{
		modelsStore = new LinkedCacheModelsStore(1000);
		modelsStore.setDelegate(createDelegate());
	}


	/* Models Store */

	public ModelBean   add(ModelBean bean)
	{
		//?: {has no key} invoke the generator
		if(bean.getModelKey() == null)
		{
			EX.assertn(keysGen);
			bean.setModelKey(keysGen.genModelKey(bean));
		}

		return modelsStore.add(bean);
	}

	public ModelBean   remove(String key)
	{
		return modelsStore.remove(key);
	}

	public ModelBean   read(String key)
	{
		return modelsStore.read(key);
	}


	/* Models Store Access */

	public ModelsStore accessStore()
	{
		return this;
	}


	/* Service */

	public void service(Event event)
	{
		//?: {startup}
		if(event instanceof SystemReady)
			startupPlan();
		//?: {own event}
		else if((event instanceof ModelsStoreEvent) && mine(event))
			doExecute((ModelsStoreEvent) event);
	}

	public void destroy()
	{
		//~: save all the entities
		if(!saveShutdown())
			saveShutdown(); //<-- try second time
	}


	/* Models Store Service (configuration) */

	public void setModelsStore(ModelsStoreBase modelsStore)
	{
		EX.assertx(modelsStore instanceof CachingModelsStore);
		this.modelsStore = modelsStore;
		modelsStore.setDelegate(createDelegate());
	}

	protected ModelsStoreBase modelsStore;

	public void setKeysGen(ModelKeysGen keysGen)
	{
		this.keysGen = keysGen;
	}

	protected ModelKeysGen keysGen;

	public void setBackend(ModelsBackend backend)
	{
		this.backend = EX.assertn(backend);
	}

	protected ModelsBackend backend;

	/**
	 * Persistent store cleanup timeout
	 * of old-stored records in minutes.
	 * Defaults to 4 hours.
	 */
	public void setSweepTimeout(int sweepTimeout)
	{
		EX.assertx(sweepTimeout > 0);
		this.sweepTimeout = sweepTimeout;
	}

	protected int sweepTimeout = 10;

	/**
	 * Delay to start synchronization of
	 * Model Beans pruned from the cache.
	 * In milliseconds, defaults to 8 seconds.
	 */
	public void setSynchDelay(int synchDelay)
	{
		EX.assertx(synchDelay > 0);
		this.synchDelay = synchDelay;
	}

	protected int synchDelay = 1000 * 8;


	/* protected: events processing */

	protected void    startupPlan()
	{
		ModelsStoreEvent e = new ModelsStoreEvent();

		//~: order to sweep
		e.setSweep(true);

		//~: delay [1; 11) seconds
		delay(e, 1000L + System.currentTimeMillis() % 10000L);

		//~: sent to self
		self(e);

		LU.I(getLog(), logsig(), ": system is ready, planning periodical sweep task");
	}

	protected void    sweepDelay()
	{
		ModelsStoreEvent e = new ModelsStoreEvent();

		//~: order to sweep
		e.setSweep(true);

		//~: delay (in minutes ÷ 10)
		delay(e, 1000L * 60 / 10 * sweepTimeout);

		//~: sent to self
		self(e);
	}

	protected void    synchDelay()
	{
		ModelsStoreEvent e = new ModelsStoreEvent();

		//~: order to synchronize
		e.setSynch(true);

		//~: delay
		delay(e, synchDelay);

		//~: sent to self
		self(e);
	}

	protected void    synchAgain()
	{
		ModelsStoreEvent e = new ModelsStoreEvent();

		//~: order to synchronize
		e.setSynch(true);

		//~: minimal delay
		delay(e, 100L);

		//~: sent to self
		self(e);
	}

	protected void    doExecute(ModelsStoreEvent e)
	{
		//?: {sweep}
		if(e.isSweep())
			doSweep();

		//?: {synchronize}
		if(e.isSynch())
			doSynch();
	}

	protected void    doSynch()
	{
		//?: {was not able to lock}
		if(!synchLock.compareAndSet(false, true))
			return;

		try
		{
			Boolean x = savePruned();

			//?: {something is left to save}
			if(Boolean.FALSE.equals(x))
				synchAgain();
		}
		finally
		{
			synchLock.set(false);
		}
	}

	protected void    doSweep()
	{
		try
		{
			LU.I(getLog(), logsig(), ": executing sweep of UI models persisted");

			//~: do database sweep
			bean(GetModelRecord.class).sweep(1000L * 60 * sweepTimeout);
		}
		finally
		{
			//~: plan the next sweep
			sweepDelay();
		}
	}

	/**
	 * Saves the pruned instances to the backend
	 * and returns true if all them were actual
	 * during the operation. Returns null when
	 * no pruned entities were found.
	 */
	protected Boolean savePruned()
	{
		Map<ModelEntry, Integer> items =
		  new HashMap<ModelEntry, Integer>(101);

		//~: collect the items removed from the cache
		((CachingModelsStore) modelsStore).copyPruned(items);

		//?: {nothing to save}
		if(items.isEmpty())
			return null;

		//~: synchronize them
		backend.store(items.keySet());

		//~: commit the results
		((CachingModelsStore) modelsStore).commitSaved(items, false);

		//?: {has nothing left}
		return items.isEmpty();
	}

	/**
	 * Lock is true when service currently
	 * synchronizes with the backend.
	 */
	protected final AtomicBoolean synchLock =
	  new AtomicBoolean();

	/**
	 * Saves all the entities from the Models Store
	 * and returns true if nothing is left to save.
	 */
	protected boolean saveShutdown()
	{
		Map<ModelEntry, Integer> items =
		  new HashMap<ModelEntry, Integer>(1001);

		//~: collect all the items of the store
		((CachingModelsStore) modelsStore).copyAll(items);

		LU.I(getLog(), logsig(), ": there are [", items.size(),
		  "] model beans to synchronize");

		//?: {nothing to save}
		if(items.isEmpty())
			return true;

		//~: synchronize them
		backend.store(items.keySet());

		//~: commit the results
		((CachingModelsStore) modelsStore).commitSaved(items, true);

		//?: {has nothing left}
		return items.isEmpty();
	}


	/* protected: Delegate */

	protected void delegateFind(ModelEntry e)
	{
		if(backend != null)
			backend.find(e);
	}

	protected void delegateFound(ModelEntry e)
	{
		//?: {has no login saved}
		EX.assertn(e.login, "Model Bean [", e.key, "] has no login assigned!");

		//sec: {does this user own the model}
		if(!e.login.equals(SecPoint.login()))
			throw EX.forbid("You do not own the Model Bean [", e.key, "]!");
	}

	protected void delegateRemove(ModelEntry e)
	{
		if(backend != null)
			backend.remove(e);
	}

	protected void delegateSave(ModelEntry e)
	{
		//sec: this domain
		EX.assertx(CMP.eq(e.domain, SecPoint.domain()));

		//sec: this login
		EX.assertx(CMP.eq(e.login, SecPoint.login()));

		//HINT: in present implementation we do not save the beans
		//  into the database at the moment they are created.

		if(backend == null) return;

		//?: {bean is not active}
		if(!e.bean.isActive())
			backend.remove(e);
		//?: {it is just created}
		else if(e.accessInc.get() != 0)
			backend.store(Collections.singleton(e));
	}

	protected void delegateCreate(ModelEntry e)
	{
		//sec: assign domain
		e.domain = SecPoint.domain();

		//sec: assign login
		e.login = SecPoint.login();
	}

	protected void delegateOverflow(int size)
	{
		synchDelay();
	}

	protected CachingModelsStore.CachingDelegate createDelegate()
	{
		return new CachingModelsStore.CachingDelegate()
		{
			public void find(ModelEntry e)
			{
				delegateFind(e);
			}

			public void found(ModelEntry e)
			{
				delegateFound(e);
			}

			public void remove(ModelEntry e)
			{
				delegateRemove(e);
			}

			public void save(ModelEntry e)
			{
				delegateSave(e);
			}

			public void create(ModelEntry e)
			{
				delegateCreate(e);
			}

			public void overflow(int size)
			{
				delegateOverflow(size);
			}
		};
	}
}