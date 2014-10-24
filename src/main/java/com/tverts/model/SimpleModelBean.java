package com.tverts.model;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;


/**
 * This model contains a map of properties.
 *
 * Model Data is stored directly in the model
 * as a property mapped by ModelData.class.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "simple-model")
@SuppressWarnings("unchecked")
public class SimpleModelBean extends ModelBeanBase
{
	/* Simple Model */

	public Map       getMap()
	{
		return (map != null)?(map):(map = new HashMap(3));
	}

	public void      setMap(Map map)
	{
		if(map == null)
			map = new HashMap<Serializable, Serializable>(5);

		EX.assertx(map instanceof Serializable);
		this.map = map;
	}

	public <D extends ModelData & Serializable> SimpleModelBean setData(D data)
	{
		EX.assertx((data == null) || (data instanceof Serializable));
		put(ModelData.class, data);
		return this;
	}


	/* public: support interface */

	public Object    get(Serializable key)
	{
		return getMap().get(key);
	}

	public <T> T     get(Serializable key, Class<T> cls)
	{
		Object res = getMap().get(key);

		if((res != null) && !cls.isAssignableFrom(res.getClass()))
			throw EX.state("Key [", key, "] maps object of class [",
			 res.getClass().getName(), "], not a [", cls.getName(), "]!"
			);

		return (T) res;
	}

	public Object    put(Serializable key, Serializable val)
	{
		if(val == null)
			return getMap().remove(key);
		else
			return getMap().put(key, val);
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return get(ModelData.class, ModelData.class);
	}


	/* private: encapsulated data */

	private Map map;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		IO.obj(o, map);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		map = IO.obj(i, Map.class);
	}
}