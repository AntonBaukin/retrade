package com.tverts.retrade.web.views;

/* standard Java classes */

import java.util.Date;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.NumericModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade (goods + prices) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.GoodPriceModelBean;


/**
 * The read-only view of Price List entry (Good Price).
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesGoodPriceView extends NumericModelView
{
	/* public: bean read interface */

	public GoodPrice          getNumeric()
	{
		return (GoodPrice)super.getNumeric();
	}

	public GoodUnit           getGood()
	{
		return getNumeric().getGoodUnit();
	}

	public GoodPriceModelBean getModel()
	{
		return (GoodPriceModelBean)super.getModel();
	}


	/* public: view [history] interface */

	public String  getWindowTitleHistory()
	{
		return formatTitles(
		  formatTitle("История цен товара", getGood().getCode()),
		  formatTitle("Прайс-лист",
		    getNumeric().getPriceList().getCode(),
		    getNumeric().getPriceList().getName()
		));
	}

	public String  getGoodPrice()
	{
		return (getNumeric().getPrice() == null)?(null):
		  (getNumeric().getPrice().toString());
	}


	/* protected: NumericModelView interface */

	protected NumericModelBean createModelInstance(Long objectKey)
	{
		//~: create the model bean
		GoodPriceModelBean mb = new GoodPriceModelBean();

		//=: entity class
		mb.setObjectClass(GoodPrice.class);

		//=: max search date (present time)
		mb.setMaxDate(new Date());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodPriceModelBean);
	}
}