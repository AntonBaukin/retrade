package com.tverts.model.store;

/* com.tverts: system (services) */

import com.tverts.support.CMP;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: models */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelsStore;

/* com.tverts: support */

import com.tverts.support.EX;


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

	public void        service(Event event)
	{}


	/* Models Store Service (configuration) */

	public void setModelsStore(ModelsStoreBase modelsStore)
	{
		this.modelsStore = modelsStore;
	}

	private ModelsStoreBase modelsStore;

	public void setKeysGen(ModelKeysGen keysGen)
	{
		this.keysGen = keysGen;
	}

	private ModelKeysGen keysGen;


	/* protected: Delegate */

	protected void     delegateFind(ModelEntry e)
	{}

	protected void     delegateFound(ModelEntry e)
	{
		//?: {has no login saved}
		EX.assertn(e.login, "Model Bean [", e.key, "] has no login assigned!");

		//sec: {does this user own the model}
		if(!e.login.equals(SecPoint.login()))
			throw EX.forbid("You do not own the Model Bean [", e.key, "]!");
	}

	protected void     delegateRemove(ModelEntry e)
	{}

	protected void     delegateSave(ModelEntry e)
	{
		//sec: this domain
		EX.assertx(CMP.eq(e.domain, SecPoint.domain()));

		//sec: this login
		EX.assertx(CMP.eq(e.login, SecPoint.login()));
	}

	protected void     delegateCreate(ModelEntry e)
	{
		//sec: assign domain
		e.domain = SecPoint.domain();

		//sec: assign login
		e.login = SecPoint.login();
	}

	protected Delegate createDelegate()
	{
		return new Delegate()
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
		};
	}
}