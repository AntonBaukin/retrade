package com.tverts.endure.aggr;

/* standard Java classes */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: endure core */

import com.tverts.endure.DelayedEntity;
import com.tverts.endure.NumericIdentity;
import com.tverts.endure.Unity;


/**
 * Stores basic properties of an aggregation task.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrTaskBase implements AggrTask
{
	public static final long serialVersionUID = 0L;


	/* public: AggrTask interface */

	public Long getAggrValue()
	{
		return aggrValueID;
	}

	public void setAggrValue(Long aggrValueID)
	{
		this.aggrValueID = aggrValueID;
	}

	public Long getSource()
	{
		if(sourceKey != null)
			return sourceKey;

		if(sourceDelayed != null)
			sourceKey = sourceDelayed.accessEntity().getPrimaryKey();

		return sourceKey;
	}

	public void setSource(Long key)
	{
		this.sourceKey = key;
	}

	public void    setSourceKey(DelayedEntity key)
	{
		this.sourceDelayed = key;
	}

	public Class   getSourceClass()
	{
		if(sourceClass != null)
			return sourceClass;

		if(sourceDelayed != null)
		{
			NumericIdentity e = sourceDelayed.accessEntity();

			if(e instanceof Unity)
				sourceClass = ((Unity)e).getUnityType().getTypeClass();
			else if(e != null)
				sourceClass = HiberPoint.type(e);
		}

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


	/* private: Object interface */

	private void writeObject(java.io.ObjectOutputStream os)
	  throws java.io.IOException
	{
		//~: side-effect of the delayed source
		getSource();
		getSourceClass();

		os.defaultWriteObject();
	}


	/* private: task properties */

	private Long   aggrValueID;
	private Long   sourceKey;
	private Class  sourceClass;
	private String orderPath;
	private Map    params;

	private transient DelayedEntity sourceDelayed;
}