package com.tverts.model.keys;

/* com.tverts: hibery */

import com.tverts.endure.keys.KeysContextStruct;
import com.tverts.endure.keys.KeysGenerator;
import com.tverts.endure.keys.KeysPoint;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Model keys generator creating keys via
 * Hibernate Sequence generator named
 * {@link KeysPoint#GEN_MODEL}.
 *
 * The format of the key as follows:
 *
 *   bean-uid_bean-uid.
 *
 * Each generator instance has it's own unique key
 * that is inserted in the result as the first part.
 * If each model will have own generator instance,
 * it would allow to detect the beans of the same model.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ModelKeysGenHiber extends ModelKeysGenBase
{
	public static final long serialVersionUID = 0L;


	/* public: ModelKeysGenHiber (bean) interface */

	public String getModelKey()
	{
		return modelKey;
	}

	public void   setModelKey(String modelKey)
	{
		this.modelKey = modelKey;
	}


	/* protected: ModelKeysGenBase interface */

	protected String generateSerialKey(ModelBean bean)
	{
		return SU.cats(modelKey(), '_', genNextKey(bean));
	}


	/* protected: generation and Hibernate */

	protected String modelKey()
	{
		synchronized(this)
		{
			if(this.modelKey == null)
				this.modelKey = genModelKey();
		}

		return this.modelKey;
	}

	protected String genModelKey()
	{
		return genNextKey(this);
	}

	protected String genNextKey(Object target)
	{
		KeysGenerator gen = KeysPoint.getInstance().
		  getGenerator(KeysPoint.GEN_MODEL);

		Object        key = gen.createPrimaryKey(
		  new KeysContextStruct(target.getClass()).
		    setSavedInstance(target)
		);

		//?: {the key was not created}
		if(key == null) throw new IllegalStateException(
		  "Unable to generate model key for the target bean " +
		  LU.cls(target));

		//?: {the key is long value}
		if(key instanceof Long)
			key = Long.toHexString((Long)key);

		return key.toString();
	}


	/* private: the state of the generator */

	private String modelKey;
}