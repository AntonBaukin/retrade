package com.tverts.data;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityTypes;

/* com.tverts: models */

import com.tverts.model.ModelBean;

/* com.tverts: objects */

import com.tverts.objects.Adapter;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Data Source to select database stored
 * entity having the Unity Type defined.
 *
 * The model bean created is
 * {@link AdaptedEntitiesSelected}.
 *
 * The configuration interface refers the
 * page set to the source instance.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class SingleEntityDataSource extends DataSourceBase
{
	/* public: bean interface */

	public Class   getTypeClass()
	{
		return typeClass;
	}

	public void    setTypeClass(Class typeClass)
	{
		this.typeClass = typeClass;
	}

	public String  getTypeName()
	{
		return typeName;
	}

	public void    setTypeName(String typeName)
	{
		this.typeName = EX.assertn(SU.s2s(typeName));
	}

	public Adapter getAdapter()
	{
		return adapter;
	}

	public void    setAdapter(Adapter adapter)
	{
		this.adapter = adapter;
	}


	/* public: Data Source (data) */

	public ModelBean createModel(DataCtx ctx)
	{
		return new AdaptedEntitiesSelected().init(
		  did(), ctx, adapter,
		  UnityTypes.unityType(typeClass, typeName)
		);
	}

	public Object    provideData(ModelBean m)
	{
		if(!(m instanceof AdaptedEntitiesSelected))
			throw EX.state("Wrong model type!");

		AdaptedEntitiesSelected x = (AdaptedEntitiesSelected)m;

		//?: {has no instance selected}
		EX.assertn(x.getEntity(), "No entity is selected in the model!");

		//~: data object
		SingleEntity d = new SingleEntity();

		d.setPrimaryKey(x.getEntity());
		d.setUnityType(x.getUnityType());

		return d;
	}

	public Object    provideData(DataCtx ctx)
	{
		throw EX.unop();
	}


	/* private: the configuration */

	private Class   typeClass;
	private String  typeName;
	private Adapter adapter;
}