package com.tverts.model;

/* com.tverts: models store */

import com.tverts.model.store.ModelsStoreAccess;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Entry point to access the model attached to the user
 * request to the system. The model is created on demand.
 *
 * @author anton.baukin@gmail.com
 */
public class ModelsAccessPoint
{
	/* ModelAccessPoint Singleton */

	public static ModelsAccessPoint getInstance()
	{
		return INSTANCE;
	}

	public static final ModelsAccessPoint INSTANCE =
	  new ModelsAccessPoint();

	protected ModelsAccessPoint()
	{}


	/* Models Access Point */

	public static ModelsStore store()
	{
		//?: {has no store access strategy}
		EX.assertn(INSTANCE.modelAccess);

		//?: {access strategy got no store}
		return EX.assertn(INSTANCE.modelAccess.accessStore());
	}

	public static ModelBean   read(String key)
	{
		return ModelsAccessPoint.store().read(key);
	}

	@SuppressWarnings("unchecked")
	public static <B extends ModelBean> B
	                          read(String key, Class<B> beanClass)
	{
		ModelBean mb = ModelsAccessPoint.read(key);

		if((mb != null) && (beanClass != null) &&
		   !beanClass.isAssignableFrom(mb.getClass())
		  )
			throw EX.state("Model bean requested by the key [", key,
			  "] is not a class checked [", beanClass.getName(), "]!");

		return (B) mb;
	}


	/* Models Access Point (configuration) */

	public void setModelAccess(ModelsStoreAccess modelAccess)
	{
		this.modelAccess = EX.assertn(modelAccess);
	}

	private ModelsStoreAccess modelAccess;
}