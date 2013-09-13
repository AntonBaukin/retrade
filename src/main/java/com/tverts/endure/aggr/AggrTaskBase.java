package com.tverts.endure.aggr;

/* standard Java classes */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: endure core */

import com.tverts.endure.DelayedKey;


/**
 * Stores basic properties of an aggregation task.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrTaskBase implements AggrTask
{
	public static final long serialVersionUID = 0L;


	/* public: AggrTask interface */

	public Long    getAggrValueKey()
	{
		return aggrValueID;
	}

	public void    setAggrValueKey(Long aggrValueID)
	{
		this.aggrValueID = aggrValueID;
	}

	public Long    getSourceKey()
	{
		return (sourceKey != null)?(sourceKey):(sourceDelayed == null)?(null):
		  (sourceKey = sourceDelayed.delayedKey());
	}

	public void    setSourceKey(Long key)
	{
		this.sourceKey = key;
	}

	public void    setSourceKey(DelayedKey key)
	{
		this.sourceDelayed = key;
	}

	public Class   getSourceClass()
	{
		return sourceClass;
	}

	public void    setSourceClass(Class sourceClass)
	{
		this.sourceClass = sourceClass;
	}

	public String  getOrderPath()
	{
		return orderPath;
	}

	public void    setOrderPath(String orderPath)
	{
		this.orderPath = orderPath;
	}


	/* public: AggrTaskBase interface (parameters) */

	@SuppressWarnings("unchecked")
	public void    param(Serializable name, Serializable value)
	{
		if(this.params == null)
			this.params = new HashMap(3);

		if(value == null)
			this.params.remove(name);
		else
			this.params.put(name, value);
	}

	public Object  param(Object name)
	{
		return (this.params == null)?(null):
		  this.params.get(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T   param(Object name, Class<T> c1ass)
	{
		Object res = (this.params == null)?(null):
		  this.params.get(name);
		if((res == null) || (c1ass == null))
			return (T)res;

		if(!c1ass.isAssignableFrom(res.getClass()))
			throw new IllegalArgumentException(String.format(
			  "Aggregation Task (raw) parameter '%s' is not an instance of '%s'!",
			  name.toString(), c1ass.getName()));

		return (T)res;
	}


	/* private: task properties */

	private Long       aggrValueID;
	private Long       sourceKey;
	private DelayedKey sourceDelayed;
	private Class      sourceClass;
	private String     orderPath;
	private Map        params;
}