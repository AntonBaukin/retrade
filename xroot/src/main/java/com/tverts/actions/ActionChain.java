package com.tverts.actions;

/**
 * Action Chain stores the actions in the order
 * to execute them. They are invoked from the
 * {@link #first()} one to the {@link #last()} one.
 * The iteration is in the same order.
 *
 * @author anton.baukin@gmail.com
 */
public interface ActionChain extends Iterable<Action>
{
	/* public: ActionChain interface */

	/**
	 * Returns the first action to execute.
	 */
	public Action      first();

	/**
	 * Adds the action to the chain as the first element.
	 */
	public ActionChain first(Action obj);

	/**
	 * Returns the final action to execute.
	 */
	public Action      last();

	/**
	 * Adds the action to the chain as the last element.
	 */
	public ActionChain last(Action obj);

	/**
	 * Inserts the action before the reference defined.
	 *
	 * If reference instance is not found in the chain
	 * {@link IllegalStateException} is raised.
	 */
	public ActionChain insert(Action ref, Action obj);

	/**
	 * Adds the action after the reference defined.
	 *
	 * If reference instance is not found in the chain
	 * {@link IllegalStateException} is raised.
	 */
	public ActionChain append(Action ref, Action obj);

	/**
	 * Removes the action defined if it presents in the chain.
	 */
	public ActionChain remove(Action obj);

	/**
	 * Tells whether the chain is empty.
	 */
	public boolean     empty();

	public int         size();
}