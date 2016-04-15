package com.tverts.model.store;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelInfo;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Implementation base for a Model Bean keys generator.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelKeysGenBase
       implements     ModelKeysGen
{
	/* Model Keys Generator */

	public String genModelKey(ModelBean bean)
	{
		EX.assertn(bean, "Model Bean instance is undefined!");
		return catModelKey(selectKeyPrefix(bean), nextModelKey(bean));
	}


	/* protected: generation details */

	protected abstract String nextModelKey(ModelBean bean);

	protected String          catModelKey(String p, String k)
	{
		return SU.cats(p, "-", k);
	}

	protected String          selectKeyPrefix(ModelBean bean)
	{
		String    p = null;
		ModelInfo i = bean.modelInfo();

		//?: {has model info}
		if(i != null)
			p = i.keysPrefix;

		return (p != null)?EX.asserts(p):(bean.getClass().getSimpleName());
	}
}