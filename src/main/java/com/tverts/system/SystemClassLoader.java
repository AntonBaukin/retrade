package com.tverts.system;

/* standard Java classes */

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* Spring Framework */

import org.springframework.util.ReflectionUtils;


/**
 * Delegates class loading to the number
 * of class loaders configured.
 *
 * @author anton.baukin@gmail.com
 */
public final class SystemClassLoader extends ClassLoader
{
	/* public: constructor */

	private static final String JBOSS_CLASS_LOADER =
	  "org.jboss.modules.ModuleClassLoader";

	public SystemClassLoader(ClassLoader parent)
	{
		super(parent);

		//?: {unsupported class loader}
		if(!parent.getClass().getName().equals(JBOSS_CLASS_LOADER))
			throw new IllegalArgumentException(String.format(
			  "Unsupported Application Server Class Loader [%s]! " +
			  "Expected class [%s].",

			  parent.getClass().getName(), JBOSS_CLASS_LOADER
			));

		//~: init transformer delegation
		try
		{
			initTransformerDelegation(parent);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	/* static: thread binding */

	public static volatile SystemClassLoader INSTANCE;


	/**
	 * Creates global {@link SystemClassLoader} instance
	 * taking the parent class loader from the thread.
	 */
	public static void init()
	{
		synchronized(SystemClassLoader.class)
		{
			if(INSTANCE != null) return;

			INSTANCE = new SystemClassLoader(
			  Thread.currentThread().getContextClassLoader()
			);
		}
	}

	/**
	 * Binds System Class Loader to the the thread given.
	 */
	public static void bind()
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		//~: put in stack
		List<ClassLoader> binds = INSTANCE.binds.get();
		if(binds == null)
			INSTANCE.binds.set(binds = new ArrayList<ClassLoader>(8));
		binds.add(cl);

		//~: set system class loader
		//if(cl != INSTANCE)
		//	Thread.currentThread().setContextClassLoader(INSTANCE);
	}

	public static void unbind()
	{
		List<ClassLoader> binds = INSTANCE.binds.get();
		if((binds == null) || binds.isEmpty())
			throw new IllegalStateException();

		//~: pop the original class loader
		ClassLoader cl = binds.remove(binds.size() - 1);

		//~: set it
		//if(cl != INSTANCE)
		//	Thread.currentThread().setContextClassLoader(cl);
	}


	/* public: SystemClassLoader interface */

	public void addTransformer(ClassFileTransformer transformer)
	{
		//~: add transformer to the parent class loader
		try
		{
			delegateAddTransformer(transformer);
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
	}


	/* private: parent class loader transformation hacks */

	private Object delegatingTransformer;
	private Method delegateAddTransformer;

	private void delegateAddTransformer(ClassFileTransformer transformer)
	  throws Exception
	{
		delegateAddTransformer.invoke(delegatingTransformer, transformer);
	}

	private void initTransformerDelegation(ClassLoader parent)
	  throws Exception
	{
		//~: find the delegate field
		Field f = ReflectionUtils.findField(
		  parent.getClass(), "transformer"
		);

		//~: get the delegate
		f.setAccessible(true);
		delegatingTransformer = f.get(parent);

		//~: get add transformer method
		delegateAddTransformer = ReflectionUtils.findMethod(
		  delegatingTransformer.getClass(), "addTransformer",
		  ClassFileTransformer.class
		);

		delegateAddTransformer.setAccessible(true);
	}


	/* private: bind stack */

	/**
	 * The class loaders stack.
	 */
	private ThreadLocal<List<ClassLoader>> binds =
	  new ThreadLocal<List<ClassLoader>>();
}