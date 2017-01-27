package com.tverts.endure.aggr;

/**
 * Java Bean containing single request to the
 * aggneration system to update the aggregated
 * value. A task is stored in database as XML in
 * the request created from it's attributes.
 *
 * @author anton.baukin@gmail.com
 */
public interface AggrTask
{
	/* Aggregation Task */

	/**
	 * Primary key of the aggregated value this task is for.
	 * {@link AggrRequest} automatically installs this value
	 * to a task when attaching it.
	 */
	public Long   getAggrValue();

	public void   setAggrValue(Long id);

	/**
	 * Primary key of the entity originated this task.
	 * Not required, but each aggregation task has it.
	 */
	public Long   getSource();

	public void   setSource(Long id);

	/**
	 * The (actual leaf) class of the source instance.
	 * Must be defined if the source key is.
	 */
	public Class  getSourceClass();

	public void   setSourceClass(Class sourceClass);

	/**
	 * If the source instance has no own order index,
	 * set the path (including the property name) to
	 * the source class.
	 *
	 * Example: let A refers B (with b) having standard
	 * order index property, A instance is the source,
	 * the path is 'b.orderIndex'.
	 *
	 * Note to omit prefix dot.
	 */
	public String getOrderPath();

	public void   setOrderPath(String path);
}