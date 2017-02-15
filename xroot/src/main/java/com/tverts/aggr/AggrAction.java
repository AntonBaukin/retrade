package com.tverts.aggr;

/* Java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.flush;

/* com.tverts: actions */

import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;


/**
 * Action to send one or more aggregation requests
 * for the delayed execution by the service.
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


	/* Aggregate Action */

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

	protected final List<AggrRequest> requests =
	  new ArrayList<>(2);


	/* Action */

	public Object     getResult()
	{
		return null;
	}


	/* protected: Action Base */

	protected void    execute()
	  throws Throwable
	{
		flush(session());

		//~: sequentially add all the requests
		for(AggrRequest request : this.requests)
			AggrPoint.aggr(request);
	}
}