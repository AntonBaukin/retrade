package com.tverts.support.xml;

/* standard Java classes */

import java.util.ArrayList;


/**
 * Direct implementation of {@link SaxStack}.
 *
 * @author anton.baukin@gmail.com
 */
public final class SaxStackBase<State>
       implements  SaxStack<State>
{
	/* public: SaxStack interface */

	public SaxEvent<State> top()
	{
		return list.isEmpty()?(null):
		  list.get(list.size() - 1);
	}

	public SaxEvent<State> get(int i)
	{
		if((i < 0) || (i >= list.size()))
			return null;
		return list.get(i);
	}

	public SaxEvent<State> pop()
	{
		if(list.isEmpty())
			return null;
		return list.remove(list.size() - 1);
	}

	public SaxStack<State> push(SaxEvent<State> e)
	{
		list.add(e);
		return this;
	}

	public int             size()
	{
		return list.size();
	}

	public boolean         empty()
	{
		return list.isEmpty();
	}


	/* private: the stack */

	private ArrayList<SaxEvent<State>> list =
	  new ArrayList<SaxEvent<State>>(8);
}