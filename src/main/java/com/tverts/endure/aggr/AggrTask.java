package com.tverts.endure.aggr;

/**
 * Aggregation task is that (serializable) object that
 * is created and initialized to affect the aggregated
 * value the task refers by the ID.
 *
 * Each aggregation value class and the related aggregator
 * implementation do expect some distinct types of a task
 * having specific fields (parameters).
 *
 * Aggregation task is not stored in the database directly.
 * It is wrapped into {@link AggrRequest} persisted object.
 *
 * Being serializable, task must also be Java Bean!
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface AggrTask extends java.io.Serializable
{
	/* public: AggrTask interface */

	/**
	 * Primary key of the aggregated value this task is for.
	 * {@link AggrRequest} automatically installs this value
	 * to a task when attaching it.
	 */
	public Long    getAggrValueKey();

	public void    setAggrValueKey(Long id);

	public Long    getSourceKey();

	public void    setSourceKey(Long id);

	/**
	 * The (actual leaf) class of the source instance.
	 * Must be defined if the source exists. (Almost always.)
	 */
	public Class   getSourceClass();

	public void    setSourceClass(Class orderClass);
}