package com.tverts.endure.tree;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;


/**
 * Connects {@link TreeItem} link with all
 * the folders from the item's to the root.
 *
 * @author anton.baukin@gmail.com
 */
public class TreeCross extends NumericBase
{
	/* public: bean interface */

	public TreeFolder getFolder()
	{
		return folder;
	}

	public void setFolder(TreeFolder folder)
	{
		this.folder = folder;
	}

	public TreeItem getItem()
	{
		return item;
	}

	public void setItem(TreeItem item)
	{
		this.item = item;
	}


	/* cross links */

	private TreeFolder folder;
	private TreeItem   item;
}