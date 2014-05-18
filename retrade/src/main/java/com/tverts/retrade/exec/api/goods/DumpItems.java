package com.tverts.retrade.exec.api.goods;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: endure (tree) */

import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeDomain;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: retrade api */

import com.tverts.api.core.DumpEntities;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Dumps goods Tree Item as api Tree Item.
 *
 * Note that Good Unit code and name are not written,
 * and the item key is the Good key.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpItems extends EntitiesDumperBase
{
	protected Object  createApiEntity(Object src)
	{
		com.tverts.endure.tree.TreeItem       s =
		  (com.tverts.endure.tree.TreeItem)src;
		com.tverts.api.retrade.goods.TreeItem i =
		  new com.tverts.api.retrade.goods.TreeItem();

		i.setPkey(s.getPrimaryKey());
		i.setGood(s.getItem().getPrimaryKey());
		i.setFolder(false);
		i.setTx(s.getTxn());
		i.setParent(s.getFolder().getPrimaryKey());

		return i;
	}

	protected Class   getUnityClass()
	{
		return com.tverts.endure.tree.TreeItem.class;
	}

	protected Class   getEntityClass()
	{
		return com.tverts.api.retrade.goods.TreeItem.class;
	}

	protected boolean isThatRequest(DumpEntities de)
	{
		return CMP.eq(de.getEntityClass(), getEntityClass()) &&
		  CMP.eq(de.getUnityType(), Goods.TYPE_GOOD_UNIT);
	}

	public void       setUnityType(String unityType)
	{
		throw EX.unop();
	}

	protected boolean isSelectByType(DumpEntities de)
	{
		return false;
	}

	protected void    restrictDumpDomain(QueryBuilder qb, DumpEntities de)
	{
		TreeDomain domain = bean(GetTree.class).getDomain(
		  tx().getDomain().getPrimaryKey(), Goods.TYPE_GOODS_TREE);

		EX.assertn(domain, "Domain [", tx().getDomain().getPrimaryKey(),
		  "] has no Goods Tree!"
		);

		//~: restrict by the goods tree domain of the folder
		qb.getClauseWhere().addPart(
		  "e.folder.domain = :domain"
		).
		 param("domain", domain);
	}
}