package com.tverts.retrade.exec.datas.goods;

/* com.tverts: data sources */

import com.tverts.data.DataCtx;
import com.tverts.data.DataSourceBase;

/* com.tverts: models */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Data Source that provides data with all
 * the Good Units in the system: gross
 * volumes and the rest costs.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class DatasGoodsVolumesRestCosts extends DataSourceBase
{
	/* public: Data Source (data) */

	public ModelBean createModel(DataCtx ctx)
	{
		return null;
	}

	public Object    provideData(ModelBean m)
	{
		throw EX.unop();
	}

	public Object    provideData(DataCtx ctx)
	{
		GoodsModelBean mb = new GoodsModelBean();

		//~: domain
		mb.setDomain(ctx.getDomain());

		//~: unlimited
		mb.setDataLimit(Integer.MAX_VALUE);

		return mb.modelData();
	}

	public String    getUiPath()
	{
		return null;
	}

	public void      setUiPath(String uiPath)
	{
		throw EX.unop();
	}
}