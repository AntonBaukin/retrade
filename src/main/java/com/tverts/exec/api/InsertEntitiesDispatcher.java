package com.tverts.exec.api;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: execution */

import com.tverts.api.core.TwoKeysObject;
import com.tverts.exec.ExecError;
import com.tverts.exec.ExecPoint;
import com.tverts.exec.ExecutorBase;

/* com.tverts: api */

import com.tverts.api.core.Holder;
import com.tverts.api.core.InsertEntities;
import com.tverts.api.core.XKeys;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dispatches API request {@link InsertEntities}
 * into updating the entities list given.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class InsertEntitiesDispatcher extends ExecutorBase
{
	/* public: Executor interface */

	public Object execute(Object request)
	  throws ExecError
	{
		if(!(request instanceof InsertEntities))
			return null;

		List<XKeys> keys = new ArrayList<XKeys>(
		  ((InsertEntities)request).getEntities().size());

		//c: execute each holder of the update list
		Object ctx = null;
		for(Holder h : ((InsertEntities)request).getEntities())
		{
			Object       e = EX.assertn(h.getEntity(), "Insert Holder entity is undefined!");
			InsertHolder i = new InsertHolder().setHolder(h).setContext(ctx);

			//!: execute insert
			Object k = ExecPoint.execute(i);
			ctx = i.getContext();

			if(!(k instanceof Long)) throw EX.state(
			  "Holder Entity (class ", e.getClass().getName(),
			  ") operation must return Long primary key!"
			);

			//~: add the keys pair
			XKeys x = new XKeys();

			//~: primary key
			x.setPkey((Long)k);

			//?: {an 2-keys entity}
			if(e instanceof TwoKeysObject)
				x.setXkey(((TwoKeysObject)e).getXkey());

			keys.add(x);
		}

		return keys;
	}
}