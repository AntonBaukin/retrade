package com.tverts.retrade.domain.core;

/* com.tverts: object (parameters) */

import com.tverts.objects.Param;
import com.tverts.objects.Prop;
import com.tverts.retrade.web.GoDomainIndexPage;


/**
 * Collection of Domain properties.
 *
 * @author anton.baukin@gmail.com
 */
public class DomainProps
{
	/* public: parameters access interface  */

	/**
	 * @see {@link GoDomainIndexPage}.
	 */
	@Param @Prop(area = "Web")
	public String getIndexPage()
	{
		return indexPage;
	}

	private String indexPage;

	public void setIndexPage(String indexPage)
	{
		this.indexPage = indexPage;
	}
}