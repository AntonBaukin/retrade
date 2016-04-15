package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.TxEntity;
import com.tverts.endure.cats.CodedEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.OU;


/**
 * Receipt from Sells Desk (POS terminal) Session.
 *
 * @author anton.baukin@gmail.com
 */
public class      SellReceipt
       extends    NumericBase
       implements CodedEntity, TxEntity
{
	/* public: SellReceipt (bean) interface */

	public SellsSession getSession()
	{
		return session;
	}

	public void setSession(SellsSession session)
	{
		this.session = session;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	/**
	 * Total income from the sell operation.
	 *
	 * WARNING: this value is assigned only by
	 * {@link #payOp(SellPayOp)} method call.
	 */
	public BigDecimal getIncome()
	{
		return income;
	}

	public void setIncome(BigDecimal i)
	{
		if((i != null) && (i.scale() != 10))
			i = i.setScale(10);

		this.income = i;
	}

	public List<GoodSell> getGoods()
	{
		return goods;
	}

	public void setGoods(List<GoodSell> goods)
	{
		this.goods = goods;
	}

	public String getPayOpStr()
	{
		return payOpStr;
	}

	public void setPayOpStr(String payOpStr)
	{
		this.payOpStr = payOpStr;
	}

	/**
	 * Sell payment flag is a string of payment operation
	 * codes separated by '-'. See {@link SellPayOp#payFlag()}.
	 *
	 * WARNING: this value is assigned only by
	 * {@link #payOp(SellPayOp)} method call.
	 */
	public String getPayFlag()
	{
		return payFlag;
	}

	public void setPayFlag(String payFlag)
	{
		this.payFlag = payFlag;
	}


	/* public: payment operation */

	public SellPayOp payOp()
	{
		if((payOp == null) && (payOpStr != null))
			payOp = EX.assertn(OU.xml2obj(payOpStr, SellPayOp.class));

		return payOp;
	}

	/**
	 * Updates the payment options.
	 *
	 * WARNING: call this method when SellPayOp
	 * was changed, of the changes would be lost!
	 */
	public void payOp(SellPayOp op)
	{
		this.payOp = op;

		if(op == null)
		{
			this.payOpStr = null;
			this.payFlag  = null;
			this.income   = null;
		}
		else
		{
			this.payOpStr = OU.obj2xml(op);
			this.payFlag  = op.payFlag();
			this.income   = op.calcTotal();
		}
	}


	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}


	/* attributes & references */

	private SellsSession   session;
	private String         code;
	private Date           time;
	private BigDecimal     income;
	private List<GoodSell> goods;
	private SellPayOp      payOp;
	private String         payOpStr;
	private String         payFlag;
}