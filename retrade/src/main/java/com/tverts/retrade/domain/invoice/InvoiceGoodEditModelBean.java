package com.tverts.retrade.domain.invoice;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.InvoiceGoodEditModelData;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Model bean for {@link InvoiceGoodView} is being edited
 * linked with parent model {@link InvoiceEditModelBean}.
 *
 * TODO support paged goods table when selecting good unit
 *
 *
 * @author anton.baukin@gmail.com
 */
public class InvoiceGoodEditModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public InvoiceGoodEditModelBean()
	{}

	/**
	 * Special version of model bean for edit model
	 * of good not in the database yet (is being created).
	 */
	public InvoiceGoodEditModelBean(InvoiceGoodView good)
	{
		this.goodKey    = good.getObjectKey();
		this.goodAccess = OU.permAcces(good);
	}


	/* public: InvoiceGoodEditModelBean (bean) interface */

	public String   getInvoiceModel()
	{
		return invoiceModel;
	}

	public void     setInvoiceModel(String invoiceModel)
	{
		this.invoiceModel = invoiceModel;
	}

	public Long     getGoodKey()
	{
		return goodKey;
	}

	public void     setGoodKey(Long goodKey)
	{
		this.goodKey = goodKey;
	}

	public Long     getTradeStore()
	{
		return tradeStore;
	}

	public void     setTradeStore(Long tradeStore)
	{
		this.tradeStore = tradeStore;
	}

	public String[] getSearchGoods()
	{
		return searchGoods;
	}

	public void     setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}

	public boolean  isDirectPrice()
	{
		return directPrice;
	}

	public void     setDirectPrice(boolean directPrice)
	{
		this.directPrice = directPrice;
	}


	/* public: InvoiceGoodEditModelBean (support) interface */

	public InvoiceEditModelBean invoiceModelBean()
	{
		InvoiceEditModelBean res = getInvoiceModelBean();
		if(res == null) throw new IllegalStateException(
		  "No Invoice Edit model found when editing it's good!");
		return res;
	}

	public InvoiceEditModelBean getInvoiceModelBean()
	{
		return (getInvoiceModel() == null)?(null):
		  readModelBean(getInvoiceModel(), InvoiceEditModelBean.class);
	}

	public InvoiceGoodView      getGoodEdit()
	{
		if(getGoodKey() == null)
			return null;

		//?: {has no Good Access strategy} create it
		if(goodAccess == null)
			goodAccess = createGoodAccess();

		return goodAccess.accessObject();
	}

	public GoodUnit             getGoodUnit()
	{
		Long key = (getGoodEdit() == null)?(null):
		  (getGoodEdit().getGoodUnit());

		return (key == null)?(null):
		  bean(GetGoods.class).getGoodUnit(key);
	}


	/* public: ModelBean (data access) interface */

	public ModelData            modelData()
	{
		return new InvoiceGoodEditModelData(this);
	}


	/* protected: good access factory */

	protected class GoodAccess implements ObjectAccess<InvoiceGoodView>
	{
		/* public: ObjectAccess interface */

		public InvoiceGoodView accessObject()
		{
			InvoiceEdit invoice = (getInvoiceModelBean() == null)?
			  (null):(getInvoiceModelBean().getInvoice());

			if(invoice != null) for(InvoiceGoodView g : invoice.getGoods())
				if(getGoodKey().equals(g.getObjectKey()))
					return g;

			return null;
		}
	}

	protected ObjectAccess<InvoiceGoodView> createGoodAccess()
	{
		return new GoodAccess();
	}


	/* private: the edit state */

	private String   invoiceModel;
	private Long     goodKey;
	private Long     tradeStore;
	private boolean  directPrice;


	/* private: edit form support */

	private String[] searchGoods;


	/* private: caches */

	private ObjectAccess<InvoiceGoodView> goodAccess;
}