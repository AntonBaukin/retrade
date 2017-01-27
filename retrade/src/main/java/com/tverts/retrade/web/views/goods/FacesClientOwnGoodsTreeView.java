package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (trees) */

import com.tverts.endure.tree.GetTree;

/* com.tverts: retrade domain (goods + firms) */

import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Back bean of the page with the Goods Tree
 * maintained by the client organization.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesClientOwnGoodsTreeView extends FacesClientGoodsTreeView
{
	/* protected: ModelView interface */

	protected Long provideTreeDomian()
	{
		Contractor c = EX.assertn(bean(GetContractor.class).
		  getContractorFirm(SecPoint.clientFirmKeyStrict()));

		return bean(GetTree.class).getDomain(getDomainKey(),
		  Goods.TYPE_GOODS_TREE, c.getPrimaryKey()).
		  getPrimaryKey();
	}
}