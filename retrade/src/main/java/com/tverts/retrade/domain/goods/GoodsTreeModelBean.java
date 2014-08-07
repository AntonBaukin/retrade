package com.tverts.retrade.domain.goods;

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
public class GoodsTreeModelBean extends GoodsModelBean
{
	public static final long serialVersionUID = 0L;


	/* Goods Tree Model Bean */

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