package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * A Trade Store (or its equivalent)
 * containing the goods buy-sell-move
 * operations.
 */
@XmlType(name = "store")
public class Store extends CatItem
{
	public static final long serialVersionUID = 0L;
}