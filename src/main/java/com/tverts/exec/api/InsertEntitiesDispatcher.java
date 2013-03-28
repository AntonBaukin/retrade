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
		for(Holder h : ((InsertEntities)request).getEntities())
		{
			Object e = h.getEntity();
			if(e == null) throw new IllegalArgumentException(
			  "Insert Holder entity is undefined!");

			//!: execute insert
			Object k = ExecPoint.execute(new InsertHolder(h));

			if(!(k instanceof Long))
				throw new IllegalStateException(String.format(
				  "Holder Entity (class %s) operation must return " +
				  "Long primary key!", e.getClass().getName()
				));

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