package com.tverts.data;

/* Java */

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Point to access data objects via the Data Sources.
 *
 * @author anton.baukin@gmail.com
 */
public class Datas
{
	/* Data Access Singleton */

	public static final Datas INSTANCE =
	  new Datas();

	public static Datas getInstance()
	{
		return INSTANCE;
	}

	private Datas()
	{}


	/* public: support interface */

	public static DataSource source(String did)
	{
		EX.asserts(did, "Valid Data Source ID must be provided!");

		return EX.assertn(INSTANCE.getSource(did),
		  "Data Source with DID [", did, "] is not registered!"
		);
	}


	/* public: access interface */

	public void         setReference(DataSourceReference reference)
	{
		EX.assertx(sources.isEmpty());

		for(DataSource ds : reference.dereferObjects())
		{
			EX.assertn(ds);

			//?: {did is undefined}
			EX.assertn(ds.did(), "Data Source of class [",
			  ds.getClass().getName(), "] has DID undefined!");

			//?: {already registered}
			EX.assertx( !sources.containsKey(ds.did()),
			  "Data Source DID [", ds.did(), "] is already registered!"
			);

			//~: put the source
			sources.put(ds.did(), ds);
		}
	}

	public DataSource   getSource(String did)
	{
		return sources.get(did);
	}

	public DataSource[] copySources()
	{
		DataSource[] res = new DataSource[sources.size()];
		res = sources.values().toArray(res);

		Arrays.sort(res, new Comparator<DataSource>()
		{
			public int compare(DataSource a, DataSource b)
			{
				String na = a.getNameLo();
				String nb = b.getNameLo();

				if(SU.sXe(na) || SU.sXe(nb))
				{
					na = a.getName();
					nb = b.getName();
				}

				return na.compareToIgnoreCase(nb);
			}
		});

		return res;
	}


	/* private: the root & the sources*/

	private Map<String, DataSource> sources =
	  new HashMap<String, DataSource>(17);
}