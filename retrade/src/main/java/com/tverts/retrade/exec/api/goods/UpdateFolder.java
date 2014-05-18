package com.tverts.retrade.exec.api.goods;

/* standard Java classes */

import java.util.Collections;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: endure (trees) */

import com.tverts.endure.tree.ActTreeDomain;
import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeNodeView;
import com.tverts.endure.tree.TreeFolder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: retrade api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.goods.TreeItem;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Updates  existing {@link TreeFolder}
 * from the given API {@link TreeItem}.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateFolder extends UpdateEntityBase
{
	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof TreeItem) &&
		  Goods.TYPE_GOODS_FOLDER.equals(holder.getTypeName());
	}

	protected Class   getUnityClass(Holder holder)
	{
		return TreeFolder.class;
	}

	protected void    update(Object entity, Object source)
	{
		TreeFolder   f = (TreeFolder) entity;
		TreeItem     i = (TreeItem) source;
		TreeNodeView v = new TreeNodeView().init(f);

		//~: code
		v.setCode(i.getCode());

		//~: name
		v.setName(i.getName());

		//~: parent
		if((i.getXParent() == null) && (i.getParent() == null))
			v.setParentKey(null);
		else
			assignParentFolder(f, i, v);


		//!: update action
		ActionsPoint.actionRun(ActTreeDomain.UPDATE_FOLDERS, f.getDomain(),
		  ActTreeDomain.PARAM_FOLDERS, Collections.singletonList(v),
		  ActTreeFolder.PARAM_TYPE, Goods.TYPE_GOODS_FOLDER
		);
	}


	/* protected: support */

	protected void assignParentFolder(TreeFolder f, TreeItem i, TreeNodeView v)
	{
		//?: {no parent key}
		EX.assertn(i.getParent(), "Folder p-key [", f.getPrimaryKey(),
		  "] x-key [", i.getXkey(), "] refers unknown parent folder [",
		  i.getXParent(), "]!"
		);

		TreeFolder p = bean(GetTree.class).getFolder(i.getParent());
		EX.assertn(p, "Goods Tree Folder [", i.getParent(), "] not found!");

		//sec: check the domain equals
		EX.assertx(p.getDomain().equals(f.getDomain()));

		//!: assign the parent folder to the edit view
		v.setParentKey(p.getPrimaryKey().toString());
	}
}