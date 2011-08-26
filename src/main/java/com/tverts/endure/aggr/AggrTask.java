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

	/**
	 * This parameter has meaning only for the tasks creating
	 * components. If not defined, the new component is added
	 * to the end, or to the head of the order according to
	 * {@link #isBeforeAfter()} flag.
	 *
	 * If defined, this is the primary key of the order
	 * reference. The crucial thing here is that it is possible
	 * that the reference itself may have no owned component in
	 * the aggregated value.
	 *
	 * The reference instance must have the save class, owner,
	 * and order type as the source of the aggregated item
	 * to create.
	 */
	public Long    getOrderKey();

	public void    setOrderKey(Long id);

	/**
	 * Tells whether to insert before {@code false} or after
	 * {@code true} the components with source defined by
	 * {@link #getOrderKey()}.
	 *
	 * If the reference is not defined, before {@code false}
	 * means to insert as the first item of the order, after
	 * {@code true} means to insert as the last one.
	 */
	public boolean isBeforeAfter();

	public void    setBeforeAfter(boolean ba);
}