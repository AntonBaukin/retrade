package com.tverts.genesis;

/**
 * Facade to Genesis subsystem.
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisPoint
{
	/*  Singleton */

	private static GenesisPoint INSTANCE =
	  new GenesisPoint();

	public static GenesisPoint getInstance()
	{
		return INSTANCE;
	}

	protected GenesisPoint()
	{}


	/* public: log destinations */

	/**
	 * General lof that may be used within the Shunt Units.
	 */
	public static final String LOG_GENESIS  =
	  "com.tverts.genesis";

	/**
	 * Log destination for the Self Shunt Service.
	 * Note that it's root is the services root,
	 * not the shunts package.
	 */
	public static final String LOG_SERVICE =
	  "com.tverts.genesis.GenesisService";


	/* public: GenesisPoint access */

	/**
	 * Primary {@link GenesisService} used by default in the
	 * facade implementation.
	 */
	public GenesisService getGenesisService()
	{
		return genesisService;
	}

	public void           setGenesisService(GenesisService service)
	{
		this.genesisService = service;
	}


	/* private: genesis service reference */

	private GenesisService genesisService;
}