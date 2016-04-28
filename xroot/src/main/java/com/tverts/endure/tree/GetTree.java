package com.tverts.endure.tree;

/* Java */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Loads {@link TreeDomain}, {@link TreeFolder},
 * and {@link TreeItem} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getTree")
public class GetTree extends GetObjectBase
{
	/* Get Tree Domains */

	public TreeDomain getDomain(Long pk)
	{
		return get(TreeDomain.class, pk);
	}

	public TreeDomain getDomain(Long domain, UnityType type, Long owner)
	{
		if(owner == null)
			return object(TreeDomain.class, q("TD-n"),
			  "domain", domain, "type", type);


		return object(TreeDomain.class, q("TD-y"),
		  "domain", domain, "type", type, "owner", owner);
	}

	public TreeDomain getDomain(Long domain, String type, Long owner)
	{
		return getDomain(domain, UnityTypes.unityType(TreeDomain.class, type), owner);
	}


	/* Get Tree Folders */

	public TreeFolder getFolder(Long pk)
	{
		return (TreeFolder) session().get(TreeFolder.class, pk);
	}

	public TreeFolder getFolder(Long treeDomain, String code)
	{
		return object(TreeFolder.class, q("TF-tdc"),
		  "domain", treeDomain, "code", code);
	}

	public List<TreeFolder> selectFolders(TreeDomain domain)
	{
		return list(TreeFolder.class, q("TF-d"),
		  "domain", EX.assertn(domain));
	}


	/* Get Tree Items */

	public TreeItem getTreeItem(Long folder, Long unity)
	{
		return object(TreeItem.class, q("TI-fu"),
		  "folder", folder, "unity", unity);
	}

	public List<TreeItem> getTreeItems(TreeDomain domain, Long unity)
	{
		return list(TreeItem.class, q("TI-du"),
		  "domain", domain, "unity", unity);
	}

	public List<TreeCross> getCrossItems(TreeDomain domain, Long unity)
	{
		return list(TreeCross.class, q("TC-du"),
		  "domain", domain, "unity",  unity);
	}

	public List<TreeCross> getCrossItems(TreeItem item)
	{
		return list(TreeCross.class, q("TC-i"), "item", item);
	}
}