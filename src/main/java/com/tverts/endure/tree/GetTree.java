package com.tverts.endure.tree;

/* standard Java classes */

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

	public TreeDomain getDomain(Long domain, UnityType type)
	{

// from TreeDomain where (domain.id = :domain) and (treeType = :type)

		return (TreeDomain) Q(

"  from TreeDomain where (domain.id = :domain) and (treeType = :type)"

		).
		  setLong("domain",    domain).
		  setParameter("type", type).
		  uniqueResult();
	}

	public TreeDomain getDomain(Long domain, String type)
	{
		return getDomain(domain,
		  UnityTypes.unityType(TreeDomain.class, type)
		);
	}


	/* Get Tree Folders */

	public TreeFolder getFolder(Long pk)
	{
		return (TreeFolder) session().get(TreeFolder.class, pk);
	}

	public TreeFolder getFolder(Long treeDomain, String code)
	{

// from TreeFolder where (domain.id = :domain) and (code = :code)

		return (TreeFolder) Q(

"  from TreeFolder where (domain.id = :domain) and (code = :code)"

		).
		  setLong("domain", treeDomain).
		  setString("code", code).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<TreeFolder> selectFolders(TreeDomain domain)
	{

// from TreeFolder where (domain = :domain)

		return (List<TreeFolder>) Q(
		  "from TreeFolder where (domain = :domain)"
		).
		  setParameter("domain", domain).
		  list();
	}

	public List<TreeFolder> selectFolders(Long domain, String treeType)
	{
		//~: find domain
		TreeDomain td = getDomain(domain, treeType);
		if(td == null) throw EX.state(
		  "Domain [", domain, "] has no Tree Domain named [",
		  treeType, "]!"
		);

		return selectFolders(td);
	}


	/* Get Tree Items */

	public TreeItem getTreeItem(Long folder, Long unity)
	{

// from TreeItem where (folder.id = :folder) and (item.id = :unity)

		return (TreeItem) Q(

"  from TreeItem where (folder.id = :folder) and (item.id = :unity)"

		).
		  setLong("folder", folder).
		  setLong("unity",  unity).
		  uniqueResult();
	}

	public List<TreeItem> getTreeItems(TreeDomain domain, Long unity)
	{

// from TreeItem where (folder.domain = :domain) and (item.id = :unity)

		return list(TreeItem.class,

"  from TreeItem where (folder.domain = :domain) and (item.id = :unity)",

		  "domain", domain,
		  "unity",  unity
		);
	}

	public List<TreeCross> getCrossItems(TreeDomain domain, Long unity)
	{

// from TreeCross where (folder.domain = :domain) and (item.item.id = :unity)

		return list(TreeCross.class,

"  from TreeCross where (folder.domain = :domain) and (item.item.id = :unity)",

		  "domain", domain,
		  "unity",  unity
		);
	}
}