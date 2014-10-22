package com.tverts.model;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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

import com.tverts.support.EX;


/**
 * This model bean is focused on accessing
 * one unified mirror and it's entity.
 * It stores just the primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "unity-model")
public class UnityModelBean extends ModelBeanBase
{
	/* Unity Model Bean */

	@XmlElement(name = "objectKey")
	public Long   getPrimaryKey()
	{
		return primaryKey;
	}

	public void   setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* Unity Model Bean (support) */

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

	public void      setInstance(United instance)
	{
		if(instance == null)
			setInstanceUndefined();
		else
		{
			EX.assertn(instance.getPrimaryKey());
			setInstanceAccesses(instance);
		}
	}

	private transient ObjectAccess<United> unitedAccess;


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


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		o.writeLong(primaryKey);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		primaryKey  = i.readLong();
	}
}