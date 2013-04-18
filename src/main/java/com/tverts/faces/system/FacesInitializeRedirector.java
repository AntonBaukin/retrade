package com.tverts.faces.system;

/* standard Java classes */

import java.util.Set;

/* Java Servlet api */

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

/* JavaServer Faces */

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.bean.ManagedBean;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;
import javax.faces.event.ListenerFor;
import javax.faces.event.ListenersFor;
import javax.faces.render.FacesBehaviorRenderer;
import javax.faces.render.Renderer;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;

/* JavaServer Faces (Mojarra RI) */

import com.sun.faces.config.FacesInitializer;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;


/**
 * Invokes JSF Reference Implementation
 * initializer in the context of
 * System Class Loader.
 *
 *
 * @author anton.baukin@gmail.com
 */
@HandlesTypes({
  ManagedBean.class,
  FacesComponent.class,
  FacesValidator.class,
  FacesConverter.class,
  FacesBehaviorRenderer.class,
  ResourceDependency.class,
  ResourceDependencies.class,
  ListenerFor.class,
  ListenersFor.class,
  UIComponent.class,
  Validator.class,
  Converter.class,
  Renderer.class
})
public class      FacesInitializeRedirector
       implements ServletContainerInitializer
{
	/* public: ServletContainerInitializer interface */

	public void onStartup(Set<Class<?>> classes, ServletContext ctx)
	  throws ServletException
	{
		//~: create system class loader
		SystemClassLoader.init();

		//~: bind it
		SystemClassLoader.bind();

		//~: initialize faces
		try
		{
			new FacesInitializer().onStartup(classes, ctx);
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}
}