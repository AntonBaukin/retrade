package com.tverts.model;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.system.SessionedAccess;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;

/* com.tverts: endure (+core) */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.core.UnitedAccess;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * This model bean is focused on accessing
 * one unified mirror and it's entity.
 * It stores just the primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnityModelBean
       extends        ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: UnityModelBean interface */

	public Long   getPrimaryKey()
	{
		return primaryKey;
	}

	public void   setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public void   setInstance(United instance)
	{
		if(instance == null)
		{
			setInstanceUndefined();
			return;
		}

		if(instance.getPrimaryKey() == null)
			throw new IllegalArgumentException();
		setInstanceAccesses(instance);
	}


	/* public: ModelBean (data access) interface */

	public abstract ModelData modelData();


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
		unitedAccess = createAccess(result);

		return result;
	}


	/* protected: support interface */

	protected void setInstanceUndefined()
	{
		setPrimaryKey(null);
		unitedAccess = null;
	}

	protected void setInstanceAccesses(United instance)
	{
		setPrimaryKey(instance.getPrimaryKey());
		unitedAccess = createAccess(instance);
	}

	protected ObjectAccess<United>
	               createAccess(United instance)
	{
		return new SessionedAccess<United>(instance,
		  new UnitedAccess<United>(getPrimaryKey()));
	}


	/* private: the unity primary key */

	private Long primaryKey;


	/* private: cached instance access */

	private ObjectAccess<United> unitedAccess;
}