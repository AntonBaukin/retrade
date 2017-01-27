package com.tverts.endure.aggr;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: endure core */

import com.tverts.endure.DelayedEntity;
import com.tverts.endure.NumericIdentity;
import com.tverts.endure.Unity;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Base of an aggregation task.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrTaskBase implements AggrTask
{
	/* Aggregation Task */

	public Long getAggrValue()
	{
		return aggrValue;
	}

	private Long aggrValue;

	public void setAggrValue(Long aggrValue)
	{
		this.aggrValue = aggrValue;
	}

	public Long getSource()
	{
		if(sourceKey != null)
			return sourceKey;

		//?: {is a delayed instance referred}
		if(sourceDelayed != null)
			sourceKey = sourceDelayed.accessEntity().getPrimaryKey();

		return sourceKey;
	}

	private Long sourceKey;

	public void setSource(Long key)
	{
		this.sourceKey = key;
	}

	private DelayedEntity sourceDelayed;

	public void setSource(DelayedEntity key)
	{
		this.sourceDelayed = key;
	}

	public Class getSourceClass()
	{
		if(sourceClass != null)
			return sourceClass;

		//?: {is a delayed instance referred}
		if(sourceDelayed != null)
		{
			NumericIdentity e = sourceDelayed.accessEntity();

			//?: {source is referred by it's unity}
			if(e instanceof Unity)
				sourceClass = ((Unity)e).getUnityType().getTypeClass();
			else if(e != null)
				sourceClass = HiberPoint.type(e);
		}

		return sourceClass;
	}

	private Class sourceClass;

	public void setSourceClass(Class sourceClass)
	{
		this.sourceClass = sourceClass;
	}

	public String  getOrderPath()
	{
		return orderPath;
	}

	private String orderPath;

	public void setOrderPath(String orderPath)
	{
		this.orderPath = orderPath;
	}


	/* public: AggrTaskBase interface (parameters) */

	public void    param(String name, Object value)
	{
		EX.asserts(name);

		if(value != null)
		{
			if(params == null)
				params = new HashMap<>();

			params.put(name, value);
		}
		else if(params != null)
			params.remove(name);
	}

	private Map<String, Object> params;

	public Object  param(String name)
	{
		EX.asserts(name);
		return (params == null)?(null):(params.get(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T   param(String name, Class<T> cls)
	{
		EX.asserts(name);
		EX.assertn(cls);

		Object res = param(name);
		if(res == null) return null;

		EX.assertx(cls.isAssignableFrom(res.getClass()),
		  "Aggregation Task parameter [", name,
		  "] is not an instance of ", cls.getName(), "!"
		);

		return (T)res;
	}
}