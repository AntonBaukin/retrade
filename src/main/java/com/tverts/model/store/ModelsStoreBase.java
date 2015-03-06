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
			doSave(doInit(bean));
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
		ModelEntry e = this.find(key);

		//?: {found it not} see in the delegate
		if(e == null)
			e = (delegate == null)?(null):(delegate.find(key));

		//?: {alas, found it not}
		if(e == null) return null;

		//~: update the access time
		e.accessTime = System.currentTimeMillis();

		//~: increment the counter
		e.accessInc.incrementAndGet();

		return (delegate == null)?(e):(delegate.found(e));
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

	protected ModelEntry          doInit(ModelBean mb)
	{
		ModelEntry e = doCreate(mb);

		//=: model bean
		if(e.bean == null)
			e.bean = EX.assertn(mb);

		//=: model key
		e.key = EX.asserts(e.bean.getModelKey());

		//=: access time
		if(e.accessTime == 0L)
			e.accessTime = System.currentTimeMillis();

		return e;
	}

	protected ModelEntry          doCreate(ModelBean mb)
	{
		return (delegate != null)?(delegate.create(mb)):(new ModelEntry());
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