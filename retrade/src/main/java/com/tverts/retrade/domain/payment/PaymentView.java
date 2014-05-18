package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: retrade domain (accounts + firms) */

import com.tverts.retrade.domain.account.Account;
import com.tverts.retrade.domain.account.PayBank;
import com.tverts.retrade.domain.account.PayFirm;
import com.tverts.retrade.domain.account.PaySelf;
import com.tverts.retrade.domain.account.PayWay;
import com.tverts.retrade.domain.firm.Contractor;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.PayDesk;
import com.tverts.retrade.domain.sells.SellsDesk;
import com.tverts.retrade.domain.sells.SellsSession;

/* com.tverts: support */

import com.tverts.support.jaxb.DateAdapter;
import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * A read-only view on a {@link Payment}. To simplify,
 * attributes of Payment subclasses are also included.
 * As also Payment Ways of various types are.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "payment")
public class PaymentView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean (payment) interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getTypeNameLo()
	{
		return typeNameLo;
	}

	public void setTypeNameLo(String typeNameLo)
	{
		this.typeNameLo = typeNameLo;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public BigDecimal getIncome()
	{
		return income;
	}

	public void setIncome(BigDecimal income)
	{
		this.income = income;
	}

	public BigDecimal getExpense()
	{
		return expense;
	}

	public void setExpense(BigDecimal expense)
	{
		this.expense = expense;
	}

	@XmlElement
	public BigDecimal getBalance()
	{
		BigDecimal b = getIncome();

		if((b != null) && (getExpense() != null))
			b = b.subtract(getExpense());
		if(b != null)
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b;
	}


	/* public: bean (self) interface */

	public Long getSelfPayKey()
	{
		return selfPayKey;
	}

	public void setSelfPayKey(Long selfPayKey)
	{
		this.selfPayKey = selfPayKey;
	}

	public Long getSelfAccountKey()
	{
		return selfAccountKey;
	}

	public void setSelfAccountKey(Long selfAccountKey)
	{
		this.selfAccountKey = selfAccountKey;
	}

	public String getSelfAccountCode()
	{
		return selfAccountCode;
	}

	public void setSelfAccountCode(String selfAccountCode)
	{
		this.selfAccountCode = selfAccountCode;
	}

	public String getSelfAccountName()
	{
		return selfAccountName;
	}

	public void setSelfAccountName(String selfAccountName)
	{
		this.selfAccountName = selfAccountName;
	}

	public Long getSelfWayKey()
	{
		return selfWayKey;
	}

	public void setSelfWayKey(Long selfWayKey)
	{
		this.selfWayKey = selfWayKey;
	}

	public String getSelfWayName()
	{
		return selfWayName;
	}

	public void setSelfWayName(String selfWayName)
	{
		this.selfWayName = selfWayName;
	}

	public String getSelfWayTypeNameLo()
	{
		return selfWayTypeNameLo;
	}

	public void setSelfWayTypeNameLo(String selfWayTypeNameLo)
	{
		this.selfWayTypeNameLo = selfWayTypeNameLo;
	}

	public String getSelfWayBankId()
	{
		return selfWayBankId;
	}

	public void setSelfWayBankId(String selfWayBankId)
	{
		this.selfWayBankId = selfWayBankId;
	}

	public String getSelfWayBankName()
	{
		return selfWayBankName;
	}

	public void setSelfWayBankName(String selfWayBankName)
	{
		this.selfWayBankName = selfWayBankName;
	}

	public String getSelfWayBankAccount()
	{
		return selfWayBankAccount;
	}

	public void setSelfWayBankAccount(String selfWayBankAccount)
	{
		this.selfWayBankAccount = selfWayBankAccount;
	}

	public String getSelfWayBankRemitteeAccount()
	{
		return selfWayBankRemitteeAccount;
	}

	public void setSelfWayBankRemitteeAccount(String selfWayBankRemitteeAccount)
	{
		this.selfWayBankRemitteeAccount = selfWayBankRemitteeAccount;
	}

	public String getSelfWayBankRemitteeName()
	{
		return selfWayBankRemitteeName;
	}

	public void setSelfWayBankRemitteeName(String selfWayBankRemitteeName)
	{
		this.selfWayBankRemitteeName = selfWayBankRemitteeName;
	}


	/* public: bean (firm) interface */

	public Long getFirmPayKey()
	{
		return firmPayKey;
	}

	public void setFirmPayKey(Long firmPayKey)
	{
		this.firmPayKey = firmPayKey;
	}

	public Long getFirmAccountKey()
	{
		return firmAccountKey;
	}

	public void setFirmAccountKey(Long firmAccountKey)
	{
		this.firmAccountKey = firmAccountKey;
	}

	public String getFirmAccountCode()
	{
		return firmAccountCode;
	}

	public void setFirmAccountCode(String firmAccountCode)
	{
		this.firmAccountCode = firmAccountCode;
	}

	public String getFirmAccountName()
	{
		return firmAccountName;
	}

	public void setFirmAccountName(String firmAccountName)
	{
		this.firmAccountName = firmAccountName;
	}

	public Long getFirmWayKey()
	{
		return firmWayKey;
	}

	public void setFirmWayKey(Long firmWayKey)
	{
		this.firmWayKey = firmWayKey;
	}

	public String getFirmWayName()
	{
		return firmWayName;
	}

	public void setFirmWayName(String firmWayName)
	{
		this.firmWayName = firmWayName;
	}

	public String getFirmWayTypeNameLo()
	{
		return firmWayTypeNameLo;
	}

	public void setFirmWayTypeNameLo(String firmWayTypeNameLo)
	{
		this.firmWayTypeNameLo = firmWayTypeNameLo;
	}

	public String getFirmWayBankId()
	{
		return firmWayBankId;
	}

	public void setFirmWayBankId(String firmWayBankId)
	{
		this.firmWayBankId = firmWayBankId;
	}

	public String getFirmWayBankName()
	{
		return firmWayBankName;
	}

	public void setFirmWayBankName(String firmWayBankName)
	{
		this.firmWayBankName = firmWayBankName;
	}

	public String getFirmWayBankAccount()
	{
		return firmWayBankAccount;
	}

	public void setFirmWayBankAccount(String firmWayBankAccount)
	{
		this.firmWayBankAccount = firmWayBankAccount;
	}

	public String getFirmWayBankRemitteeAccount()
	{
		return firmWayBankRemitteeAccount;
	}

	public void setFirmWayBankRemitteeAccount(String firmWayBankRemitteeAccount)
	{
		this.firmWayBankRemitteeAccount = firmWayBankRemitteeAccount;
	}

	public String getFirmWayBankRemitteeName()
	{
		return firmWayBankRemitteeName;
	}

	public void setFirmWayBankRemitteeName(String firmWayBankRemitteeName)
	{
		this.firmWayBankRemitteeName = firmWayBankRemitteeName;
	}

	public Long getFirmKey()
	{
		return firmKey;
	}

	public void setFirmKey(Long firmKey)
	{
		this.firmKey = firmKey;
	}

	public String getFirmCode()
	{
		return firmCode;
	}

	public void setFirmCode(String firmCode)
	{
		this.firmCode = firmCode;
	}

	public String getFirmName()
	{
		return firmName;
	}

	public void setFirmName(String firmName)
	{
		this.firmName = firmName;
	}


	/* public: bean (payment order) interface */

	public Long getOrderKey()
	{
		return orderKey;
	}

	public void setOrderKey(Long orderKey)
	{
		this.orderKey = orderKey;
	}

	public String getOrderCode()
	{
		return orderCode;
	}

	public void setOrderCode(String orderCode)
	{
		this.orderCode = orderCode;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getOrderTime()
	{
		return orderTime;
	}

	public void setOrderTime(Date orderTime)
	{
		this.orderTime = orderTime;
	}

	public String getOrderRemarks()
	{
		return orderRemarks;
	}

	public void setOrderRemarks(String orderRemarks)
	{
		this.orderRemarks = orderRemarks;
	}

	public BigDecimal getOrderTotalIncome()
	{
		return orderTotalIncome;
	}

	public void setOrderTotalIncome(BigDecimal orderTotalIncome)
	{
		this.orderTotalIncome = orderTotalIncome;
	}

	public BigDecimal getOrderTotalExpense()
	{
		return orderTotalExpense;
	}

	public void setOrderTotalExpense(BigDecimal orderTotalExpense)
	{
		this.orderTotalExpense = orderTotalExpense;
	}

	public BigDecimal getOrderActualIncome()
	{
		return orderActualIncome;
	}

	public void setOrderActualIncome(BigDecimal orderActualIncome)
	{
		this.orderActualIncome = orderActualIncome;
	}

	public BigDecimal getOrderActualExpense()
	{
		return orderActualExpense;
	}

	public void setOrderActualExpense(BigDecimal orderActualExpense)
	{
		this.orderActualExpense = orderActualExpense;
	}

	@XmlElement
	public BigDecimal getOrderBalance()
	{
		BigDecimal ti = getOrderTotalIncome();
		BigDecimal te = getOrderTotalExpense();
		BigDecimal ai = getOrderActualIncome();
		BigDecimal ae = getOrderActualExpense();
		BigDecimal bl = BigDecimal.ZERO;

		if(ai != null) bl = bl.add(ai);
		if(ti != null) bl = bl.subtract(ti);

		if(ae != null) bl = bl.subtract(ae);
		if(te != null) bl = bl.add(te);

		if(bl != null)
			bl = bl.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bl;
	}


	/* public: bean (sells) interface */

	public Long getSellsDeskKey()
	{
		return sellsDeskKey;
	}

	public void setSellsDeskKey(Long sellsDeskKey)
	{
		this.sellsDeskKey = sellsDeskKey;
	}

	public String getSellsDeskCode()
	{
		return sellsDeskCode;
	}

	public void setSellsDeskCode(String sellsDeskCode)
	{
		this.sellsDeskCode = sellsDeskCode;
	}

	public String getSellsDeskName()
	{
		return sellsDeskName;
	}

	public void setSellsDeskName(String sellsDeskName)
	{
		this.sellsDeskName = sellsDeskName;
	}

	public String getSellsDeskRemarks()
	{
		return sellsDeskRemarks;
	}

	public void setSellsDeskRemarks(String sellsDeskRemarks)
	{
		this.sellsDeskRemarks = sellsDeskRemarks;
	}

	public Long getSellsPayDeskKey()
	{
		return sellsPayDeskKey;
	}

	public void setSellsPayDeskKey(Long sellsPayDeskKey)
	{
		this.sellsPayDeskKey = sellsPayDeskKey;
	}

	public String getSellsPayDeskName()
	{
		return sellsPayDeskName;
	}

	public void setSellsPayDeskName(String sellsPayDeskName)
	{
		this.sellsPayDeskName = sellsPayDeskName;
	}

	public String getSellsPayDeskRemarks()
	{
		return sellsPayDeskRemarks;
	}

	public void setSellsPayDeskRemarks(String sellsPayDeskRemarks)
	{
		this.sellsPayDeskRemarks = sellsPayDeskRemarks;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getSellsPayDeskOpenDate()
	{
		return sellsPayDeskOpenDate;
	}

	public void setSellsPayDeskOpenDate(Date sellsPayDeskOpenDate)
	{
		this.sellsPayDeskOpenDate = sellsPayDeskOpenDate;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getSellsPayDeskCloseDate()
	{
		return sellsPayDeskCloseDate;
	}

	public void setSellsPayDeskCloseDate(Date sellsPayDeskCloseDate)
	{
		this.sellsPayDeskCloseDate = sellsPayDeskCloseDate;
	}


	/* public: init (main) interface */

	public PaymentView init(String flags, Payment p)
	{
		initFlags(flags);
		objectKey = p.getPrimaryKey();
		typeNameLo = p.getUnity().getUnityType().getTitleLo();
		time = p.getTime();
		code = p.getCode();
		remarks = p.getRemarks();
		income = p.getIncome();
		expense = p.getExpense();

		if(flag("self"))
			init(p.getPaySelf());

		if(flag("order"))
			init(p.getPayOrder());

		if(flag("firm") && (p instanceof Settling))
			init((Settling) p);

		return this;
	}


	/* public: init (self) interface */

	public PaymentView init(PaySelf p)
	{
		selfPayKey = p.getPrimaryKey();

		if(flag("account"))
			initSelfAccount(p.getAccount());

		if(flag("way"))
			initSelfWay(p.getPayWay());

		return this;
	}

	public PaymentView initSelfAccount(Account a)
	{
		selfAccountKey = a.getPrimaryKey();
		selfAccountCode = a.getCode();
		selfAccountName = a.getName();

		return this;
	}

	public PaymentView initSelfWay(PayWay w)
	{
		selfWayKey = w.getPrimaryKey();
		selfWayName = w.getName();
		if(flag("way-type"))
			selfWayTypeNameLo = w.getUnity().getUnityType().getTitleLo();

		if(flag("bank") && (w instanceof PayBank))
			initSelfBankWay((PayBank) w);

		return this;
	}

	public PaymentView initSelfBankWay(PayBank w)
	{
		selfWayBankId = w.getBankId();
		selfWayBankName = w.getBankName();
		selfWayBankAccount = w.getBankAccount();
		selfWayBankRemitteeAccount = w.getRemitteeAccount();
		selfWayBankRemitteeName = w.getRemitteeName();

		return this;
	}


	/* public: init (firm) interface */

	public PaymentView init(Settling p)
	{
		return init(p.getPayFirm());
	}

	public PaymentView init(PayFirm p)
	{
		firmPayKey = p.getPrimaryKey();

		initFirmAccount(p.getAccount());

		if(flag("way"))
			initFirmWay(p.getPayWay());

		return this;
	}

	public PaymentView initFirmAccount(Account a)
	{
		firmAccountKey = a.getPrimaryKey();
		firmAccountCode = a.getCode();
		firmAccountName = a.getName();

		if(flag("firm"))
			init(a.getContractor());

		return this;
	}

	public PaymentView init(Contractor c)
	{
		firmKey = c.getPrimaryKey();
		firmCode = c.getCode();
		firmName = c.getName();

		return this;
	}

	public PaymentView initFirmWay(PayWay w)
	{
		firmWayKey = w.getPrimaryKey();
		firmWayName = w.getName();

		if(flag("way-type"))
			firmWayTypeNameLo = w.getUnity().getUnityType().getTitleLo();

		if(flag("bank") && (w instanceof PayBank))
			initFirmBankWay((PayBank) w);

		return this;
	}

	public PaymentView initFirmBankWay(PayBank w)
	{
		firmWayBankId = w.getBankId();
		firmWayBankName = w.getBankName();
		firmWayBankAccount = w.getBankAccount();
		firmWayBankRemitteeAccount = w.getRemitteeAccount();
		firmWayBankRemitteeName = w.getRemitteeName();

		return this;
	}


	/* public: init (payment order) interface */

	public PaymentView init(PayOrder o)
	{
		orderKey = o.getPrimaryKey();
		orderCode = o.getCode();
		orderTime = o.getTime();
		orderRemarks = o.getRemarks();
		orderTotalIncome = o.getTotalIncome();
		orderTotalExpense = o.getTotalExpense();
		orderActualIncome = o.getActualIncome();
		orderActualExpense = o.getActualExpense();

		if(flag("sells") && (o instanceof SellsSession))
			init((SellsSession) o);

		return this;
	}


	/* public: init (sells) interface */

	public PaymentView init(SellsSession s)
	{
		init(s.getSellsDesk());
		init(s.getPayDesk());

		return this;
	}

	public PaymentView init(SellsDesk d)
	{
		sellsDeskKey = d.getPrimaryKey();
		sellsDeskCode = d.getCode();
		sellsDeskName = d.getName();
		sellsDeskRemarks = d.getRemarks();

		return this;
	}

	public PaymentView init(PayDesk d)
	{
		sellsPayDeskKey = d.getPrimaryKey();
		sellsPayDeskName = d.getName();
		sellsPayDeskRemarks = d.getRemarks();
		sellsPayDeskOpenDate = d.getOpenDate();
		sellsPayDeskCloseDate = d.getCloseDate();

		return this;
	}


	/* protected: init flags */

	protected transient String flags = "";

	protected boolean flag(String f)
	{
		return flags.contains("-" + f + '-');
	}

	protected void initFlags(String flags)
	{
		if(flags == null) flags = "";
		if(!flags.startsWith("-")) flags = "-" + flags;
		if(!flags.endsWith("-")) flags += '-';

		this.flags = flags;
	}


	/* private: payment attributes */

	private Long       objectKey;
	private String     typeNameLo;
	private Date       time;
	private String     code;
	private String     remarks;
	private BigDecimal income;
	private BigDecimal expense;


	/* private: pay self attributes */

	private Long       selfPayKey;
	private Long       selfAccountKey;
	private String     selfAccountCode;
	private String     selfAccountName;
	private Long       selfWayKey;
	private String     selfWayName;
	private String     selfWayTypeNameLo;
	private String     selfWayBankId;
	private String     selfWayBankAccount;
	private String     selfWayBankName;
	private String     selfWayBankRemitteeAccount;
	private String     selfWayBankRemitteeName;


	/* private: pay firm attributes */

	private Long       firmPayKey;
	private Long       firmAccountKey;
	private String     firmAccountCode;
	private String     firmAccountName;
	private Long       firmWayKey;
	private String     firmWayName;
	private String     firmWayTypeNameLo;
	private String     firmWayBankId;
	private String     firmWayBankName;
	private String     firmWayBankAccount;
	private String     firmWayBankRemitteeAccount;
	private String     firmWayBankRemitteeName;
	private Long       firmKey;
	private String     firmCode;
	private String     firmName;


	/* private: payment order */

	private Long       orderKey;
	private String     orderCode;
	private Date       orderTime;
	private String     orderRemarks;
	private BigDecimal orderTotalIncome;
	private BigDecimal orderTotalExpense;
	private BigDecimal orderActualIncome;
	private BigDecimal orderActualExpense;


	/* private: sells attributes */

	private Long       sellsDeskKey;
	private String     sellsDeskCode;
	private String     sellsDeskName;
	private String     sellsDeskRemarks;
	private Long       sellsPayDeskKey;
	private String     sellsPayDeskName;
	private String     sellsPayDeskRemarks;
	private Date       sellsPayDeskOpenDate;
	private Date       sellsPayDeskCloseDate;
}