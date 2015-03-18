package com.tverts.api.clients;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * Data about a Computer
 * registered in the system.
 */
@XmlType(name = "computer")
@XmlRootElement(name = "computer")
public class Computer extends CatItem
{}