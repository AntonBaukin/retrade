package com.tverts.model;

/**
 * Entry point to access the model attached to the user
 * request to the system. The model is created on demand.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ModelAccessPoint
{
	/* ModelAccessPoint Singleton */

	public static ModelAccessPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ModelAccessPoint INSTANCE =
	  new ModelAccessPoint();

	protected ModelAccessPoint()
	{}

	/* public: ModelAccessPoint interface */

	public static ModelPoint model()
	{
		ModelAccess ma = getInstance().getModelAccess();
		if(ma == null) throw new IllegalStateException();

		return ma.accessModel();
	}


	/* public: ModelAccessPoint (bean) interface */

	public ModelAccess getModelAccess()
	{
		return modelAccess;
	}

	public void        setModelAccess(ModelAccess modelAccess)
	{
		if(modelAccess == null)
			throw new IllegalArgumentException();

		this.modelAccess = modelAccess;
	}

	/* private: point entries */

	private ModelAccess modelAccess;
}