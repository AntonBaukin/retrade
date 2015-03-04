package com.tverts.model.store;

/* Java */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelsStore;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;
import com.tverts.support.LU;


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
		ModelEntry e = find(EX.asserts(bean.getModelKey()));
		ModelBean  m = null;

		//?: {found it not} create & save
		if(e == null)
			save(create(bean));
		//~: just update
		else
		{
			//~: assign bean
			m = e.bean; e.bean = bean;

			//~: update the access time
			e.accessTime = System.currentTimeMillis();

			//~: not a new
			e.isnew = false;
		}

		return m;
	}

	public ModelBean remove(String key)
	{
		ModelEntry e = find(EX.asserts(key));
		if(e == null) return null;

		//~: remove the entry
		remove(e);

		//~: return the bean
		return EX.assertn(e.bean, "Model Store entry [", key, "] has no bean!");
	}

	public ModelBean read(String key)
	{
		ModelEntry e = find(EX.asserts(key));
		if(e == null) return null;

		//~: update the access time
		e.accessTime = System.currentTimeMillis();

		return EX.assertn(e.bean, "Model Store entry [", key, "] has no bean!");
	}


	/* protected: entries access */

	protected abstract ModelEntry find(String key);

	protected abstract void       remove(ModelEntry e);

	protected abstract void       save(ModelEntry e);

	protected ModelEntry          create(ModelBean mb)
	{
		ModelEntry e = new ModelEntry();

		e.bean       = EX.assertn(mb);
		e.key        = EX.asserts(mb.getModelKey());
		e.accessTime = System.currentTimeMillis();
		e.isnew      = true;

		return e;
	}


	/* Delegate */

	/**
	 * Interface to additionally wrap the own strategies.
	 */
	public static interface Delegate
	{
		/* Models Store Delegate */

		public ModelEntry find(String key);

		public ModelEntry found(ModelEntry e);

		public void       remove(ModelEntry e);

		public ModelEntry save(ModelEntry e);

		public ModelEntry create(ModelEntry e);
	}


	/* Model Entry */

	public static class ModelEntry implements Externalizable
	{
		/**
		 * The key of the model bean.
		 * Always assigned to the key of the bean.
		 */
		public String    key;

		/**
		 * The Model Bean instance.
		 */
		public ModelBean bean;

		/**
		 * Domain key of the user owning the bean.
		 */
		public Long      domain;

		/**
		 * Primary key of the user owning the bean.
		 */
		public Long      login;

		/**
		 * The last access time.
		 */
		public long      accessTime;

		/**
		 * Transient instance marking just-created entry instance.
		 */
		public boolean   isnew;


		/* Object */

		public int     hashCode()
		{
			return key.hashCode();
		}

		public boolean equals(Object o)
		{
			return (this == o) || (o instanceof ModelEntry) &&
			  ((ModelEntry)o).key.equals(this.key);
		}


		/* Serialization */

		public void    writeExternal(ObjectOutput o)
		  throws IOException
		{
			//?: {the key not differs}
			EX.assertx(key.equals(bean.getModelKey()));

			//>: model key
			IO.str(o, key);

			//>: domain
			IO.longer(o, domain);

			//>: login
			IO.longer(o, login);

			//>: access time
			o.writeLong(accessTime);

			//>: model bean class
			IO.cls(o, bean.getClass());

			//>: model bean
			bean.writeExternal(o);
		}

		@SuppressWarnings("unchecked")
		public void    readExternal(ObjectInput i)
		  throws IOException, ClassNotFoundException
		{
			//<: model key
			key = IO.str(i);

			//<: domain
			domain = IO.longer(i);

			//<: login
			login = IO.longer(i);

			//<: access time
			accessTime = i.readLong();

			//<: model bean class
			Class cls = EX.assertn(IO.cls(i));
			EX.assertx(ModelBean.class.isAssignableFrom(cls));

			//<: model bean
			try
			{
				this.bean = (ModelBean) cls.newInstance();
				this.bean.readExternal(i);
			}
			catch(Throwable e)
			{
				throw EX.wrap(e, "Error occurred while reading Model Bean instance ",
				  "of class [", LU.cls(cls), "]!");
			}
		}
	}
}