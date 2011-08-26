package com.tverts.endure.aggr;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.Unity;

/* com.tverts: support */

import static com.tverts.support.OU.obj2xml;
import static com.tverts.support.OU.xml2obj;


/**
 * TODO comment AggrRequest
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrRequest implements NumericIdentity
{
	/* public: NumericIdentity interface */

	public Long       getPrimaryKey()
	{
		return primaryKey;
	}

	public void       setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: AggrRequest (source) interface */

	/**
	 * Source Unity of the request. Source originates
	 * the request. Without it the request is not needed.
	 */
	public Unity      getSource()
	{
		return source;
	}

	public void       setSource(Unity source)
	{
		this.source = source;

		if((this.aggrTask != null) && (source != null))
		{
			this.aggrTask.setSourceKey(source.getPrimaryKey());

			if(source.getUnityType() != null)
				this.aggrTask.setSourceClass(source.getUnityType().getTypeClass());
		}
	}

	public Unity      getOrderRef()
	{
		return orderRef;
	}

	public void       setOrderRef(Unity orderRef)
	{
		this.orderRef = orderRef;
	}


	/* public: AggrRequest (value + task) interface */

	public AggrValue  getAggrValue()
	{
		return aggrValue;
	}

	public void       setAggrValue(AggrValue aggrValue)
	{
		this.aggrValue = aggrValue;

		if((this.aggrTask != null) && (aggrValue != null))
			this.aggrTask.setAggrValueKey(aggrValue.getPrimaryKey());
	}

	public AggrTask   getAggrTask()
	{
		return aggrTask;
	}

	public void       setAggrTask(AggrTask aggrTask)
	{
		this.aggrTask = aggrTask;

		//HINT: here we copy the fields from the task to
		//  this request and vice versa. But a task stores
		//  not the references, but the primary keys.
		//
		//  But we do not load that instances by their keys
		//  as database context is not assumed.

		//~: fill aggregated value ID
		if((aggrTask != null) && (this.aggrValue != null))
			aggrTask.setAggrValueKey(this.aggrValue.getPrimaryKey());

		//~: fill the source ID
		if((aggrTask != null) && (this.source != null) &&
		   (aggrTask.getSourceKey() == null)
		  )
		{
			aggrTask.setSourceKey(this.source.getPrimaryKey());

			if(this.source.getUnityType() != null)
				aggrTask.setSourceClass(this.source.getUnityType().getTypeClass());
		}
	}

	public String     getAggrTaskXML()
	{
		return (getAggrTask() == null)?(null):
		  obj2xml(getAggrTask());
	}

	public void       setAggrTaskXML(String xml)
	{
		setAggrTask(xml2obj(xml, AggrTask.class));
	}

	/**
	 * Persisted attribute with the class of the aggregation task.
	 * Used by the aggregation scheduler when selecting requests
	 * to execute.
	 */
	public Class      getAggrTaskClass()
	{
		return (this.aggrTask == null)?(null):
		  (this.aggrTask.getClass());
	}

	public void       setAggrTaskClass(Class aggrTaskClass)
	{}

	public String     getErrorText()
	{
		return errorText;
	}

	public void       setErrorText(String errorText)
	{
		this.errorText = errorText;
	}


	/* persisted attributes */

	private Long      primaryKey;
	private Unity     source;
	private Unity     orderRef;
	private AggrValue aggrValue;
	private AggrTask  aggrTask;
	private String    errorText;
}