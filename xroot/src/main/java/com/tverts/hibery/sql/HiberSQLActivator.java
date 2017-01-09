package com.tverts.hibery.sql;

/* Java */

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.mapping.PersistentClass;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* Java DOM */

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;
import com.tverts.system.tx.TxPoint;

/* com.tverts: servlet (listeners) */

import com.tverts.servlet.listeners.PickListener;
import com.tverts.servlet.listeners.ContextListenerBase;

/* com.tverts: hibery (system) */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


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
@Component @PickListener(order = 200)
public class   HiberSQLActivator
       extends ContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		//~: search files with SQL tasks
		List<String> files; try
		{
			files = searchFiles();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error occured while seaching '*.sql.xml'",
			  " files with SQL tasks!");
		}

		//~: parse the tasks of the files
		final List<SQLTask> tasks = new ArrayList<>(16);
		for(String file : files) try
		{
			prepareFile(file, tasks);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error occured when prepearing ",
			  "SQL tasks in file [", file, "]!"
			);
		}

		//!: execute the tasks
		if(!tasks.isEmpty()) bean(TxBean.class).execute(() ->
		{
			try
			{
				executeTasksTx(tasks);
			}
			catch(Throwable e)
			{
				throw EX.wrap(e, "Error occured when executing SQL task!");
			}
		});
	}


	/* protected: SQL tasks execution */

	protected List<String> searchFiles()
	{
		List<String> res = new ArrayList<>(4);
		for(PersistentClass icls : HiberSystem.meta().getEntityBindings())
		{
			Class  cls = icls.getMappedClass();
			Object url = cls.getResource(cls.getSimpleName() + ".sql.xml");

			if(url != null)
				res.add(url.toString());
		}

		return res;
	}

	protected void         prepareFile(String url, List<SQLTask> tasks)
	  throws Exception
	{
		Document xml = parseXML(url);

		//c: select all <tasks>/<task> elements
		for(Element n : xml.getRootElement().getChildren("task"))
		{
			//~: get task class name
			String tclsn = EX.assertn( SU.s2s(n.getAttributeValue("class")),
			  "SQL Tasks file [", url, "] <task> tag has no 'class' attribute!"
			);

			//~: load the class
			Class tcls; try
			{
				tcls = Thread.currentThread().
				  getContextClassLoader().loadClass(tclsn);
			}
			catch(Exception e)
			{
				tcls = null;
			}

			EX.assertn(tcls, "SQL Tasks file [", url,
			  "] <task> tag has unknown 'class' value: [", tclsn, "]!");

			//~: create the task instance
			Object task; try
			{
				task = tcls.newInstance();
			}
			catch(Throwable w)
			{
				throw EX.wrap(w, "SQL Tasks file [", url,
				  "] <task> tag with 'class' value: [", tclsn,
				  "]; couldn't create class instance! "
				);
			}

			//?: {not a SQL Task}
			if(!(task instanceof SQLTask)) throw EX.ass(
			  "SQL Tasks file [", url, "] <task> tag has 'class' value: [",
			  tclsn, "] of not a SQLTask interface!"
			);

			//~: configure the task
			((SQLTask)task).configure(n);

			//!: enqueue the task
			tasks.add((SQLTask)task);
		}
	}

	protected Document     parseXML(String url)
	  throws Exception
	{
		if(builder == null)
			builder = new SAXBuilder();
		return builder.build(new URL(url));
	}

	protected void         executeTasksTx(List<SQLTask> tasks)
	  throws Exception
	{
		//c: execute the tasks given
		for(SQLTask task : tasks) try
		{
			task.execute(TxPoint.txSession());
		}
		catch(Throwable e)
		{
			//~: report the error
			throw EX.wrap(e, "Error in SQL Task of class: [",
			  task.getClass().getName(), "]!");
		}
	}


	/* private: support instances */

	private SAXBuilder builder;
}