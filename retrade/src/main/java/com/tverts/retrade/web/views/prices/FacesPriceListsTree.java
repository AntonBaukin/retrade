package com.tverts.retrade.web.views.prices;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: endure (trees) */

import com.tverts.endure.tree.TreeNodeView;

/* com.tverts: retrade faces views */

import com.tverts.retrade.web.views.goods.FacesGoodsView;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodsTreeModelBean;
import com.tverts.retrade.domain.prices.ActPriceListsTreeUpdate;
import com.tverts.retrade.domain.prices.PriceList;
import com.tverts.retrade.domain.prices.PriceListsTreeModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of the Price Lists tree.
 *
 * @author anton.baukin@gmail.com.
 */
@ManagedBean @RequestScoped
public class FacesPriceListsTree extends FacesGoodsView
{
	/* actions */

	public String doSetCurrentList()
	{
		String    pk = SU.s2s(request().getParameter("currentList"));
		PriceList pl = ((pk == null) || "$".equals(pk))?(null):
		  loadList(Long.parseLong(pk));

		//~: update the model
		getModel().setCurrentList((pl == null)?(null):(pl.getPrimaryKey()));

		return null;
	}

	public String doCommitTreeEdit()
	{
		List<TreeNodeView> views = new ArrayList<TreeNodeView>(4);

		//~: create the nodes
		for(int i = 0;;i++)
		{
			String key = SU.s2s(request().getParameter("treeListKey" + i));
			if(key == null) break;

			TreeNodeView n = new TreeNodeView();
			views.add(n);

			//~: folder key
			n.setObjectKey(key);

			//~: folder code
			n.setCode(SU.s2s(request().getParameter("treeListCode" + i)));

			//~: folder name
			n.setName(SU.s2s(request().getParameter("treeListName" + i)));

			//~: folder parent
			n.setParentKey(SU.s2s(request().getParameter("treeListParent" + i)));
		}

		//?: {nothing to do}
		if(views.isEmpty()) return null;

		//~: load all the lists
		List<PriceList> lists = bean(GetGoods.class).
		  getPriceLists(getModel().domain());

		//~: take their primary keys
		Set<Long> lkeys = new HashSet<Long>(lists.size());
		for(PriceList l : lists) lkeys.add(l.getPrimaryKey());

		//sec: check the lists are correct
		for(TreeNodeView n : views)
		{
			//?: {try add root node}
			EX.assertx(!"$".equals(n.getObjectKey()));

			//?: {it is not a new instance}
			if(!n.getObjectKey().startsWith("$")) EX.assertx(
			  lkeys.contains(Long.parseLong(n.getObjectKey())),
			  "Wrong Price List [", n.getObjectKey(), "]!"
			);

			//~: replace root node for parents with null
			if("$".equals(n.getParentKey()))
				n.setParentKey(null);

			//~: the same for parent
			if((n.getParentKey() != null) && !n.getParentKey().startsWith("$"))
				EX.assertx( lkeys.contains(Long.parseLong(n.getParentKey())),
				  "Wrong Price List [", n.getObjectKey(), "]!"
				);
		}

		//!: issue update action
		actionRun(ActPriceListsTreeUpdate.UPDATE, loadDomain(),
		  ActPriceListsTreeUpdate.LISTS, views
		);

		return null;
	}


	/* public: view interface */

	public PriceListsTreeModelBean getModel()
	{
		return (PriceListsTreeModelBean) super.getModel();
	}


	/* protected: actions support */

	protected PriceList loadList(Long pk)
	{
		//~: get it
		PriceList pl = EX.assertn(
		  bean(GetGoods.class).getPriceList(pk),
		  "Price List not found!"
		);

		//sec: list of the same domain
		if(!pl.getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid("Price List of else Domain!");

		return pl;
	}



	/* protected: ModelView interface */

	protected PriceListsTreeModelBean createModel()
	{
		PriceListsTreeModelBean mb = new PriceListsTreeModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof PriceListsTreeModelBean);
	}
}