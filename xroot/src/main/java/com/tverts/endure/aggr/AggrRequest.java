package com.tverts.endure.aggr;

/* com.tverts: endure */

import com.tverts.endure.DelayedEntity;
import com.tverts.endure.NumericBase;
import com.tverts.endure.Unity;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.OU.obj2xml;
import static com.tverts.support.OU.xml2obj;


/**
 * Stored in the database request to the aggregation engine.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrRequest extends NumericBase
{
	/* public: AggrRequest (source) interface */

	/**
	 * Source Unity of the request. It may be defined
	 * for entities with existing Unity mirror.
	 */
	public Unity      getSource()
	{
		return (source != null)?(source):(accessSource == null)?(null):
		  (source = (Unity) accessSource.accessEntity());
	}

	public void       setSource(Unity source)
	{
		this.source = source;

		if((this.aggrTask != null) && (source != null))
		{
			this.aggrTask.setSourceKey(EX.assertn(
			  source.getPrimaryKey()));

			this.aggrTask.setSourceClass(EX.assertn(
			  source.getUnityType()).getTypeClass());
		}
	}

	public void       setSource(DelayedEntity access)
	{
		this.accessSource = access;

		if((this.aggrTask instanceof AggrTaskBase) && (access != null))
			((AggrTaskBase)this.aggrTask).setSourceKey(access);
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
		if((aggrTask == null) && (aggrTaskXML != null))
			aggrTask = xml2obj(aggrTaskXML, AggrTask.class);

		return aggrTask;
	}

	public void       setAggrTask(AggrTask aggrTask)
	{
		this.aggrTask = aggrTask;
		this.aggrTaskClass = this.aggrTaskXML = null;

		//HINT: here we copy the fields from the task to
		//  this request and vice versa. But a task stores
		//  not the references, but the primary keys.
		//
		//  But we do not load that instances by their keys
		//  as database context is not assumed.

		//~: set aggregated value key
		if((aggrTask != null) && (this.aggrValue != null))
			aggrTask.setAggrValueKey(this.aggrValue.getPrimaryKey());

		//~: set the source key
		if(aggrTask != null) if(this.source != null)
			aggrTask.setSourceKey(EX.assertn(this.source.getPrimaryKey()));
		else if((this.accessSource != null) && (aggrTask instanceof AggrTaskBase))
			((AggrTaskBase)aggrTask).setSourceKey(this.accessSource);
	}

	public String     getAggrTaskXML()
	{
		if((aggrTaskXML == null) && (aggrTask != null))
			aggrTaskXML = obj2xml(aggrTask);
		return aggrTaskXML;
	}

	public void       setAggrTaskXML(String xml)
	{
		this.aggrTaskXML = xml;
	}

	/**
	 * Persisted attribute with the class of the aggregation task.
	 * Used by the aggregation scheduler when selecting requests
	 * to execute.
	 */
	public String     getAggrTaskClass()
	{
		if((aggrTaskClass == null) && (aggrTask != null))
			aggrTaskClass = aggrTask.getClass().getName();
		return aggrTaskClass;
	}

	public void       setAggrTaskClass(String aggrTaskClass)
	{
		this.aggrTaskClass = aggrTaskClass;
	}

	public String     getErrorText()
	{
		return errorText;
	}

	public void       setErrorText(String errorText)
	{
		this.errorText = errorText;
	}


	/* persisted attributes */

	private Unity         source;
	private DelayedEntity accessSource;
	private AggrValue     aggrValue;
	private AggrTask      aggrTask;
	private String        aggrTaskXML;
	private String        aggrTaskClass;
	private String        errorText;
}