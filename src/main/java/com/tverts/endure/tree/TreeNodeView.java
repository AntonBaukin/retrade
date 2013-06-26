package com.tverts.endure.tree;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.United;
import com.tverts.endure.cats.CodedEntity;
import com.tverts.endure.cats.NamedEntity;


/**
 * Node of Tree Domain. May be a {@link TreeFolder}
 * as intermediate node, or a {@link TreeItem} as
 * a leaf node.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "node")
public class TreeNodeView implements java.io.Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	/**
	 * Primary key of Folder (if not leaf),
	 * or Item (if leaf).
	 */
	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public Long getParentKey()
	{
		return parentKey;
	}

	public void setParentKey(Long parentKey)
	{
		this.parentKey = parentKey;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isLeaf()
	{
		return leaf;
	}

	public void setLeaf(boolean leaf)
	{
		this.leaf = leaf;
	}


	/* public: init interface */

	public TreeNodeView init(Object obj)
	{
		if(obj instanceof TreeFolder)
			return this.init((TreeFolder) obj);

		if(obj instanceof TreeItem)
			return this.init((TreeItem) obj);

		if(obj instanceof United)
			return this.init((United) obj);

		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		return this;
	}

	public TreeNodeView init(TreeFolder f)
	{
		objectKey = f.getPrimaryKey();
		parentKey = (f.getParent() == null)?(null):
		  (f.getParent().getPrimaryKey());

		code      = f.getCode();
		name      = f.getName();

		return this;
	}

	public TreeNodeView init(TreeItem i)
	{
		objectKey = i.getPrimaryKey();
		parentKey = i.getFolder().getPrimaryKey();
		leaf      = true;

		return this;
	}

	public TreeNodeView init(United u)
	{
		if(u instanceof CodedEntity)
			code = ((CodedEntity)u).getCode();

		if(u instanceof NamedEntity)
			name = ((NamedEntity)u).getName();

		return this;
	}


	/* tree node properties */

	private Long    objectKey;
	private Long    parentKey;
	private String  code;
	private String  name;
	private boolean leaf;
}