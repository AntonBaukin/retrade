package com.tverts.retrade.domain.invoice;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.InvoiceGoodEditModelData;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;
import com.tverts.support.OU;


/**
 * Model bean for {@link InvoiceGoodView} is being edited
 * linked with parent model {@link InvoiceEditModelBean}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "invoice-good-edit-model")
public class InvoiceGoodEditModelBean extends DataSelectModelBean
{
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


	/* Invoice Good Edit Model */

	public String getInvoiceModel()
	{
		return invoiceModel;
	}

	public void setInvoiceModel(String invoiceModel)
	{
		this.invoiceModel = invoiceModel;
	}

	public Long getGoodKey()
	{
		return goodKey;
	}

	public void setGoodKey(Long goodKey)
	{
		this.goodKey = goodKey;
	}

	public Long getTradeStore()
	{
		return tradeStore;
	}

	public void setTradeStore(Long tradeStore)
	{
		this.tradeStore = tradeStore;
	}

	public boolean isDirectPrice()
	{
		return directPrice;
	}

	public void setDirectPrice(boolean directPrice)
	{
		this.directPrice = directPrice;
	}


	/* Invoice Good Edit Model (support) */

	public InvoiceEditModelBean invoiceModelBean()
	{
		return EX.assertn(getInvoiceModelBean(),
		  "No Invoice Edit model found when editing it's good!");
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


	/* private: encapsulated data */

	private String   invoiceModel;
	private Long     goodKey;
	private Long     tradeStore;
	private boolean  directPrice;

	private ObjectAccess<InvoiceGoodView> goodAccess;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.str(o, invoiceModel);
		IO.longer(o, goodKey);
		IO.longer(o, tradeStore);
		o.writeBoolean(directPrice);

		//?: {not a transient data access}
		if(goodAccess instanceof Serializable)
			IO.obj(o, goodAccess);
		else
			IO.obj(o, null);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		invoiceModel = IO.str(i);
		goodKey      = IO.longer(i);
		tradeStore   = IO.longer(i);
		directPrice  = i.readBoolean();
		goodAccess   = IO.obj(i, ObjectAccess.class);
	}
}