package com.tverts.api.core;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Adds {@link TxObject} to {@link JustObject}.
 */
@XmlType(name = "just-tx-object", propOrder = { "tx" })
public abstract class JustTxObject
       extends        JustObject
       implements     TxObject
{
	/**
	 * The transaction number assigned
	 * by the primary database.
	 */
	@XmlAttribute
	public Long getTx()
	{
		return (tx == 0L)?(null):(tx);
	}

	private long tx;

	public void setTx(Long tx)
	{
		this.tx = (tx == null)?(0L):(tx);
	}
}