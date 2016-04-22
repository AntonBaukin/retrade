package com.tverts.model.store;

/* Java */

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxBean;


/**
 * Persistent backend for the UI Model Beans stored
 * in the database in the {@link ModelRecord} objects.
 *
 * @author anton.baukin@gmail.com.
 */
public class HiberModelsStore extends ModelsBackendBase
{
	/* Models Backend */

	public void find(final ModelEntry e)
	{
		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				//~: load bean bytes from the database
				byte[] bytes = bean(GetModelRecord.class).load(e);

				if(bytes != null)
				{
					//~: decode the bean
					e.bean = restore(bytes);

					//~: mark as loaded
					e.loaded = true;
				}
			}
		});
	}

	public void store(Collection<ModelEntry> es)
	{
		//~: encode the beans
		final HashMap<ModelEntry, byte[]> ebs =
		  new HashMap<ModelEntry, byte[]>(es.size());
		for(ModelEntry e : es)
			ebs.put(e, store(e.bean));

		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				//~: save the records
				bean(GetModelRecord.class).save(ebs);
			}
		});
	}

	public void remove(final ModelEntry e)
	{
		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				//~: remove the record
				bean(GetModelRecord.class).
				  remove(Collections.singleton(e.key));
			}
		});
	}
}