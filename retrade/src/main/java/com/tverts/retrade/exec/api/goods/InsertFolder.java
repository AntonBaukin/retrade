package com.tverts.retrade.exec.api.goods;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.exec.api.InsertEntityBase;
import com.tverts.exec.api.InsertHolder;

/* com.tverts: endure (trees) */

import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeFolder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: retrade api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.goods.TreeItem;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Saves new {@link TreeFolder} from the given
 * API {@link TreeItem}.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertFolder extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof TreeItem) &&
		  Goods.TYPE_GOODS_FOLDER.equals(holder.getTypeName());
	}

	protected Long    insert(InsertHolder h)
	{
		TreeItem   i = (TreeItem) h.getHolder().getEntity();
		TreeFolder f = new TreeFolder();

		//~: goods tree domain
		f.setDomain(EX.assertn( bean(GetTree.class).
		  getDomain(domain().getPrimaryKey(), Goods.TYPE_GOODS_TREE, null),
		  "Domain [", domain().getPrimaryKey(), "] has no Goods Tree!"
		));

		//~: code
		f.setCode(EX.assertn(i.getCode()));

		//~: name
		f.setName(EX.assertn(i.getName()));

		//~: parent folder reference
		if(i.getXParent() != null)
			assignParentFolder(f, h);

		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, f,
		  ActTreeFolder.PARAM_TYPE, Goods.TYPE_GOODS_FOLDER);

		//~: map the primary key to the context
		getKeysMap(h).put(i.getXkey(), f.getPrimaryKey());

		return f.getPrimaryKey();
	}


	/* protected: support */

	protected void assignParentFolder(TreeFolder f, InsertHolder h)
	{
		//~: obtain key of the parent folder
		TreeItem i = (TreeItem) h.getHolder().getEntity();
		Long     k = i.getParent();

		//?: {has no parent key} lookup from the context of just inserted
		if(k == null) k = EX.assertn(
		  getKeysMap(h).get(i.getXParent()),
		  "Tree Folder [", i.getXkey(), "] refers parent Folder [",
		  i.getXParent(), "] that is not inserted yet!"
		);

		TreeFolder p = bean(GetTree.class).getFolder(k);
		EX.assertn(p, "Goods Tree Folder [", k, "] not found!");

		//sec: check the domain equals
		EX.assertx(p.getDomain().equals(f.getDomain()));

		//!: assign the parent folder
		f.setParent(p);
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Long> getKeysMap(InsertHolder h)
	{
		Map<String, Long> m = (Map<String, Long>) h.getContext();
		if(m == null) h.setContext(m = new HashMap<String, Long>(17));
		return m;
	}
}
