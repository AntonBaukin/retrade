package com.tverts.spring.system;

/* Java Naming */

import javax.naming.Context;
import javax.naming.NamingException;

/* Spring Framework */

import org.springframework.jndi.JndiTemplate;

/* com.tverts: system */

import com.tverts.system.JTAPoint;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Template to create JNDI Context from
 * jndi.properties resource file.
 *
 * @author anton.baukin@gmail.com.
 */
public class LocalJNDITemplate extends JndiTemplate
{
	protected Context createInitialContext()
	  throws NamingException
	{
		try
		{
			return JTAPoint.getInstance().buildContext();
		}
		catch(Throwable e)
		{
			e = EX.xrt(e);

			if(e instanceof NamingException)
				throw (NamingException)e;
			else
				throw EX.wrap(e);
		}
	}
}