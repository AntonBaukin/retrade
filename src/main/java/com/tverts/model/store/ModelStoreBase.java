package com.tverts.model.store;

/* standard Java classes */

import java.util.Date;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelBeanInfo;
import com.tverts.model.ModelKeysGen;
import com.tverts.model.ModelStore;

/* com.tverts: support */

import com.tverts.support.OU;
import static com.tverts.support.SU.s2s;


/**
 * TODO comment ModelStoreBase
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelStoreBase
       implements     ModelStore
{
	public static final long serialVersionUID = 0L;


	/* public: ModelStore (not Java Bean) interface */

	public ModelBean findBean(String key)
	{
		//~: check the key
		key = checkKey(key);

		//~: find the entry
		ModelBeanEntry e = findEntry(key);
		return (e == null)?(null):(e.getModelBean());
	}

	public ModelBean addBean(ModelBean bean)
	{
		//?: {the bean is not defined}
		if(bean == null) throw new IllegalArgumentException(
		  "Can't store undefined model bean!");

		//~: generate the bean model key
		assignKey(bean);

		//!: save the bean
		ModelBeanEntry e = saveEntry(bean);

		//?: {undefined result} illegal state
		if(e == null) throw new IllegalStateException(String.format(
		  "Model Store was unable to save instance '%s' due " +
		  "to the internal mapping error!", OU.cls(bean)));

		return e.getModelBean();
	}

	public ModelBean readBean(String key)
	{
		//~: check the key
		key = checkKey(key);

		//~: find the entry
		ModelBeanEntry e = findEntry(key);

		//?: {it does not exist} nothing to do
		if(e == null) return null;

		//!: update the entry timestamp
		e.updateReadTime();

		return e.getModelBean();
	}

	public Date      accessReadTime(String key)
	{
		//~: check the key
		key = checkKey(key);

		//~: find the entry
		ModelBeanEntry e = findEntry(key);
		return (e == null)?(null):(e.getReadTime());
	}


	/* public: ModelStoreBase (bean) interface */

	public ModelKeysGen getKeysGen()
	{
		return keysGen;
	}

	public void         setKeysGen(ModelKeysGen keysGen)
	{
		this.keysGen = keysGen;
	}


	/* public: bean entry */

	public static class ModelBeanEntry
	{
		/* public: constructors */

		public ModelBeanEntry()
		{}

		public ModelBeanEntry(ModelBean modelBean)
		{
			this.modelBean = modelBean;
			updateReadTime();
		}


		/* public: ModelBeanEntry (bean) interface */

		public ModelBean getModelBean()
		{
			return modelBean;
		}

		public void      setModelBean(ModelBean modelBean)
		{
			this.modelBean = modelBean;
		}

		public Date      getReadTime()
		{
			return readTime;
		}

		public void      setReadTime(Date readTime)
		{
			this.readTime = readTime;
		}

		public void      updateReadTime()
		{
			Date rt = this.getReadTime();

			//?: {the last update time is changed} update the timestamp
			if((rt == null) || (rt.getTime() != System.currentTimeMillis()))
				setReadTime(new Date());
		}


		/* private: entry attributes */

		private ModelBean modelBean;
		private Date      readTime;
	}


	/* protected: internal store interface */

	protected abstract ModelBeanEntry findEntry(String key);

	protected abstract ModelBeanEntry saveEntry(ModelBean bean);


	/* protected: internal store interface */

	protected ModelKeysGen keysGen()
	{
		ModelKeysGen gen = getKeysGen();

		//?: {keys generator is not defined} illegal state
		if(gen == null) throw new IllegalStateException(
		  "Model Store has no Keys Generation Strategy installed!");

		return gen;
	}

	protected String       checkKey(String key)
	{
		//?: {the key is empty}
		if((key = s2s(key)) == null) throw new IllegalArgumentException(
		  "Can't access Model Bean stored by an empty binding key!");

		return key;
	}

	protected void         assignKey(ModelBean bean)
	{
		ModelBeanInfo info = bean.getClass().
		  getAnnotation(ModelBeanInfo.class);

		//?: {the bean is unique}
		if((info != null) && info.unique())
		{
			String key = s2s(info.key());

			//?: {annotation has no key attribute} use simple name
			if(key == null)
				key = OU.getSimpleNameLowerFirst(bean.getClass());

			//!: do assign
			bean.setModelKey(key);
			return;
		}

		//?: {the bean already has the key} rely on it
		if(bean.getModelKey() != null)
		{
			checkKey(bean.getModelKey());
			return;
		}


		//<: generate unique key

		ModelKeysGen gen = getKeysGen();

		//?: {the generator does not present}
		if(gen == null) throw new IllegalStateException(
		  "Model Store has no model keys generator strategy specified!");

		//!: do generate
		String key = s2s(gen.genModelKey(bean));

		//?: {the key was not created}
		if(key == null) throw new IllegalStateException(String.format(
		  "Model Store was unable to create to create a key for model bean " +
		  "'%s' with generator '%s'", OU.cls(bean), OU.cls(gen)
		));

		//!: do assign
		bean.setModelKey(key);

		//>: generate unique key
	}


	/* private: keys generation strategy */

	private ModelKeysGen keysGen;
}