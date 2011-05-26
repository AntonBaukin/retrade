package com.tverts.actions;

/* standard Java class */

import java.util.ArrayList;
import java.util.Iterator;

/* Apache Commons Collections */

import org.apache.commons.collections.iterators.ReverseListIterator;


/**
 * Stores the chain in the array in reversed order.
 * It is good when placing new actions as the first
 * elements of the chain.
 *
 * @author anton.baukin@gmail.com
 */
public final class ActionChainAsList implements ActionChain
{
	/* public: ActionChain interface */

	public Action      first()
	{
		return (actions.isEmpty())?(null):
		  (actions.get(actions.size() - 1));
	}

	public ActionChain first(Action obj)
	{
		actions.add(obj);
		return this;
	}

	public Action      last()
	{
		return (actions.isEmpty())?(null):
		  (actions.get(0));
	}

	public ActionChain last(Action obj)
	{
		actions.add(0, obj);
		return this;
	}

	public ActionChain insert(Action ref, Action obj)
	{
		//~: find the element
		int i = actions.indexOf(ref);
		if(i == -1) throw new IllegalStateException();

		//HINT: in the reversed order 'before' means 'after'
		actions.add(i + 1, obj);

		return this;
	}

	public ActionChain append(Action ref, Action obj)
	{
		//~: find the element
		int i = actions.indexOf(ref);
		if(i == -1) throw new IllegalStateException();

		//HINT: in the reversed order 'after' means 'before'
		actions.add(i, obj);

		return this;
	}

	public ActionChain remove(Action obj)
	{
		actions.remove(obj);
		return this;
	}

	public boolean     empty()
	{
		return actions.isEmpty();
	}

	public int         size()
	{
		return actions.size();
	}

	/* public: Iterable interface */

	@SuppressWarnings("unchecked")
	public Iterator<Action> iterator()
	{
		return new ReverseListIterator(actions);
	}

	/* private: actions collection */

	private ArrayList<Action> actions =
	  new ArrayList<Action>(8);
}