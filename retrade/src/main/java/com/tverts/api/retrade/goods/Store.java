package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * A Trade Store (or its equivalent)
 * containing the goods buy-sell-move
 * operations.
 */
@XmlType(name = "store")
@XmlRootElement(name = "store")
public class Store extends CatItem
{}