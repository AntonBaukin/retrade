package com.tverts.model;

/* standard Java classes */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This model contains a map of properties.
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(name = "model")
@SuppressWarnings("unchecked")
public class SimpleModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Map       getMap()
	{
		return (map != null)?(map):(map = new HashMap(3));
	}

	public void      setMap(Map map)
	{
		if(map == null)
			map = new HashMap<Serializable, Serializable>(5);
		this.map = map;
	}

	@XmlTransient
	public ModelData getData()
	{
		return data;
	}

	public void      setData(ModelData data)
	{
		this.data = data;
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
		return getMap().put(key, val);
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return (data != null)?(data):
		  get(ModelData.class, ModelData.class);
	}


	/* the map & data */

	private Map       map;
	private ModelData data;
}