package com.tverts.model;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.endure.core.NumericAccess;
import com.tverts.hibery.system.HiberSystem;
import com.tverts.hibery.system.SessionedAccess;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;

/* com.tverts: endure (+core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.core.GetUnity;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * This general model bean is to access
 * {@link NumericIdentity} instances
 * by the class and Long primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class NumericModelBean
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
		if(!updateq(this.primaryKey, primaryKey))
			markUpdated();

		this.primaryKey = primaryKey;
	}

	public Class  getObjectClass()
	{
		return objectClass;
	}

	public void   setObjectClass(Class<? extends NumericIdentity> cls)
	{
		this.objectClass = cls;
	}

	public void   setInstance(NumericIdentity instance)
	{
		if(instance == null)
		{
			setInstanceUndefined();
			return;
		}

		if(instance.getPrimaryKey() == null)
			throw new IllegalArgumentException();
		setInstanceAccessed(instance);
	}


	/* public: ModelBean (data access) interface */

	public abstract ModelData modelData();


	/* public: support interface */

	@SuppressWarnings("unchecked")
	public NumericIdentity    loadNumeric()
	{
		if(getObjectClass() == null)
			throw new IllegalStateException();

		return (getPrimaryKey() == null)?(null):
		  bean(GetUnity.class).getNumeric(
		    getObjectClass(), getPrimaryKey());
	}

	public NumericIdentity    accessNumeric()
	{
		NumericIdentity result = null;

		if(numericAccess != null)
			result = (NumericIdentity)numericAccess.accessObject();

		//?: {the instance is still in the cache}
		if(result != null)
			return result;

		//~: load the invoice
		result = loadNumeric();
		if(result == null) return null;

		//~: create the access strategy
		numericAccess = createAccess(result);

		return result;
	}


	/* protected: support interface */

	protected void         setInstanceUndefined()
	{
		setObjectClass(null);
		setPrimaryKey(null);
		numericAccess = null;
	}

	@SuppressWarnings("unchecked")
	protected void         setInstanceAccessed(NumericIdentity instance)
	{
		setObjectClass(HiberSystem.getInstance().
		  findActualClass(instance));
		setPrimaryKey(instance.getPrimaryKey());
		numericAccess = createAccess(instance);
	}

	@SuppressWarnings("unchecked")
	protected ObjectAccess createAccess(NumericIdentity instance)
	{
		return new SessionedAccess(instance, new NumericAccess(
		  getObjectClass(), getPrimaryKey()));
	}


	/* private: the object's class and key */

	private Class objectClass;
	private Long  primaryKey;


	/* private: cached instance access */

	private ObjectAccess numericAccess;
}