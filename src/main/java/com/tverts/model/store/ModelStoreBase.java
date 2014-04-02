package com.tverts.model.store;

/* Java */

import java.io.Serializable;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelBeanInfo;
import com.tverts.model.ModelKeysGen;
import com.tverts.model.ModelStore;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.OU;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Implementation base for Model Beans
 * storage. Helps to handle beans,
 * but misses the storage, activation,
 * passivation, or garbage collection.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelStoreBase
       implements     ModelStore
{
	public static final long serialVersionUID = 0L;


	/* public: ModelStore (not Java Bean) interface */

	public ModelBean addBean(ModelBean bean)
	{
		//?: {the bean is not defined}
		EX.assertn(bean, "Can't store undefined model bean!");

		//~: generate the bean model key
		assignKey(bean);

		//!: save the bean
		ModelBeanEntry e = saveEntry(bean);

		//?: {undefined result}
		EX.assertn(e, "Model Store was unable to save instance '",
		  LU.cls(bean), "' due to the internal mapping error!"
		);

		//sec: auth login is required
		e.setLogin(SecPoint.login());

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

		//sec: check the login
		if(!SecPoint.login().equals(e.getLogin()))
			throw EX.forbid("Model Bean [", key, "] was not created by this user!");

		//!: update the entry timestamp
		e.updateReadTime();

		return e.getModelBean();
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

	public static class ModelBeanEntry implements Serializable
	{
		public static final long serialVersionUID = 0L;


		/* public: constructors */

		public ModelBeanEntry()
		{}

		public ModelBeanEntry(ModelBean modelBean)
		{
			this.modelBean = modelBean;
			this.updateReadTime();
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

		public void      updateReadTime()
		{
			this.readTime = System.currentTimeMillis();
		}

		/**
		 * Primary key of Auth Login of the user
		 * had created this model. Used for security.
		 */
		public Long      getLogin()
		{
			return login;
		}

		public void      setLogin(Long login)
		{
			this.login = login;
		}


		/* private: entry attributes */

		private ModelBean modelBean;
		private long      readTime;
		private Long      login;
	}


	/* protected: internal store interface */

	protected abstract ModelBeanEntry findEntry(String key);

	protected abstract ModelBeanEntry saveEntry(ModelBean bean);


	/* protected: internal store interface */

	protected ModelKeysGen keysGen()
	{
		return EX.assertn( getKeysGen(),
		  "Model Store has no Keys Generation Strategy installed!"
		);
	}

	protected String       checkKey(String key)
	{
		//?: {the key is empty}
		return EX.assertn(SU.s2s(key),
		  "Can't access Model Bean stored by an empty binding key!"
		);
	}

	protected void         assignKey(ModelBean bean)
	{
		ModelBeanInfo info = bean.getClass().
		  getAnnotation(ModelBeanInfo.class);

		//?: {the bean is unique}
		if((info != null) && info.unique())
		{
			String key = SU.s2s(info.key());

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

		//?: {the generator does not present}
		ModelKeysGen gen = EX.assertn( getKeysGen(),
		  "Model Store has no model keys generator strategy specified!"
		);

		//?: {the key was not created}
		String key = EX.assertn( SU.s2s(gen.genModelKey(bean)),
		  "Model Store was unable to create to create a key for model bean '",
		  LU.cls(bean), "' with generator '", LU.cls(gen), "'!"
		);

		//~: do assign
		bean.setModelKey(key);

		//>: generate unique key
	}


	/* private: keys generation strategy */

	private ModelKeysGen keysGen;
}