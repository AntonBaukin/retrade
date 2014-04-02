package com.tverts.servlet;

/* Java */

import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/* Java Servlets */

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.beanOrNull;

/* com.tverts: model */

import com.tverts.model.DataSelectModel;
import com.tverts.model.DataSortModel;
import com.tverts.model.ModelAccessPoint;
import com.tverts.model.ModelBean;
import com.tverts.model.ModelProvider;
import com.tverts.model.ModelRequest;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: objects */

import com.tverts.objects.XMAPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.streams.BytesStream;


/**
 * Servlet top access user interface models.
 *
 * @author anton.baukin@gmail.com.
 */
public class ModelServlet extends GenericServlet
{
	/* public: Generic Servlet */

	public void service(ServletRequest xreq, ServletResponse xres)
	  throws java.io.IOException, ServletException
	{
		if(!(xreq instanceof HttpServletRequest))
			throw EX.state();

		if(!(xres instanceof HttpServletResponse))
			throw EX.state();

		HttpServletRequest  req = (HttpServletRequest)xreq;
		HttpServletResponse res = (HttpServletResponse)xres;

		//?: {not a GET request}
		if(!"GET".equalsIgnoreCase(req.getMethod()))
		{
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		String    param = SU.s2s(req.getParameter(ModelView.MODEL_PROVIDER));
		ModelBean model = null;
		Object    data  = null;

		//?: {has model provider}
		if(param != null)
		{
			Object provider = beanOrNull(param);

			//?: {has provider} get the model
			if(provider instanceof ModelProvider)
				model = ((ModelProvider)provider).provideModel();
		}

		//?: {model not provided & has model parameter} access via the model point
		if(model == null)
		{
			param = SU.s2s(req.getParameter(ModelView.MODEL_PARAM));

			if(param != null)
				model = ModelAccessPoint.model().readBean(param);
		}

		//~: apply the data selection limits
		if(model instanceof DataSelectModel)
		{
			String  ps = SU.s2s(req.getParameter(DataSelectModel.START_PARAM));
			String  pl = SU.s2s(req.getParameter(DataSelectModel.LIMIT_PARAM));
			Integer s  = (ps == null)?(null):Integer.parseInt(ps);
			Integer l  = (pl == null)?(null):Integer.parseInt(pl);

			EX.assertx( (s == null) || (s >= 0),
			  "Data selection 'start' parameter is illegal!"
			);

			EX.assertx( (l == null) || ((l >= 0) && (l <= DataSelectModel.LIMIT_MAX)),
			  "Data selection 'limit' parameter is illegal!"
			);

			if(s != null) ((DataSelectModel)model).setDataStart(s);
			if(l != null) ((DataSelectModel)model).setDataLimit(l);
		}


		//~: apply sort properties
		if(model instanceof DataSortModel)
		{
			((DataSortModel)model).clearSort();

			for(int i = 0;;i++)
			{
				String p = SU.s2s(req.getParameter("sortProperty" + i));
				String d = SU.s2s(req.getParameter("sortDesc" + i));
				if(p == null) break;

				((DataSortModel)model).addSort(p, "true".equals(d));
			}
		}

		//~: set the model req key
		ModelRequest.getInstance().setKey(
		  req.getParameter(ModelView.MODEL_REQ_PARAM));

		try
		{
			//?: {has model} access data bean
			if(model != null)
				data = ((ModelBean)model).modelData();

			//?: {no model bean provided}
			if(data == null)
			{
				if(param == null)
					res.sendError(404, "No model bean (or provider) were specified!");
				else
					res.sendError(404, "Specified model bean (or provider) was not found!");

				return;
			}
		}
		finally
		{
			ModelRequest.getInstance().setKey(null);
		}

		//~: do write model data as XML
		BytesStream bytes = new BytesStream();
		try
		{
			OutputStream stream = bytes;

			//?: {supports Gun-ZIP}
			{
				String ae = SU.sXs(req.getHeader("Accept-Encoding")).toLowerCase();

				if(ae.contains("gzip"))
				{
					stream = new GZIPOutputStream(stream);
					res.addHeader("Content-Encoding", "gzip");
				}
			}

			//~: model -> XML
			bytes.setNotCloseNext(true);
			XMAPoint.writeObject(data, stream);

			//~: response content type
			res.setContentType("application/xml;charset=UTF-8");

			//~: response length
			res.setContentLength((int)bytes.length());

			//~: write the data
			bytes.copy(res.getOutputStream());
		}
		finally
		{
			bytes.close();
		}
	}
}