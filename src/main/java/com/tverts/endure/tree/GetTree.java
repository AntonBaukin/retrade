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
}