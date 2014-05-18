package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* com.tverts: retrade domain (payments) */

import com.tverts.retrade.domain.payment.PayOrder;


/**
 * Session of selling goods from a POS terminal (Sells Desk).
 * Sells Session refers both the Sells Desk, and the
 * Payment Desk assigned to it at the session time.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SellsSession extends PayOrder
{
	/* public: SellsSession (bean) interface */

	public Date getCloseTime()
	{
		return closeTime;
	}

	public void setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	public SellsDesk getSellsDesk()
	{
		return sellsDesk;
	}

	public void setSellsDesk(SellsDesk sellsDesk)
	{
		this.sellsDesk = sellsDesk;
	}

	public PayDesk getPayDesk()
	{
		return payDesk;
	}

	public void setPayDesk(PayDesk payDesk)
	{
		this.payDesk = payDesk;
	}

	public List<SellReceipt> getReceipts()
	{
		if(receipts == null)
			receipts = new ArrayList<SellReceipt>(0);
		return receipts;
	}

	public void setReceipts(List<SellReceipt> receipts)
	{
		this.receipts = receipts;
	}


	/* sells desk & payments desk */

	private Date              closeTime;
	private SellsDesk         sellsDesk;
	private PayDesk           payDesk;
	private List<SellReceipt> receipts;
}