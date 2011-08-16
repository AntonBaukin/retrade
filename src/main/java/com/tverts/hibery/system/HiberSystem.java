package com.tverts.hibery.system;

/* standard Java classes */

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.util.IdentityMap;


/**
 * Provides access to the lower implementation levels
 * of Hibernate infrastructure of the application.
 *
 * @author anton.baukin@gmail.com
 */
public class HiberSystem
{
	/* public: Singleton */

	public static HiberSystem getInstance()
	{
		return INSTANCE;
	}

	private static final HiberSystem INSTANCE =
	  new HiberSystem();

	protected HiberSystem()
	{}

	/* public: logging destinations */

	public static final String LOG_HIBER_SYSTEM =
	  HiberSystem.class.getName();


	/* public: HiberSystem interface */

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory     = sf;
		this.connectionProvider = null;

		//?: {this factory provides implementation interface}
		if(sf instanceof SessionFactoryImplementor)
		{
			//~: remember the JDBC connections provider

			this.connectionProvider =
			  (((SessionFactoryImplementor)sf).getConnectionProvider());
		}

		if(sf != null) try
		{
			processSessionFactory(sf);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void setConfiguration(Configuration config)
	{
		this.configuration = config;
	}


	/* public: enhanced routines */

	public Class      findActualClass(Object instance)
	{
		if(instance == null)
			return null;

		if(!(instance instanceof HibernateProxy))
			return instance.getClass();

		//~: first, make it quicker...
		Class result = ((HibernateProxy)instance).
		  getHibernateLazyInitializer().getPersistentClass();

		//?: {this class has descendants} be sure more
		if(hasDescendants(result))
			result = ((HibernateProxy)instance).
			  getHibernateLazyInitializer().getImplementation().getClass();

		return result;
	}

	/**
	 * Returns all the entities of the given exact class,
	 * (but not it's subclasses!), that are stored in the
	 * persistence context of the session.
	 *
	 * This system level call is needed when there are update
	 * HQL queries executed, and you want to make all the attached
	 * instances actual.
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<T> findAttachedEntities(Session session, Class<T> entityClass)
	{
		if(!(session instanceof SessionImplementor))
			throw new IllegalArgumentException();

		HashSet  result   = new HashSet(31);
		Iterator entities = IdentityMap.keyIterator(
		  ((SessionImplementor)session).getPersistenceContext().getEntityEntries());

		while(entities.hasNext())
		{
			Object entity = entities.next();

			//?: {this entity is persisted normally}
			if(session.contains(entity))
				//?: {it is exact the same class}
				if(entityClass.equals(findActualClass(entity)))
					result.add(entity);
		}

		return result;
	}

	/**
	 * Reloads from the database all the entities of the exact class
	 * given excluding the (optionally provided) ones.
	 *
	 * WARNING!  This implementation is experimental.
	 *   Always do test it's using!
	 */
	public <T> Set<T> reloadAttachedEntities
	  (Session session, Class<T> entityClass, Collection<? extends T> excluded)
	{
		//~: find the entities to reload
		Set<T> res = findAttachedEntities(session, entityClass);
		if(excluded != null)
			res.removeAll(excluded);

		PersistenceContext pctx = ((SessionImplementor)session).
		  getPersistenceContext();

		//~: reload the entities
		for(Object entity : res)
		{
			EntityEntry ee = pctx.getEntry(entity);

			//?: {the entity is saved to the database}
			if((ee != null) && ee.isExistsInDatabase() && ee.isModifiableEntity())
			{
				LockMode lm = ee.getLockMode();

				//?: {no lock mode is affected}
				if((lm == null) || LockMode.NONE.equals(lm))
				{
					session.refresh(entity);
					continue;
				}

				//?: {special WRITE lock}
				if(LockMode.WRITE.equals(lm))  //<-- this is experimental
					lm = LockMode.PESSIMISTIC_WRITE;

				session.refresh(entity, new LockOptions(lm));
			}
		}

		return res;
	}


	/* public: access hibernate configuration */

	public Configuration        getConfiguration()
	{
		return configuration;
	}

	public Dialect              getDialect()
	{
		if(!(this.sessionFactory instanceof SessionFactoryImplementor))
			return null;

		return ((SessionFactoryImplementor)this.sessionFactory).getDialect();
	}

	public static Dialect       dialect()
	{
		Dialect dialect = HiberSystem.getInstance().getDialect();

		if(dialect == null) throw new IllegalStateException(
		  "Hibernate Dialect is not defined!");

		return dialect;
	}

	public static Configuration config()
	{
		Configuration config = HiberSystem.getInstance().
		  getConfiguration();

		if(config == null) throw new IllegalStateException();
		return config;
	}


	/* public: access connections provider */

	public ConnectionProvider   getConnectionProvider()
	{
		return connectionProvider;
	}

	public Connection           openConnection()
	  throws SQLException
	{
		ConnectionProvider cp = getConnectionProvider();

		if(cp == null) throw new IllegalStateException();
		return cp.getConnection();
	}

	public void                 closeConnection(Connection connection)
	  throws SQLException
	{
		if(connection.isClosed())
			return;

		ConnectionProvider cp = getConnectionProvider();

		if(cp == null) throw new IllegalStateException();
		cp.closeConnection(connection);
	}


	/* public: hibernate system survey */

	public Set<Class>             getMappedClasses()
	{
		return mappedClasses;
	}

	public Map<Class, Set<Class>> getDescendants()
	{
		return descendants;
	}

	public boolean                hasDescendants(Class entityClass)
	{
		Set<Class> ds = descendants.get(entityClass);
		return (ds != null) && !ds.isEmpty();
	}


	/* protected: hibernate system survey */

	protected void processSessionFactory(SessionFactory sf)
	  throws Exception
	{
		Collection<ClassMetadata> classesMetadata = sf.
		  getAllClassMetadata().values();

		ClassLoader               classLoader     = Thread.
		  currentThread().getContextClassLoader();

		//~: define all entities classes
		mappedClasses = new HashSet<Class>(classesMetadata.size());
		for(ClassMetadata classMetadata : classesMetadata)
			mappedClasses.add(classLoader.loadClass(classMetadata.getEntityName()));

		//~: create classes hierarchy closure
		descendants = new HashMap<Class, Set<Class>>(mappedClasses.size());
		for(Class mappedClass : mappedClasses)
			descendants.put(mappedClass, new HashSet<Class>(1));

		//~: trace classes hierarchy
		for(Class mappedClass : mappedClasses)
		{
			ArrayList<Class> ancestors = new ArrayList<Class>(1);

			ancestors.add(mappedClass.getSuperclass());
			ancestors.addAll(Arrays.asList(mappedClass.getInterfaces()));

			while(!ancestors.isEmpty())
			{
				Class ancestor = ancestors.remove(ancestors.size() - 1);
				if(ancestor == null) continue; //<-- it is possible

				//~: add to the stack
				ancestors.add(ancestor.getSuperclass());
				ancestors.addAll(Arrays.asList(ancestor.getInterfaces()));

				//~: add to it's descendants set
				Set<Class> closure = descendants.get(ancestor);
				if(closure == null)
					descendants.put(ancestor, closure = new HashSet<Class>(3));

				closure.add(mappedClass);
			}
		}

		//~: make the collections read-only
		mappedClasses = Collections.unmodifiableSet(mappedClasses);

		for(Map.Entry<Class, Set<Class>> e : descendants.entrySet())
			e.setValue(Collections.unmodifiableSet(e.getValue()));
		descendants = Collections.unmodifiableMap(descendants);
	}


	/* private: hibernate system points */

	private SessionFactory     sessionFactory;
	private Configuration      configuration;
	private ConnectionProvider connectionProvider;


	/* private: hibernate system survey */

	private Set<Class>             mappedClasses;
	private Map<Class, Set<Class>> descendants;
}