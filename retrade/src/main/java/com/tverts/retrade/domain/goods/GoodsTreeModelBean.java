package com.tverts.retrade.domain.goods;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

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
public class GoodsTreeModelBean extends GoodsModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

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


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new GoodsTreeModelData(this);
	}


	/* model attribute */

	private Long    currentFolder;
	private boolean withSubFolders;
}