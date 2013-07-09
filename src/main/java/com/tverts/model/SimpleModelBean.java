package com.tverts.model;

/* standard Java classes */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This model contains a map of properties.
 *
 * @author anton.baukin@gmail.com
 */
public class SimpleModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Map<Serializable, Serializable> getMap()
	{
		return map;
	}

	public void setMap(Map<Serializable, Serializable> map)
	{
		if(map == null)
			map = new HashMap<Serializable, Serializable>(5);
		this.map = map;
	}


	/* public: support interface */

	public Serializable get(Serializable key)
	{
		return map.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T        get(Serializable key, Class<T> cls)
	{
		Object res = map.get(key);

		if((res != null) && !cls.isAssignableFrom(res.getClass()))
			throw EX.state("Key [", key, "] maps object of class [",
			 res.getClass().getName(), "], not a [", cls.getName(), "]!"
			);

		return (T) res;
	}

	public Serializable put(Serializable key, Serializable val)
	{
		return map.put(key, val);
	}


	/* the map */

	private Map<Serializable, Serializable> map =
	  new HashMap<Serializable, Serializable>(5);
}