package com.tverts.retrade.domain.prices;

/* Java */

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.ContractorView;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Displays Price Lists of the Contractor
 * denoted in the order of the association.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "firm-prices")
public class FirmPricesView extends ContractorView
{
	/* Firm Prices View Bean */

	/**
	 * Primary keys of the Price Lists separated by space ' '.
	 */
	public String getListsKeys()
	{
		return listsKeys;
	}

	private String listsKeys;

	public void setListsKeys(String listsKeys)
	{
		this.listsKeys = listsKeys;
	}

	/**
	 * Codes of the Price Lists separated by tab '\t'.
	 */
	public String getListsCodes()
	{
		return listsCodes;
	}

	private String listsCodes;

	public void setListsCodes(String listsCodes)
	{
		this.listsCodes = listsCodes;
	}

	/**
	 * Names of the Price Lists separated by tab '\t'.
	 */
	public String getListsNames()
	{
		return listsNames;
	}

	private String listsNames;

	public void setListsNames(String listsNames)
	{
		this.listsNames = listsNames;
	}


	/* Initialization */

	public FirmPricesView initPrices(List<FirmPrices> fps)
	{
		//~: map lists by the priority
		Map<Integer, FirmPrices> fpm = new TreeMap<Integer, FirmPrices>();
		for(FirmPrices fp : fps)
		{
			EX.assertx(null == fpm.put(fp.getPriority(), fp));

			if(getObjectKey() != null)
				EX.assertx(CMP.eq(getObjectKey(), fp.getContractor().getPrimaryKey()));
		}

		//~: form the strings
		StringBuilder ks = new StringBuilder(32);
		StringBuilder cs = new StringBuilder(64);
		StringBuilder ns = new StringBuilder(92);

		for(FirmPrices fp : fpm.values())
		{
			ks.append((ks.length() != 0)?(" "):(""));
			ks.append(fp.getPriceList().getPrimaryKey());

			cs.append((cs.length() != 0)?("\t"):(""));
			cs.append(fp.getPriceList().getCode());

			ns.append((ns.length() != 0)?("\t"):(""));
			ns.append(fp.getPriceList().getName());
		}

		//~: assign them
		listsKeys  = ks.toString();
		listsCodes = cs.toString();
		listsNames = ns.toString();

		return this;
	}
}