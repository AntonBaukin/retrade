package com.tverts.retrade.domain.goods;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.goods.GoodsTreeModelData;


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
	public static final long serialVersionUID = 0L;


	/* Goods Tree Model Bean */

	/**
	 * Tree Domain this model is for.
	 * Must be always defined!
	 */
	public Long getTreeDomain()
	{
		return treeDomain;
	}

	private Long treeDomain;

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

	private Long currentFolder;

	public void setCurrentFolder(Long currentFolder)
	{
		this.currentFolder = currentFolder;
	}

	public boolean isWithSubFolders()
	{
		return withSubFolders;
	}

	private boolean withSubFolders;

	public void setWithSubFolders(boolean withSubFolders)
	{
		this.withSubFolders = withSubFolders;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new GoodsTreeModelData(this);
	}
}