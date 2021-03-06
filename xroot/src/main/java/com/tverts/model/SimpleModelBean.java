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
import javax.xml.bind.annotation.XmlTransient;
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

	@XmlTransient
	public Map  getMap()
	{
		return (map != null)?(map):(map = new HashMap(3));
	}

	private Map map;

	public void setMap(Map map)
	{
		if(map == null)
			map = new HashMap<Serializable, Serializable>(5);

		EX.assertx(map instanceof Serializable);
		this.map = map;
	}

	public SimpleModelBean setData(SimpleModelData data)
	{
		put(ModelData.class, data);
		return this;
	}


	/* public: support interface */

	public Object get(Serializable key)
	{
		return getMap().get(key);
	}

	public <T> T  get(Serializable key, Class<T> cls)
	{
		Object res = getMap().get(key);

		if((res != null) && !cls.isAssignableFrom(res.getClass()))
			throw EX.state("Key [", key, "] maps object of class [",
			 res.getClass().getName(), "], not a [", cls.getName(), "]!"
			);

		return (T) res;
	}

	public <T> T  getx(Class<T> cls)
	{
		return get(cls, cls);
	}

	public SimpleModelBean put(Serializable key, Serializable val)
	{
		if(val == null)
			getMap().remove(key);
		else
			getMap().put(key, val);

		return this;
	}


	/* public: ModelBean (data access) interface */

	public SimpleModelData modelData()
	{
		SimpleModelData md = get(ModelData.class, SimpleModelData.class);

		if(md != null) return md; else
		{
			md = (SimpleModelData) super.modelData();
			if(md != null)
				setData(md);
		}

		return md;
	}


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

		//~: assign itself to the data
		SimpleModelData md = modelData();
		if(md != null)
			md.setModel(this);
	}
}