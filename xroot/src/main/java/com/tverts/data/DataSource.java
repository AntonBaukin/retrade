package com.tverts.data;

/* com.tverts: model */

import com.tverts.model.ModelBean;


/**
 * Data Source is a strategy of obtaining
 * selectors for data of the system.
 *
 * Selector is a XML-compatible, serializable
 * Java Bean that is given to the execution
 * subsystem to provide the real data as
 * an XML stream written as Bytes Stream.
 *
 * The most of Data Sources do support
 * the configuration via web-interface.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public interface DataSource
{
	/* public: Data Source (info) */

	/**
	 * Unique Data Source ID.
	 */
	public String    did();

	public String    getName();

	public String    getNameLo();

	public String    getDescr();

	public String    getDescrLo();


	/* public: Data Source (data) */

	/**
	 * Returns the path to the JSF page
	 * handling the data configuration.
	 *
	 * If this Data Source is not configurable,
	 * must return null, and the model bean
	 * will not be requested.
	 */
	public String    getUiPath();

	public ModelBean createModel(DataCtx ctx);

	/**
	 * Creates the data for the model configured.
	 * Invoked only when the data source supports
	 * web-interface.
	 */
	public Object    provideData(ModelBean m);

	/**
	 * Creates the data. Invoked only when the source
	 * does not have the web-interface.
	 *
	 * The context given has the data minimum.
	 */
	public Object    provideData(DataCtx ctx);

	public boolean   isSystem();
}