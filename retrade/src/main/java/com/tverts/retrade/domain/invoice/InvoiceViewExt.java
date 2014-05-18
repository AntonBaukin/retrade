package com.tverts.retrade.domain.invoice;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: retrade domain (forms + stores) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.store.TradeStore;


/**
 * Extended Invoice View.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "invoice")
public class InvoiceViewExt extends InvoiceView
{
	public static final long serialVersionUID = 0L;


	/* public: InvoiceEdit (bean) interface */

	public Long     getTradeStore()
	{
		return tradeStore;
	}

	public void     setTradeStore(Long tradeStore)
	{
		this.tradeStore = tradeStore;
	}

	public String   getStoreCode()
	{
		return storeCode;
	}

	public void     setStoreCode(String storeCode)
	{
		this.storeCode = storeCode;
	}

	public String   getStoreName()
	{
		return storeName;
	}

	public void     setStoreName(String storeName)
	{
		this.storeName = storeName;
	}

	public Long     getTradeStoreSource()
	{
		return tradeStoreSource;
	}

	public void     setTradeStoreSource(Long tradeStoreSource)
	{
		this.tradeStoreSource = tradeStoreSource;
	}

	public String   getStoreSourceCode()
	{
		return storeSourceCode;
	}

	public void     setStoreSourceCode(String storeSourceCode)
	{
		this.storeSourceCode = storeSourceCode;
	}

	public String   getStoreSourceName()
	{
		return storeSourceName;
	}

	public void     setStoreSourceName(String storeSourceName)
	{
		this.storeSourceName = storeSourceName;
	}

	public Long     getContractor()
	{
		return contractor;
	}

	public void     setContractor(Long contractor)
	{
		this.contractor = contractor;
	}

	public String   getContractorCode()
	{
		return contractorCode;
	}

	public void     setContractorCode(String contractorCode)
	{
		this.contractorCode = contractorCode;
	}

	public String   getContractorName()
	{
		return contractorName;
	}

	public void     setContractorName(String contractorName)
	{
		this.contractorName = contractorName;
	}

	public String   getContractorFullName()
	{
		return contractorFullName;
	}

	public void     setContractorFullName(String contractorFullName)
	{
		this.contractorFullName = contractorFullName;
	}

	public String   getRemarks()
	{
		return remarks;
	}

	public void     setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	public List<InvoiceGoodView> getGoods()
	{
		return (goods != null)?(goods):
		  (goods = new ArrayList<InvoiceGoodView>(8));
	}

	public void     setGoods(List<InvoiceGoodView> goods)
	{
		this.goods = goods;
	}


	/* public: InvoiceEdit (support) interface */

	public InvoiceViewExt init(Object obj)
	{
		if(obj instanceof Contractor)
			return this.init((Contractor)obj);

		return (InvoiceViewExt) super.init(obj);
	}

	public InvoiceViewExt init(Invoice i)
	{
		super.init(i);

		//~: remarks
		this.remarks = i.getRemarks();

		//~: trade store
		if(i.getInvoiceData().getStore() != null)
			this.init(i.getInvoiceData().getStore());

		//~: trade store source
		if(Invoices.isMoveInvoice(i))
			this.initSource(((MoveData)i.getInvoiceData()).getSourceStore());

		//?: {editing this invoice} create the goods edits
		int j = 0; for(InvGood g : i.getInvoiceData().getGoods())
			getGoods().add(new InvoiceGoodView().init(g).init(j++));

		return this;
	}

	public InvoiceViewExt init(Contractor c)
	{
		this.contractor = c.getPrimaryKey();
		this.contractorCode = c.getCode();
		this.contractorName = c.getName();

		if(c.getFirm() != null)
			this.contractorFullName = c.getFirm().getFullName();

		return this;
	}

	public InvoiceViewExt init(TradeStore s)
	{
		this.tradeStore = s.getPrimaryKey();
		this.storeCode = s.getCode();
		this.storeName = s.getName();

		return this;
	}

	public InvoiceViewExt initSource(TradeStore s)
	{
		this.tradeStoreSource = s.getPrimaryKey();
		this.storeSourceCode = s.getCode();
		this.storeSourceName = s.getName();

		return this;
	}


	/* private: invoice attributes */

	private Long   tradeStore;
	private String storeCode;
	private String storeName;
	private Long   tradeStoreSource;
	private String storeSourceCode;
	private String storeSourceName;
	private Long   contractor;
	private String contractorCode;
	private String contractorName;
	private String contractorFullName;
	private String remarks;


	/* private: the list of the goods */

	private List<InvoiceGoodView> goods;
}