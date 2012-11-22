package com.tverts.hibery.sql;

/* standard Java classes */

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* Hibernate Persistence Layer */

import org.hibernate.mapping.PersistentClass;

/* Java DOM */

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/* com.tverts: servlet (listeners) */

import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: hibery (system) */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * This listener is invoked after the Hibernate
 * Session Factory creation is complete (and the
 * schema update also complete).
 *
 * It searches for 'EntityClass.sql.xml' files,
 * parses them, and executes the SQL tasks
 * ({@link SQLTask}) defined there.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   HiberSQLActivator
       extends ServletContextListenerBase
{

	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		List<String> files;

		//~: search files with SQL tasks
		try
		{
			files = searchFiles();
		}
		catch(Exception e)
		{
			throw new RuntimeException(
			  "Error occured while seaching '*.sql.xml' files with SQL tasks!", e);
		}

		Exception  e = null;
		Connection c = null;

		for(String file : files) try
		{
			if(c == null)
				c = HiberSystem.getInstance().openConnection();
			executeFile(file, c);
		}
		catch(Exception x)
		{
			e = x;
			break;
		}

		//?: {rollback}
		if((c != null) && (e != null)) try
		{
			c.rollback();
		}
		catch(Exception x)
		{}

		//?: {commit}
		if((c != null) && (e == null)) try
		{
			c.commit();
		}
		catch(Exception x)
		{
			e = x;
		}

		if(c != null) try
		{
			HiberSystem.getInstance().closeConnection(c);
		}
		catch(Exception x)
		{
			if(e == null) e = x;
		}

		if(e != null) throw new RuntimeException(
		  "Error occured while invoking SQL tasks on the system startup!", e);
	}

	public void contextDestroyed(ServletContextEvent sce)
	{}


	/* protected: sql tasks execution */

	protected List<String> searchFiles()
	{
		List<String>              res  = new ArrayList<String>(4);
		Iterator<PersistentClass> icls = HiberSystem.
		  config().getClassMappings();

		while(icls.hasNext())
		{
			Class  cls = icls.next().getMappedClass();
			Object url = cls.getResource(cls.getSimpleName() + ".sql.xml");

			if(url != null)
				res.add(url.toString());
		}

		return res;
	}

	protected void         executeFile(String url, Connection c)
	  throws Exception
	{
		Document xml = parseXML(url);

		//c: select all <tasks>/<task> elements
		for(Element n : xml.getRootElement().getChildren("task"))
		{
			//~: get task class name
			String tclsn = s2s(n.getAttributeValue("class"));
			if(tclsn == null) throw new IllegalStateException(String.format(
			  "SQL Tasks file [%s] <task> tag has no 'class' attribute!", url));

			//~: load the class
			Class  tcls  = null;

			try
			{
				tcls = Thread.currentThread().
				  getContextClassLoader().loadClass(tclsn);
			}
			catch(Exception e)
			{}

			if(tcls == null) throw new IllegalStateException(String.format(
			  "SQL Tasks file [%s] <task> tag has unknown " +
			  "'class' value: [%s]!", url, tclsn));

			//~: create the task instance
			Object task  = tcls.newInstance();
			if(!(task instanceof SQLTask)) throw new IllegalStateException(
			  String.format("SQL Tasks file [%s] <task> tag has 'class' value: " +
			   "[%s] of not a SQLTask class!", url, tclsn));


			//~: configure the task
			((SQLTask)task).configure(n);

			//!: execute the task
			((SQLTask)task).execute(c);
		}
	}

	protected Document     parseXML(String url)
	  throws Exception
	{
		if(builder == null)
			builder = new SAXBuilder();
		return builder.build(new URL(url));
	}


	/* private: support instances */

	private SAXBuilder builder;
}