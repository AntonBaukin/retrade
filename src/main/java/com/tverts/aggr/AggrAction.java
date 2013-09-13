package com.tverts.aggr;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* com.tverts: actions */

import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;


/**
 * Action to send one or more aggregation requests.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrAction extends ActionWithTxBase
{
	/* public: constructor */

	public AggrAction(ActionTask task)
	{
		super(task);
	}


	/* public: AggrAction (parameters access) interface */

	public AggrAction add(AggrRequest... requests)
	{
		this.requests.addAll(Arrays.asList(requests));
		return this;
	}

	public AggrAction add(List<AggrRequest> requests)
	{
		this.requests.addAll(requests);
		return this;
	}

	public boolean    isEmpty()
	{
		return this.requests.isEmpty();
	}

	public boolean    isSynchronous()
	{
		return synch;
	}

	public AggrAction setSynchronous(boolean synch)
	{
		this.synch = synch;
		return this;
	}


	/* public: Action interface (the state access) */

	public Object     getResult()
	{
		return null;
	}


	/* protected: ActionBase interface */

	protected void    execute()
	  throws Throwable
	{
		session().flush();

		//~: sequentially add all the requests
		for(AggrRequest request : this.requests)
			AggrPoint.aggr(request, synch);
	}


	/* protected: action parameters */

	protected final List<AggrRequest> requests =
	  new ArrayList<AggrRequest>(8);

	protected boolean                 synch;
}