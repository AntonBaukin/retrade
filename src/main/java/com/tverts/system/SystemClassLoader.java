package com.tverts.system;

/* standard Java classes */

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
	 * Binds this class loader to the the thread given.
	 * Works only when the current class loader is that
	 * existing on the initialization.
	 */
	public static void bind()
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		if((cl == INSTANCE) | (cl == INSTANCE.getParent()))
		{
			//~: increment counter
			Integer i = INSTANCE.binds.get();
			i = (i == null)?(1):(i + 1);
			INSTANCE.binds.set(i);

			//~: do bind
			if(i == 1)
				Thread.currentThread().setContextClassLoader(INSTANCE);
		}
	}

	public static void unbind()
	{
		if(Thread.currentThread().getContextClassLoader() == INSTANCE)
		{
			//~: decrement counter
			Integer i = INSTANCE.binds.get();
			if((i == null) || (i <= 0))
				throw new IllegalStateException();
			i = (i == 1)?(null):(i - 1);
			INSTANCE.binds.set(i);

			//~: do unbind
			if(i == null)
				Thread.currentThread().setContextClassLoader(INSTANCE.getParent());
		}
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


	/* private: bind counter */

	private ThreadLocal<Integer> binds =
	  new ThreadLocal<Integer>();
}