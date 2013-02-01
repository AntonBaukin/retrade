package com.tverts.api.core;

/* standard Java classes */

import java.io.Serializable;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * Dumps United entities (having Unity)
 * by their Unity Type (the leaf class
 * and the unity type name).
 *
 * The dump operation completes in a sequence
 * of requests running in separated transactions.
 *
 * To avoid problems with intermediate updates
 * of the entities is being dumped, the transaction
 * numbers range and the primary keys ordering
 * is utilized.
 */
@XmlType(name = "select-entity")
public class DumpEntities implements Serializable
{
	/**
	 * The Unity Type (leaf) class name.
	 */
	@XmlElement(name = "unity-class", required = true)
	public Class getUnityClass()
	{
		return unityClass;
	}

	public void setUnityClass(Class unityClass)
	{
		this.unityClass = unityClass;
	}

	/**
	 * The Unity Type name.
	 */
	@XmlElement(name = "unity-type", required = true)
	public String getUnityType()
	{
		return unityType;
	}

	public void setUnityType(String unityType)
	{
		this.unityType = unityType;
	}

	/**
	 * When client already has the data of previous
	 * transactions, and wants only the data delta,
	 * he sets the minimum Tx number to the oldest
	 * he has. The selection would take only the
	 * objects with Tx number greater than the minimum.
	 *
	 * Note that Tx numbers are greater than 0.
	 */
	@XmlAttribute(name = "min-tx")
	public Long getMinTx()
	{
		return minTx;
	}

	public void setMinTx(Long minTx)
	{
		this.minTx = minTx;
	}

	/**
	 * To avoid problems with concurrent updates,
	 * server uses this maximum transaction number.
	 * It selects only the entities having Tx number
	 * lower or equal to the maximum.
	 *
	 * This parameter may be undefined only when
	 * serving the first of the dump requests:
	 * the server would set the value and return it
	 * to the client in this operation instance.
	 * In the following requests the client must
	 * set the value he got.
	 */
	@XmlAttribute(name = "max-tx")
	public Long getMaxTx()
	{
		return maxTx;
	}

	public void setMaxTx(Long maxTx)
	{
		this.maxTx = maxTx;
	}

	/**
	 * When selecting, the entities are ordered by
	 * the primary key value. This helps to avoid
	 * problems of updated entities: they would
	 * have the Tx number set greater than the dump
	 * maximum Tx number, and "disappear" from the
	 * resulting set. If plain SQL skip (Hibernate
	 * first result) technique is used, some entities
	 * might be lost as the selecting window slide right.
	 *
	 * The default value is 0, and all the entities
	 * have the keys greater than 0.
	 *
	 * Select condition takes the entities with the
	 * keys greater than this parameter.
	 *
	 * To get the value for the next request just
	 * take the key of the last entity returned
	 * in the previous request.
	 */
	@XmlAttribute(name = "min-pkey")
	public long getMinPkey()
	{
		return minPkey;
	}

	public void setMinPkey(long minPkey)
	{
		this.minPkey = minPkey;
	}


	/* select parameters */

	private Class  unityClass;
	private String unityType;

	private Long   minTx;
	private Long   maxTx;
	private long   minPkey;
}