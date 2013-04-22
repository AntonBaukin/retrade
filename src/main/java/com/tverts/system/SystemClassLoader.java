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

		//~: bind counter
		Integer n = INSTANCE.bindn.get();
		n = (n == null)?(1):(n + 1);
		INSTANCE.bindn.set(n);

		//~: bind index
		Integer i = INSTANCE.bindi.get();

		//?: {the thread is not bound yet}
		if(i == null)
			//?: {that class loader} do bind
			if(cl == INSTANCE.getParent())
			{
				Thread.currentThread().setContextClassLoader(INSTANCE);
				INSTANCE.bindi.set(n);
			}
	}

	public static void unbind()
	{
		//~: bind index
		Integer i = INSTANCE.bindi.get();

		//~: bind counter
		Integer n = INSTANCE.bindn.get();
		if((n == null) || (n <= 0))
			throw new IllegalStateException();

		//?: {it is that index} do unbind
		if(n.equals(i))
		{
			Thread.currentThread().setContextClassLoader(INSTANCE.getParent());
			INSTANCE.bindi.set(null);
		}

		//~: decrement counter
		n = (n == 1)?(null):(n - 1);
		INSTANCE.bindn.set(n);
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

	/**
	 * The number of bind invocations.
	 */
	private ThreadLocal<Integer> bindn = new ThreadLocal<Integer>();

	/**
	 * Bind invocation when system class loader was actually attached.
	 */
	private ThreadLocal<Integer> bindi = new ThreadLocal<Integer>();
}