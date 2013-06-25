package com.tverts.endure.tree;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;


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
}