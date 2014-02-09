package com.tverts.hibery.keys;

/* standard Java classes */

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/* Hibernate Persistence Layer */

import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysGenerator;
import com.tverts.endure.keys.KeysGeneratorBinder;

/* com.tverts: hibery system */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.OU;
import com.tverts.support.SU;


/**
 * Access strategy wrapping Hibernate
 * Identifier Generator implementation.
 *
 * @author anton.baukin@gmail.com
 */
public class      HiberKeysGeneratorBinder
       implements KeysGeneratorBinder
{
	/* public: KeysGeneratorBinder interface */

	public String        getGeneratorName()
	{
		return generatorName;
	}

	public KeysGenerator getGeneratorBound()
	{
		return generatorBound;
	}

	public void          bindGenerator()
	{
		EX.assertx(getGeneratorBound() == null);
		EX.assertn(getGeneratorClass());

		this.generatorBound = createGenerator();
	}


	/* public: HiberKeysGeneratorBinder interface */

	public void       setGeneratorName(String name)
	{
		EX.assertn(name = SU.s2s(name));
		this.generatorName = name;
	}

	public Class<? extends IdentifierGenerator>
	                  getGeneratorClass()
	{
		return generatorClass;
	}

	public void       setGeneratorClass
	  (Class<? extends IdentifierGenerator> gclass)
	{
		this.generatorClass = gclass;
	}

	public Properties getProperties()
	{
		return (properties != null)?(properties):
		  (properties = new Properties());
	}

	public void       setProperties(Properties props)
	{
		this.properties = OU.clone(props);
	}


	/* protected: build generator */

	protected KeysGenerator createGenerator()
	{
		IdentifierGenerator hgen;
		Properties          props = OU.clone(getProperties());
		final String        NN    = PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER;

		//?: {has no name normalizer property} set it
		if(!props.containsKey(NN))
			props.put(NN, HiberSystem.config().createMappings().
			  getObjectNameNormalizer());

		//~: create generator instance
		try
		{
			hgen = getGeneratorClass().newInstance();

			if(hgen instanceof Configurable)
				((Configurable)hgen).configure(
				  getKeyType(), props, HiberSystem.dialect());
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}

		//~: schema updates to create generator in database
		if(hgen instanceof PersistentIdentifierGenerator) try
		{
			final PersistentIdentifierGenerator pig =
			  (PersistentIdentifierGenerator)hgen;

			//!:  ------------------> MUST
			bean(TxBean.class).setNew(true).execute(new Runnable()			{
				public void run()
				{
					try
					{
						ensureDatabaseTx(pig);
					}
					catch(Throwable e)
					{
						throw EX.wrap(e);
					}
				}
			}
			);
		}
		catch(Throwable e)
		{
			LU.D(getLog(), "seemed exists primary keys sequence [",
			  ((PersistentIdentifierGenerator)hgen).generatorKey().toString(), "]?..");
		}

		return new HiberKeysGeneratorBridge(hgen);
	}

	/**
	 * WARNING: we do not check whether the sequence already exist.
	 *   New transaction is required to ignore the error!
	 */
	protected void          ensureDatabaseTx(PersistentIdentifierGenerator hgen)
	  throws Exception
	{
		//~: create update queries
		String[]   qs = hgen.sqlCreateStrings(HiberSystem.dialect());

		//~: open the connection
		Connection co = HiberSystem.getInstance().openConnection();
		Statement  st = co.createStatement();
		Exception  er = null;

		//~: execute the queries
		try
		{
			//~: execute DDL updates
			for(String q : qs)
				st.execute(q);
		}
		catch(Exception e)
		{
			throw er = e;
		}
		finally
		{
			//~: try to release the connection
			try
			{
				HiberSystem.getInstance().closeConnection(co);
			}
			catch(Exception e)
			{
				if(er == null) er = e;
			}
		}

		//?: {got error} seems sequence exists
		if(er != null)
			throw er;

		LU.I(getLog(), "successfully created primary keys sequence [",
		  hgen.generatorKey().toString(), "]");
	}

	protected Type          getKeyType()
	{
		return LongType.INSTANCE;
	}


	/* protected: logging */

	protected String        getLog()
	{
		return LU.getLogBased(HiberSystem.class, this);
	}


	/* private: the binder state */

	private String          generatorName;
	private KeysGenerator   generatorBound;


	/* private: the binder parameters */

	private Properties      properties;
	private Class<? extends IdentifierGenerator>
	                        generatorClass;
}