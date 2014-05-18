package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: retrade domain (goods + trade stores) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.MeasureUnit;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceList;
import com.tverts.retrade.domain.store.StoreGood;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Refers {@link InvGood} or {@link StoreGood}
 * by the good index in the invoice.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good")
public class InvoiceGoodView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: InvoiceGoodBean (bean) interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public int  getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	@XmlElement
	public Long getGoodUnit()
	{
		return goodUnit;
	}

	@XmlElement
	public String getGoodCode()
	{
		return goodCode;
	}

	@XmlElement
	public String getGoodName()
	{
		return goodName;
	}

	@XmlElement
	public String getVolumeUnitName()
	{
		return volumeUnitName;
	}

	@XmlElement
	public boolean isVolumeInteger()
	{
		return volumeInteger;
	}

	public BigDecimal getGoodVolume()
	{
		return goodVolume;
	}

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

	public void setGoodVolumeDelta(BigDecimal goodVolumeDelta)
	{
		this.goodVolumeDelta = goodVolumeDelta;
	}

	@XmlElement
	public Long getGoodPrice()
	{
		return goodPrice;
	}

	public Long getPriceList()
	{
		return priceList;
	}

	public void setPriceList(Long priceList)
	{
		this.priceList = priceList;
	}

	@XmlElement
	public String getPriceListCode()
	{
		return priceListCode;
	}

	@XmlElement
	public String getPriceListName()
	{
		return priceListName;
	}

	public Boolean getMoveOn()
	{
		return moveOn;
	}

	public void setMoveOn(Boolean moveOn)
	{
		this.moveOn = moveOn;
	}

	public Boolean getNeedCalc()
	{
		return needCalc;
	}

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

	@XmlElement
	public Boolean getGoodSemiReady()
	{
		return goodSemiReady;
	}


	/* public: initialization interface */

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
		this.volumeInteger = !m.isFractional();

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
		boolean    i = !g.getGoodUnit().getMeasure().isFractional();

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

	public InvoiceGoodView init(PriceList p)
	{
		this.priceList     = p.getPrimaryKey();
		this.priceListCode = p.getCode();
		this.priceListName = p.getName();

		return this;
	}


	/* private:  good view parameters */

	private Long       objectKey;
	private int        index;
	private Long       goodUnit;
	private String     goodCode;
	private String     goodName;
	private String     volumeUnitName;
	private boolean    volumeInteger;
	private BigDecimal goodVolume;
	private BigDecimal volumeCost;
	private BigDecimal goodVolumeDelta;
	private Long       goodPrice;
	private Long       priceList;
	private String     priceListCode;
	private String     priceListName;
	private Boolean    moveOn;
	private Boolean    needCalc;
	private Date       calcDate;
	private Boolean    goodSemiReady;
}