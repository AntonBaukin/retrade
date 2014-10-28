package com.tverts.retrade.domain.goods;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.goods.GoodsTreeModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean for table with views on folders
 * and their goods (in separated table).
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "goods-tree-model")
public class GoodsTreeModelBean extends GoodsModelBean
{
	/* Goods Tree Model (bean) */

	/**
	 * Tree Domain this model is for.
	 * Must be always defined!
	 */
	public Long getTreeDomain()
	{
		return treeDomain;
	}

	public void setTreeDomain(Long treeDomain)
	{
		this.treeDomain = treeDomain;
	}

	/**
	 * Currently selected Tree Folder.
	 */
	public Long getCurrentFolder()
	{
		return currentFolder;
	}

	public void setCurrentFolder(Long currentFolder)
	{
		this.currentFolder = currentFolder;
	}

	public boolean isWithSubFolders()
	{
		return withSubFolders;
	}

	public void setWithSubFolders(boolean withSubFolders)
	{
		this.withSubFolders = withSubFolders;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new GoodsTreeModelData(this);
	}


	/* private: encapsulated data */

	private Long    treeDomain;
	private Long    currentFolder;
	private boolean withSubFolders;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.longer(o, treeDomain);
		IO.longer(o, currentFolder);
		o.writeBoolean(withSubFolders);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		treeDomain     = IO.longer(i);
		currentFolder  = IO.longer(i);
		withSubFolders = i.readBoolean();
	}
}