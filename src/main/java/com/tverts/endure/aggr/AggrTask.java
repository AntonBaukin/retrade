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
	public Long    getAggrValueID();

	public void    setAggrValueID(Long id);

	public Long    getSourceID();

	public void    setSourceID(Long id);

	/**
	 * This is primary key of some Unity instance being
	 * the source for the existing components before
	 * (or after) those you want to insert the
	 * component is being created by this task.
	 *
	 * Has meaning only for the tasks creating components.
	 * If not defined, the new component is added to the
	 * end or to the head of the order according to
	 * {@link #isBeforeAfter()} flag.
	 */
	public Long    getOrderRefID();

	public void    setOrderRefID(Long id);

	/**
	 * Tells whether to insert before {@code false} or after
	 * {@code true} the components with source defined by
	 * {@link #getOrderRefID()}.
	 *
	 * If the reference is not defined, before {@code false}
	 * means to insert as the first item of the order, after
	 * {@code true} means to insert as the last one.
	 */
	public boolean isBeforeAfter();

	public void    setBeforeAfter(boolean ba);
}