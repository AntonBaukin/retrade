package com.tverts.retrade.domain.invoice;

/* Java */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: retrade domain (goods + trade stores) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.MeasureUnit;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.store.StoreGood;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Refers {@link InvGood} or {@link StoreGood}
 * by the good index in the invoice.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good")
@XmlType(name = "invoice-good-view")
public class InvoiceGoodView implements Serializable
{
	public static final long serialVersionUID = 20140806;


	/* Invoice Good (bean) */

	public Long getObjectKey()
	{
		return objectKey;
	}

	private Long objectKey;

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public int getIndex()
	{
		return index;
	}

	private int index;

	public void setIndex(int index)
	{
		this.index = index;
	}

	public Long getGoodUnit()
	{
		return goodUnit;
	}

	private Long goodUnit;

	@XmlElement
	public String getGoodCode()
	{
		return goodCode;
	}

	private String goodCode;

	@XmlElement
	public String getGoodName()
	{
		return goodName;
	}

	private String goodName;

	@XmlElement
	public String getVolumeUnitName()
	{
		return volumeUnitName;
	}

	private String volumeUnitName;

	@XmlElement
	public boolean isVolumeInteger()
	{
		return volumeInteger;
	}

	private boolean volumeInteger;

	public BigDecimal getGoodVolume()
	{
		return goodVolume;
	}

	private BigDecimal goodVolume;

	public void setGoodVolume(BigDecimal v)
	{
		if(v != null) if(this.volumeInteger)
			v = v.setScale(0);
		else
			v = v.setScale(3);

		this.goodVolume = v;
	}

	public BigDecimal getVolumeCost()
	{
		return volumeCost;
	}

	private BigDecimal volumeCost;

	public void setVolumeCost(BigDecimal v)
	{
		if(v != null) v = v.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.volumeCost = v;
	}

	@XmlElement
	public BigDecimal getUnitCost()
	{
		return ((volumeCost == null) || (goodVolume == null))?(null):
		  (volumeCost.divide(goodVolume, 2, BigDecimal.ROUND_HALF_EVEN));
	}

	public BigDecimal getGoodVolumeDelta()
	{
		return goodVolumeDelta;
	}

	private BigDecimal goodVolumeDelta;

	public void setGoodVolumeDelta(BigDecimal goodVolumeDelta)
	{
		this.goodVolumeDelta = goodVolumeDelta;
	}

	@XmlElement
	public Long getGoodPrice()
	{
		return goodPrice;
	}

	private Long goodPrice;

	public Long getPriceList()
	{
		return priceList;
	}

	private Long priceList;

	public void setPriceList(Long priceList)
	{
		this.priceList = priceList;
	}

	@XmlElement
	public String getPriceListCode()
	{
		return priceListCode;
	}

	private String priceListCode;

	@XmlElement
	public String getPriceListName()
	{
		return priceListName;
	}

	private String priceListName;

	public Boolean getMoveOn()
	{
		return moveOn;
	}

	private Boolean moveOn;

	public void setMoveOn(Boolean moveOn)
	{
		this.moveOn = moveOn;
	}

	public Boolean getNeedCalc()
	{
		return needCalc;
	}

	private Boolean needCalc;

	public void setNeedCalc(Boolean needCalc)
	{
		this.needCalc = needCalc;
	}

	@XmlElement
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getCalcDate()
	{
		return calcDate;
	}

	private Date calcDate;

	@XmlElement
	public Boolean getGoodSemiReady()
	{
		return goodSemiReady;
	}

	private Boolean goodSemiReady;


	/* Initialization */

	public InvoiceGoodView init(Invoice invoice, int index)
	{
		EX.assertn(invoice.getInvoiceData());
		EX.assertx(
		  invoice.getInvoiceData().getGoods().size() > index,

		  "No enough Goods in Invoice [", invoice.getPrimaryKey(),
		  "], index was [", index, "]!"
		);

		//?: {is fixed invoice}
		if(Invoices.isInvoiceFixed(invoice))
			this.initFixedStateGood(invoice, index);

		//~: the index (+1)
		this.index = index + 1;

		//~: take the invoice good by the index
		return this.init(invoice.getInvoiceData().getGoods().get(index));
	}

	public InvoiceGoodView init(int index)
	{
		this.index = index + 1; //<-- +1
		return this;
	}

	public InvoiceGoodView init(InvGood g)
	{
		//~: good key
		this.objectKey = g.getPrimaryKey();

		//~: good unit
		this.init(g.getGoodUnit());

		//~: volume
		setGoodVolume(g.getVolume());

		//?: {buy good}
		if(g instanceof BuyGood)
			this.init((BuyGood) g);

		//?: {sell good}
		if(g instanceof SellGood)
			this.init((SellGood) g);

		//?: {move good}
		if(g instanceof MoveGood)
			this.init((MoveGood) g);

		//?: {result good}
		if(g instanceof ResGood)
			this.init((ResGood) g);

		//~: need calculation
		if(g instanceof NeedCalcGood)
			this.needCalc = ((NeedCalcGood)g).getNeedCalc();

		//~: good unit
		return this;
	}

	public InvoiceGoodView init(GoodUnit gu)
	{
		//~: good key
		this.goodUnit = gu.getPrimaryKey();

		//~: good code
		this.goodCode = gu.getCode();

		//~: good name
		this.goodName = gu.getName();

		//~: good calculation
		if(gu.getGoodCalc() != null)
			this.goodSemiReady = gu.getGoodCalc().isSemiReady();

		//~: measure unit
		return this.init(gu.getMeasure());
	}

	public InvoiceGoodView init(MeasureUnit m)
	{
		//~: measure unit (displayed) name
		this.volumeUnitName = m.getCode();

		//~: volume is integer
		this.volumeInteger = !m.getOx().isFractional();

		return this;
	}

	public InvoiceGoodView init(BuyGood g)
	{
		//~: volume cost
		setVolumeCost(g.getCost());

		return this;
	}

	public InvoiceGoodView init(SellGood g)
	{
		//~: volume cost
		setVolumeCost(g.getCost());

		//?: {has good price reference}
		if(g.getPrice() != null)
			this.init(g.getPrice());

		return this;
	}

	public InvoiceGoodView init(MoveGood g)
	{
		//~: move-on operation flag
		this.moveOn = g.getMoveOn();

		return this;
	}

	public InvoiceGoodView init(ResGood g)
	{
		//~: calculation date
		if(g.getGoodCalc() != null)
		{
			this.calcDate = g.getGoodCalc().getOpenTime();
			this.goodSemiReady = g.getGoodCalc().isSemiReady();
		}

		return this;
	}

	public InvoiceGoodView initFixedStateGood(Invoice invoice, int index)
	{
		InvoiceStateFixed s = (InvoiceStateFixed) invoice.getInvoiceState();

		//?: {this is a volume check document}
		if(Invoices.isVolumeCheck(invoice))
			return this.init(s.getStoreGoods().get(index));

		return this;
	}

	public InvoiceGoodView init(StoreGood g)
	{
		boolean    i = !g.getGoodUnit().getMeasure().getOx().isFractional();

		BigDecimal v = g.getVolumePositive();
		if(v == null) v = BigDecimal.ZERO;
		if(g.getVolumeNegative() != null)
			v = v.subtract(g.getVolumeNegative());

		this.goodVolumeDelta = (!i)?(v):(v.setScale(0));

		return this;
	}

	public InvoiceGoodView init(GoodPrice p)
	{
		this.goodPrice = p.getPrimaryKey();

		return this.init(p.getPriceList());
	}

	public InvoiceGoodView init(PriceListEntity p)
	{
		this.priceList     = p.getPrimaryKey();
		this.priceListCode = p.getCode();
		this.priceListName = p.getName();

		return this;
	}
}