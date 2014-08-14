package com.tverts.retrade.web.views.goods;

/* Java */

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

import com.tverts.endure.tree.ActTreeDomain;
import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeDomain;
import com.tverts.endure.tree.TreeFolder;
import com.tverts.endure.tree.TreeNodeView;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodsTreeModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.SU.s2s;


/**
 * The view of the Good Units tree.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesGoodsTreeView extends FacesGoodsView
{
	/* Actions */

	public String doSetCurrentFolder()
	{
		String     pk = s2s(request().getParameter("currentFolder"));
		TreeFolder tf = ((pk == null) || "$".equals(pk))?(null):
		  loadFolder(Long.parseLong(pk));

		//~: update the model
		getModel().setCurrentFolder((tf == null)?(null):(tf.getPrimaryKey()));

		//~: with sub folders
		getModel().setWithSubFolders(
		  "true".equals(request().getParameter("withSubFolders"))
		);

		return null;
	}

	public String doCommitTreeEdit()
	{
		List<TreeNodeView> views = new ArrayList<TreeNodeView>(4);

		//~: create the nodes
		for(int i = 0;;i++)
		{
			String key = s2s(request().getParameter("treeFolderKey" + i));
			if(key == null) break;

			TreeNodeView n = new TreeNodeView();
			views.add(n);

			//~: folder key
			n.setObjectKey(key);

			//~: folder code
			n.setCode(s2s(request().getParameter("treeFolderCode" + i)));

			//~: folder name
			n.setName(s2s(request().getParameter("treeFolderName" + i)));

			//~: folder parent
			n.setParentKey(s2s(request().getParameter("treeFolderParent" + i)));
		}

		//?: {nothing to do}
		if(views.isEmpty()) return null;

		//~: get the goods Tree Domain
		TreeDomain td = EX.assertn(bean(GetTree.class).
			 getDomain(getModel().getTreeDomain()));

		//~: load all the folders in the current session
		List<TreeFolder> folders =
		  bean(GetTree.class).selectFolders(td);

		//~: take their primary keys
		Set<Long> fkeys = new HashSet<Long>(folders.size());
		for(TreeFolder f : folders) fkeys.add(f.getPrimaryKey());

		//sec: check the folders are correct
		for(TreeNodeView n : views)
		{
			//?: {try add root node}
			if("$".equals(n.getObjectKey()))
				throw EX.arg();

			//?: {it is not a new instance}
			if(!n.getObjectKey().startsWith("$"))
				if(!fkeys.contains(Long.parseLong(n.getObjectKey())))
					throw EX.arg("Wrong Folder [", n.getObjectKey(), "]!");

			//~: replace root node for parents with null
			if("$".equals(n.getParentKey()))
				n.setParentKey(null);

			//~: the same for parent
			if((n.getParentKey() != null) && !n.getParentKey().startsWith("$"))
				if(!fkeys.contains(Long.parseLong(n.getParentKey())))
					throw EX.arg("Wrong Folder [", n.getObjectKey(), "]!");
		}


		//!: run tree update action
		actionRun(ActTreeDomain.UPDATE_FOLDERS, td,
		  ActTreeDomain.PARAM_FOLDERS, views,
		  ActTreeFolder.PARAM_TYPE, Goods.TYPE_GOODS_FOLDER
		);

		return null;
	}

	public String doMoveSelectedGoods()
	{
		//~: load current folder
		String     pk = s2s(request().getParameter("destinationFolder"));
		TreeFolder tf = (pk == null)?(null):loadFolder(Long.parseLong(pk));
		if(tf == null) throw EX.arg("No destination Tree Folder is given!");

		//~: selection set
		String selset = s2s(request().getParameter("selset"));
		if(selset == null) selset = "";

		//~: the goods selected
		List<GoodUnit> goods = bean(GetGoods.class).
		  getSelectedGoodUnits(tf.getDomain().getDomain().getPrimaryKey(), selset);

		boolean copy = "true".equals(request().getParameter("copyGoods"));

		//c: process the goods
		for(GoodUnit g : goods)
		{
			//?: {moving the goods} remove them
			if(!copy) actionRun(
			  ActTreeDomain.DELETE_ITEM, tf.getDomain(),
			  ActTreeDomain.PARAM_ITEM, g
			);

			//~: add the good
			actionRun(ActTreeFolder.ADD, tf, ActTreeFolder.PARAM_ITEM, g);
		}

		return null;
	}


	/* View */

	public GoodsTreeModelBean getModel()
	{
		return (GoodsTreeModelBean) super.getModel();
	}


	/* protected: actions support */

	protected TreeFolder loadFolder(Long pk)
	{
		//~: get it
		TreeFolder tf = bean(GetTree.class).getFolder(pk);

		//?: {not found it}
		if(tf == null) throw EX.state("Folder not found!");

		//?: {not that type}
		if(!Goods.TYPE_GOODS_FOLDER.equals(tf.getUnity().getUnityType().getTypeName()))
			throw EX.state("Illegal Folder type!");

		//sec: folder of the same domain
		if(!tf.getDomain().getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid("Folded of else Domain!");

		return tf;
	}


	/* protected: ModelView interface */

	protected GoodsTreeModelBean createModel()
	{
		GoodsTreeModelBean mb = createModelInstance();

		//=: domain
		mb.setDomain(getDomainKey());

		//=: tree domain
		mb.setTreeDomain(EX.assertn(provideTreeDomian()));

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodsTreeModelBean);
	}

	protected GoodsTreeModelBean createModelInstance()
	{
		return new GoodsTreeModelBean();
	}

	protected Long provideTreeDomian()
	{
		return bean(GetTree.class).getDomain(getDomainKey(),
		  Goods.TYPE_GOODS_TREE, null).getPrimaryKey();
	}
}