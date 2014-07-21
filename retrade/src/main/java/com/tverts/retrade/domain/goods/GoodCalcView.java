package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Read and edit view on a Good Calculation.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good-calc")
public class GoodCalcView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public Long getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(Long goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getOpenTime()
	{
		return openTime;
	}

	public void setOpenTime(Date openTime)
	{
		this.openTime = openTime;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getCloseTime()
	{
		return closeTime;
	}

	public void setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	public boolean isSemiReady()
	{
		return semiReady;
	}

	public void setSemiReady(boolean semiReady)
	{
		this.semiReady = semiReady;
	}

	public List<CalcPartView> getParts()
	{
		return (parts != null)?(parts):
		  (parts = new ArrayList<CalcPartView>(2));
	}

	public void setParts(List<CalcPartView> parts)
	{
		this.parts = parts;
	}

	@XmlTransient
	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public boolean isDerived()
	{
		return derived;
	}

	public void setDerived(boolean derived)
	{
		this.derived = derived;
	}

	public Long getSuperGood()
	{
		return superGood;
	}

	public void setSuperGood(Long superGood)
	{
		this.superGood = superGood;
	}

	public String getSubCode()
	{
		return subCode;
	}

	public void setSubCode(String subCode)
	{
		this.subCode = subCode;
	}

	public BigDecimal getSubVolume()
	{
		return subVolume;
	}

	public void setSubVolume(BigDecimal subVolume)
	{
		this.subVolume = subVolume;
	}

	public String getSuperGoodCode()
	{
		return superGoodCode;
	}

	public void setSuperGoodCode(String superGoodCode)
	{
		this.superGoodCode = superGoodCode;
	}

	@XmlElement
	public String getSuperGoodName()
	{
		return superGoodName;
	}

	@XmlElement
	public String getSuperGoodMeasure()
	{
		return superGoodMeasure;
	}

	@XmlElement
	public boolean isSuperGoodInteger()
	{
		return superGoodInteger;
	}


	/* public: init interface */

	public GoodCalcView init(Object o)
	{
		if(o instanceof GoodCalc)
			return this.init((GoodCalc) o);

		return this;
	}

	public GoodCalcView init(GoodCalc c)
	{
		//~: general calculation
		this.objectKey = c.getPrimaryKey();
		this.goodUnit = c.getGoodUnit().getPrimaryKey();
		this.openTime = c.getOpenTime();
		this.closeTime = c.getCloseTime();
		this.semiReady = c.isSemiReady();
		this.remarks = c.getOx().getRemarks();

		//~: derived good
		this.derived = (c.getSuperGood() != null);
		if(this.derived)
		{
			this.subCode = c.getOx().getSubCode();
			this.subVolume = c.getOx().getSubVolume();
			this.initSuperGood(c.getSuperGood());
		}

		return this;
	}

	public GoodCalcView initParts(GoodCalc c)
	{
		for(CalcPart p : c.getParts())
			getParts().add(new CalcPartView().init(p));

		return this;
	}

	public GoodCalcView init(GoodUnitView g)
	{
		this.goodUnit = g.getObjectKey();

		return this;
	}

	public GoodCalcView initSuperGood(GoodUnit sg)
	{
		if(sg == null)
		{
			this.derived = false;
			this.subCode = null;
			this.subVolume = null;

			this.superGood = null;
			this.superGoodCode = null;
			this.superGoodName = null;
			this.superGoodMeasure = null;
			this.superGoodInteger = false;
		}
		else
		{
			this.derived = true;

			this.superGood = sg.getPrimaryKey();
			this.superGoodCode = sg.getCode();
			this.superGoodName = sg.getName();
			this.superGoodMeasure = sg.getMeasure().getCode();
			this.superGoodInteger = !sg.getMeasure().getOx().isFractional();

			if(this.subVolume != null)
				this.subVolume = this.subVolume.setScale(
				  (this.superGoodInteger)?(0):(3)
				);
		}

		return this;
	}


	/* calculation properties */

	private Long    objectKey;
	private Long    goodUnit;
	private Date    openTime;
	private Date    closeTime;
	private boolean semiReady;
	private String  remarks;

	private List<CalcPartView> parts;


	/* derived good properties */

	private boolean    derived;
	private Long       superGood;
	private String     subCode;
	private BigDecimal subVolume;
	private String     superGoodCode;
	private String     superGoodName;
	private String     superGoodMeasure;
	private boolean    superGoodInteger;
}