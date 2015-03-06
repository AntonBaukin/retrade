package com.tverts.model.store;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelsStore;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Implementation base for Model Beans
 * storage algorithm.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelsStoreBase implements ModelsStore
{
	/* Models Store Base */

	public void setDelegate(Delegate delegate)
	{
		this.delegate = delegate;
	}

	protected Delegate delegate;


	/* Models Store */

	public ModelBean add(ModelBean bean)
	{
		EX.assertn(bean);

		//~: search the entry first
		ModelEntry e = doFind(EX.asserts(bean.getModelKey()));
		ModelBean  m = null;

		//?: {found it not} create & save
		if(e == null)
			doSave(doCreate(bean));
		//~: assign bean
		else
		{
			//~: assign bean
			m = e.bean; e.bean = bean;
		}

		return m;
	}

	public ModelBean remove(String key)
	{
		ModelEntry e = doFind(EX.asserts(key));
		if(e == null) return null;

		//~: remove the entry
		doRemove(e);

		//~: return the bean
		return EX.assertn(e.bean, "Model Store entry [", key, "] has no bean!");
	}

	public ModelBean read(String key)
	{
		ModelEntry e = doFind(EX.asserts(key));
		return (e == null)?(null):
		  EX.assertn(e.bean, "Model Store entry [", key, "] has no bean!");
	}


	/* protected: entries access */

	protected abstract ModelEntry find(String key);

	protected ModelEntry          doFind(String key)
	{
		ModelEntry e = find(key);

		//?: {found it not} see in the delegate
		if(e == null)
		{
			if(delegate == null)
				return null;

			//~: create empty entry with the key
			e = newEntry();
			e.key = key;

			//~: try to find
			delegate.find(e);

			//?: {found it not}
			if(e.bean == null)
				return null;
		}

		//~: update the access time
		e.accessTime = System.currentTimeMillis();

		//~: increment the counter
		e.accessInc.incrementAndGet();

		if(delegate != null)
			delegate.found(e);

		return e;
	}

	protected abstract void       save(ModelEntry e);

	protected void                doSave(ModelEntry e)
	{
		EX.asserts(e.key);
		EX.assertn(e.bean);

		//~: save locally
		save(e);

		//~: save in the delegate
		if(delegate != null)
			delegate.save(e);
	}

	protected ModelEntry          newEntry()
	{
		return new ModelEntry();
	}

	protected ModelEntry          doCreate(ModelBean mb)
	{
		ModelEntry e = newEntry();

		//=: model bean
		if(e.bean == null)
			e.bean = EX.assertn(mb);

		//=: model key
		e.key = EX.asserts(e.bean.getModelKey());

		//=: access time
		if(e.accessTime == 0L)
			e.accessTime = System.currentTimeMillis();

		if(delegate != null)
			delegate.create(e);

		return e;
	}

	protected abstract void       remove(ModelEntry e);

	protected void                doRemove(ModelEntry e)
	{
		//~: remove from the delegate
		if(delegate != null)
			delegate.remove(e);

		//~: remove locally
		remove(e);
	}
}