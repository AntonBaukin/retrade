package com.tverts.model;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;

/* com.tverts: endure (+core) */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.core.UnitedAccess;


/**
 * This model bean is focused on accessing
 * one unified mirror and it's entity.
 * It stores just the primary key.
 *
 * As the data model it returns the entity
 * as {@link UnityModelData}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class UnityModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: UnityModelBean interface */

	public Long   getPrimaryKey()
	{
		return primaryKey;
	}

	public void   setPrimaryKey(Long primaryKey)
	{
		if(!updateq(this.primaryKey, primaryKey))
			markUpdated();

		this.primaryKey = primaryKey;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new UnityModelData(this);
	}

	/* public: support interface */

	public Unity     loadUnity()
	{
		return (getPrimaryKey() == null)?(null):
		  bean(GetUnity.class).getUnity(getPrimaryKey());
	}

	public United    loadEntity()
	{
		return (getPrimaryKey() == null)?(null):
		  bean(GetUnity.class).getUnited(getPrimaryKey());
	}

	public United    accessEntity()
	{
		United result = null;

		if(unitedAccess != null)
			result = unitedAccess.accessObject();

		//?: {the invoice is still in the cache}
		if(result != null)
			return result;

		//~: load the invoice
		result = loadEntity();
		if(result == null) return null;

		//~: create the access strategy
		unitedAccess = new UnitedAccess<United>(result.getPrimaryKey());

		return result;
	}


	/* private: the unity primary key */

	private Long primaryKey;


	/* private: cached instance access */

	private ObjectAccess<United> unitedAccess;
}