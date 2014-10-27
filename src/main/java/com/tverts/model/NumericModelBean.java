package com.tverts.model;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.system.SessionedAccess;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;

/* com.tverts: endure (+core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.core.NumericAccess;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;


/**
 * This general model bean is to access
 * {@link NumericIdentity} instances
 * by the class and Long primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class NumericModelBean extends ModelBeanBase
{
	/* Numeric Model Bean */

	public Long getPrimaryKey()
	{
		return primaryKey;
	}

	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public Class<? extends NumericIdentity> getObjectClass()
	{
		return objectClass;
	}

	public void setObjectClass(Class<? extends NumericIdentity> cls)
	{
		this.objectClass = cls;
	}


	/* Numeric Model Bean (support) */

	public void            setInstance(NumericIdentity instance)
	{
		if(instance == null)
			setInstanceUndefined();
		else
		{
			EX.assertn(instance.getPrimaryKey());
			setInstanceAccessed(instance);
		}
	}

	@SuppressWarnings("unchecked")
	public NumericIdentity loadNumeric()
	{
		if(getPrimaryKey() == null)
			return null;

		EX.assertn(getObjectClass());
		return bean(GetUnity.class).
		  getNumeric(getObjectClass(), getPrimaryKey());
	}

	public NumericIdentity accessNumeric()
	{
		NumericIdentity result = null;

		if(numericAccess != null)
			result = (NumericIdentity) numericAccess.accessObject();

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

	private transient ObjectAccess numericAccess;


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
		setObjectClass(HiberPoint.type(instance));
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

	private Long                             primaryKey;
	private Class<? extends NumericIdentity> objectClass;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.longer(o, primaryKey);
		IO.cls(o, objectClass);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		primaryKey  = IO.longer(i);
		objectClass = IO.cls(i);
	}
}