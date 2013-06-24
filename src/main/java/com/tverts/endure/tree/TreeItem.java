package com.tverts.endure.tree;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.Unity;


/**
 * Tree Folder Item is a reference to
 * Unity object that assigned to that folder.
 *
 * The same Unity instance in a folder may be
 * referred only once.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TreeItem extends NumericBase
{
	/* public: TreeItem interface */

	public TreeFolder getFolder()
	{
		return folder;
	}

	public void setFolder(TreeFolder folder)
	{
		this.folder = folder;
	}

	public Unity getItem()
	{
		return item;
	}

	public void setItem(Unity item)
	{
		this.item = item;
	}


	/* folder and unity references */

	private TreeFolder folder;
	private Unity      item;
}