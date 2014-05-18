package com.tverts.retrade.domain.invoice;

/* com.tverts: endure */

import com.tverts.endure.EntityState;
import com.tverts.endure.UnitedBase;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * Simplest state of {@link Invoice} instance.
 * The latter may have only one state at a time.
 *
 * @author anton.baukin@gmail.com
 */
public class      InvoiceState
       extends    UnitedBase
       implements EntityState
{
	/* public: EntityState interface */

	public UnityType getStateType()
	{
		return (getUnity() == null)?(null):
		  (getUnity().getUnityType());
	}


	/* public: InvoiceState bean interface */

	public Invoice   getInvoice()
	{
		return invoice;
	}

	public void      setInvoice(Invoice invoice)
	{
		this.invoice = invoice;
	}


	/* links to the invoice */

	private Invoice invoice;
}