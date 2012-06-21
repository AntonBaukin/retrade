package com.tverts.endure.aggr;


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
		return sourceID;
	}

	public void    setSourceKey(Long key)
	{
		this.sourceID = key;
	}

	public Class   getSourceClass()
	{
		return sourceClass;
	}

	public void    setSourceClass(Class sourceClass)
	{
		this.sourceClass = sourceClass;
	}


	/* private: task properties */

	private Long   aggrValueID;
	private Long   sourceID;
	private Class  sourceClass;
}