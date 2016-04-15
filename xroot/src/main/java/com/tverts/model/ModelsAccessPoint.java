package com.tverts.model;

/* com.tverts: models store */

import com.tverts.model.store.ModelsStoreAccess;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


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

	public static ModelsStore modelsStore()
	{
		//?: {has no store access strategy}
		EX.assertn(INSTANCE.modelAccess, "No UI Model Beans Store ",
		  "access strategy is installed in the Point!"
		);

		return EX.assertn(INSTANCE.modelAccess.accessStore(),
		  "UI Model Beans Store access strategy [",
		  LU.cls(INSTANCE.modelAccess), "] is broken!"
		);
	}


	/* Models Access Point (configuration) */

	public void setModelAccess(ModelsStoreAccess modelAccess)
	{
		this.modelAccess = EX.assertn(modelAccess);
	}

	private ModelsStoreAccess modelAccess;
}