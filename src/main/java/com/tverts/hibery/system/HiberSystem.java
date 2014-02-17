package com.tverts.hibery.system;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;

/* com.tverts: hibery */

import com.tverts.hibery.keys.HiberKeysContextStruct;
import com.tverts.endure.keys.KeysPoint;

/* com.tverts: system (tx) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.keys.KeysContext;


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


	/* public: HiberSystem (bean) interface */

	@SuppressWarnings("deprecation")
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory  = sf;

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


	/* public: primary keys generation */

	public KeysContext keysContext(Object instance, Session session)
	{
		if(instance == null)
			throw new IllegalArgumentException();

		return new HiberKeysContextStruct(instance.getClass()).
		  setSavedInstance(instance).
		  setSession(session);
	}

	public void        createPrimaryKey
	  (Session session, NumericIdentity instance, boolean fortest)
	{
		//?: {already have primary key} do not force change
		if(instance.getPrimaryKey() != null)
			return;

		Object primaryKey = KeysPoint.facadeGenerator().
		  createPrimaryKey(keysContext(instance, session));

		if(!(primaryKey instanceof Long))
			throw new IllegalStateException();

		//?: {is a test instance}
		if(fortest)
			primaryKey = -(Long)primaryKey;

		instance.setPrimaryKey((Long)primaryKey);
	}

	public long        createTxNumber(SessionFactory sf, Tx tx)
	{
		Session session = sf.getCurrentSession();

		if(session == null) throw new IllegalStateException(
		  "Hibernate Session Factpry has no Session " +
		  "bound to the current thread!");

		return (Long) KeysPoint.facadeGenerator().
		  createPrimaryKey(keysContext(tx, session));
	}


	/* public: enhanced routines */

	public Class       findActualClass(Object instance)
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

	public Object      unproxy(Object e)
	{
		if(!(e instanceof HibernateProxy))
			return e;

		Hibernate.initialize(e);
		return ((HibernateProxy)e).
		  getHibernateLazyInitializer().getImplementation();
	}

	@SuppressWarnings("unchecked")
	public Object      unproxyDeeply(SessionFactory f, Object e, Map closure)
	{
		if(e == null) return null;

		if(closure == null)
			closure = new IdentityHashMap(17);

		//?: {contained in the closure}
		Object x = closure.get(e);
		if(x != null) return x;

		//?: {it is a list | set}
		if((e instanceof List) || (e instanceof Set))
		{
			//HINT: collections may not be shared between the entities loaded,
			//  and we do not put them into the closure.

			Collection r = (e instanceof List)
			  ?(new ArrayList(((Collection)e).size()))
			  :(new HashSet(((Collection)e).size()));
			boolean    y = false;

			for(Iterator i = ((Collection)e).iterator();(i.hasNext());)
			{
				Object v = unproxyDeeply(f, (x = i.next()), closure);

				if(v != x) y = true;
				r.add(v);
			}

			//?: {was changed} update it
			return (y)?(r):(e);
		}

		//?: {it is a map}
		if(e instanceof Map)
		{
			//HINT: maps may not be shared between the entities loaded,
			//  and we do not put them into the closure.

			Map     r = new HashMap(((Map) e).size());
			boolean y = false;

			for(Iterator i = ((Map)e).entrySet().iterator();(i.hasNext());)
			{
				Map.Entry m = (Map.Entry) i.next();

				Object    k = unproxyDeeply(f, (x = m.getKey()), closure);
				if(k != x) y = true;

				Object    v = unproxyDeeply(f, (x = m.getValue()), closure);
				if(v != x) y = true;

				r.put(k, v);
			}

			//?: {was changed} update it
			return (y)?(r):(e);
		}

		//?: {not of out entity class} return as is
		if(!e.getClass().getName().startsWith("com.tverts"))
			return e;

		//~: un-proxy it
		x = unproxy(e); closure.put(e, x);
		e = x;          closure.put(e, e);

		ClassMetadata m = f.getClassMetadata(e.getClass());
		if(m == null) return e;

		//c: for all the properties (not having dots: are system)
		for(String p : m.getPropertyNames()) if(p.indexOf('.') == -1)
		{
			Object v = m.getPropertyValue(e, p);
			if(v == null) continue;

			//~: lookup the closure
			if((x = closure.get(v)) == null)
				x = unproxyDeeply(f, v, closure);

			//?: {instance was changed} update it
			if(v != x)
				m.setPropertyValue(e, p, x);
		}

		return e;
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
	public <T> Set<T>  findAttachedEntities(Session session, Class<T> entityClass)
	{
		if(!(session instanceof SessionImplementor))
			throw new IllegalArgumentException();

		Set result = new IdentitySet(31);

		//~: get the entries of the context
		Entry<Object, EntityEntry>[] entries = ((SessionImplementor)session).
		  getPersistenceContext().reentrantSafeEntityEntries();

		for(Entry<Object, EntityEntry> e : entries)
		{
			Object entity = e.getKey();

			//?: {this entity is persisted normally}
			if(session.contains(entity))
				//?: {it is exact the same class}
				if(entityClass.isAssignableFrom(findActualClass(entity)))
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
	public <T> Set<T>  reloadAttachedEntities
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


	/* public: hibernate system debug */

	public static int debugContextSize()
	{
		return debugContextSize(TxPoint.txSession());
	}

	public static int debugContextSize(Session s)
	{
		return ((SessionImplementor)s).getPersistenceContext().
		  getNumberOfManagedEntities();
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

	private SessionFactory         sessionFactory;
	private Configuration          configuration;


	/* private: hibernate system survey */

	private Set<Class>             mappedClasses;
	private Map<Class, Set<Class>> descendants;
}