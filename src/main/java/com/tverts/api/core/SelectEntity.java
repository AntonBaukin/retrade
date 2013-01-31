package com.tverts.api.core;

/* standard Java classes */

import java.io.Serializable;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * Selects United entities (having Unity)
 * by their Unity Type (the leaf class
 * and the unity type name).
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(name = "select-entity")
public class SelectEntity implements Serializable
{



	/* select parameters */

	private Class  unityClass;
	private String unityType;
}