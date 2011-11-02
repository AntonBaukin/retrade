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

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysGenerator;
import com.tverts.endure.keys.KeysGeneratorBinder;

/* com.tverts: hibery system */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.OU;
import com.tverts.support.SU;

/**
 * TODO comment HiberKeysGeneratorBinder
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
		if(getGeneratorBound() != null)
			throw new IllegalStateException();

		if(getGeneratorClass() == null)
			throw new IllegalStateException();

		this.generatorBound = createGenerator();
	}


	/* public: HiberKeysGeneratorBinder interface */

	public void       setGeneratorName(String name)
	{
		if((name = SU.s2s(name)) == null)
			throw new IllegalArgumentException();
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

		//?: {has no name normilizer property} set it
		if(!props.containsKey(NN))
			props.put(NN, HiberSystem.config().createMappings().
			  getObjectNameNormalizer());

		//~: cerate generator instance
		try
		{
			hgen = getGeneratorClass().newInstance();

			if(hgen instanceof Configurable)
				((Configurable)hgen).configure(
				  getKeyType(), props, HiberSystem.dialect());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		//~: schema updates to create generator in database
		if(hgen instanceof PersistentIdentifierGenerator)
			ensureDatabase((PersistentIdentifierGenerator)hgen);

		return new HiberKeysGeneratorBridge(hgen);
	}

	protected void          ensureDatabase(PersistentIdentifierGenerator hgen)
	{
		//~: create update queries
		String[]   qs = hgen.sqlCreateStrings(HiberSystem.dialect());

		Connection co;
		Statement  st;

		//~: open the connection
		try
		{
			co = HiberSystem.getInstance().openConnection();
			st = co.createStatement();
		}
		catch(Exception e)
		{
			throw new RuntimeException(
			  "Can't open database connection to " +
			  "create primary keys sequences!", e);
		}

		Exception er = null;

		try
		{
			//~: execute DDL updates
			for(String q : qs)
				st.execute(q);

			//!: commit
			co.commit();

			if(LU.isI(getLog())) LU.I(getLog(), String.format(
			  "successfully created primary keys sequence '%s'",

			  hgen.generatorKey().toString()));
		}
		catch(Exception e)
		{
			//!: Here we ignore update errors. We do not check
			//   whether the sequence already exists.
		}
		finally
		{
			//~: close the statement
			try
			{
				st.close();
			}
			catch(Exception e)
			{
				er = e;
			}

			//~: close the connection
			try
			{
				HiberSystem.getInstance().closeConnection(co);
			}
			catch(Exception e)
			{
				if(er == null) er = e;
			}
		}

		//?: {has connection error} raise it
		if(er != null) throw new RuntimeException(
		  "Can't close database connection when " +
		  "creating primary keys sequences!", er);
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