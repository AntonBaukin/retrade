package com.tverts.model.store;

/* com.tverts: system (services) */

import com.tverts.model.store.ModelsStoreBase.Delegate;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: models, model stores */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelsStore;
import com.tverts.model.store.ModelsStoreBase.ModelEntry;

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
		modelsStore = new SimpleModelStore();
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

	protected ModelEntry delegateFind(String key)
	{
		return null;
	}

	protected ModelEntry delegateFound(ModelEntry e)
	{
		//?: {has no login saved}
		EX.assertn(e.login, "Model Bean [", e.key, "] has no login assigned!");

		//sec: {does this user own the model}
		if(!e.login.equals(SecPoint.login()))
			throw EX.forbid("You do not own the Model Bean [", e.key, "]!");

		return e;
	}

	protected void       delegateRemove(ModelEntry e)
	{}

	protected ModelEntry delegateSave(ModelEntry e)
	{
		//~: mark as not new
		EX.assertx(e.isnew);
		e.isnew = false;

		//sec: assign domain
		EX.assertx(e.domain == null);
		e.domain = SecPoint.domain();

		//sec: assign login
		EX.assertx(e.login == null);
		e.login = SecPoint.login();

		return e;
	}

	protected Delegate   createDelegate()
	{
		return new Delegate()
		{
			public ModelEntry find(String key)
			{
				return delegateFind(key);
			}

			public ModelEntry found(ModelEntry e)
			{
				return delegateFound(e);
			}

			public void       remove(ModelEntry e)
			{
				delegateRemove(e);
			}

			public ModelEntry save(ModelEntry e)
			{
				return delegateSave(e);
			}
		};
	}
}