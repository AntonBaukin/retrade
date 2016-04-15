package com.tverts.retrade.exec.api.goods;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: endure (tree) */

import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeDomain;
import com.tverts.endure.tree.TreeFolder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: retrade api */

import com.tverts.api.core.DumpEntities;
import com.tverts.api.retrade.goods.TreeItem;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps goods {@link TreeFolder} as {@link TreeItem}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpFolders extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		TreeFolder f = (TreeFolder)src;
		TreeItem   i = new TreeItem();

		i.setFolder(true);
		i.setPkey(f.getPrimaryKey());
		i.setTx(f.getTxn());
		i.setCode(f.getCode());
		i.setName(f.getName());

		if(f.getParent() != null)
			i.setParent(f.getParent().getPrimaryKey());

		return i;
	}

	protected Class  getUnityClass()
	{
		return TreeFolder.class;
	}

	protected Class  getEntityClass()
	{
		return TreeItem.class;
	}

	public String    getUnityType()
	{
		return Goods.TYPE_GOODS_FOLDER;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}

	protected void   restrictDumpDomain(QueryBuilder qb, DumpEntities de)
	{
		TreeDomain domain = bean(GetTree.class).getDomain(
		  tx().getDomain().getPrimaryKey(), Goods.TYPE_GOODS_TREE, null);

		EX.assertn(domain, "Domain [", tx().getDomain().getPrimaryKey(),
		  "] has no Goods Tree!"
		);

		//~: restrict by the goods tree domain
		qb.getClauseWhere().addPart(
		  "e.domain = :domain"
		).
		 param("domain", domain);
	}
}