package com.tverts.endure.report;

/* Java */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* Java Servlet */

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import com.tverts.servlet.Download;
import com.tverts.servlet.REQ;
import com.tverts.servlet.Upload;

/* com.tverts: models */

import com.tverts.model.ViewModelBeanBase;

/* com.tverts: objects */

import com.tverts.objects.BinarySource.Biny;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;
import com.tverts.support.streams.BytesStream;


/**
 * Model to create and edit a Report Template.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model")
@XmlType(name = "report-template")
public class      ReportTemplateModelBean
       extends    ViewModelBeanBase
       implements Upload, Download
{
	/* View */

	public ReportTemplateView getView()
	{
		if(super.getView() == null)
			setView(new ReportTemplateView());
		return (ReportTemplateView) super.getView();
	}

	public Class viewClass()
	{
		return ReportTemplateView.class;
	}


	/* Report Template Model */

	public String getDid()
	{
		return did;
	}

	public void setDid(String did)
	{
		this.did = did;
	}

	public String getDataSourceName()
	{
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName)
	{
		this.dataSourceName = dataSourceName;
	}

	@XmlTransient
	public byte[] getTemplate()
	{
		return template;
	}

	public void setTemplate(byte[] template)
	{
		this.template = template;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}


	/* public: Upload interface */

	public void upload(UploadCtx ctx)
	  throws IOException
	{
		//?: {not a single file} something strange
		EX.assertx(ctx.index() == 0);

		//~: the file name
		this.fileName = ctx.fileName();

		//~: the streamed bytes
		try(BytesStream stream = new BytesStream())
		{
			//~: gun-zip the template
			GZIPOutputStream gzip = new GZIPOutputStream(stream);
			IO.pump(ctx.stream(), gzip);

			stream.setNotCloseNext(true);
			gzip.close();

			//~: the resulting bytes
			this.template = stream.bytes();
		}
	}

	public void commitUpload(Biny biny)
	  throws java.io.IOException
	{
		biny.set("Content-Type", "application/json");
		biny.stream(0).write("{'success': true}".getBytes("UTF-8"));
	}


	/* public: Download interface */

	public void download(HttpServletRequest req, HttpServletResponse res)
	  throws IOException, ServletException
	{
		//~: gun-zipped template
		byte[] data = this.template;

		//?: {has no template bytes assigned} load
		if((data == null) && (getView().getObjectKey() != null))
		{
			ReportTemplate rt = bean(GetReports.class).
			  getReportTemplate(getView().getObjectKey());

			data = EX.assertn(rt).getTemplate();
		}

		//?: {has no template} nothing to download
		if(data == null)
		{
			res.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}

		res.setContentType("text/xml");
		res.setHeader("Content-Disposition", "attachment;");

		//?: {client supports deflate}
		if(REQ.isGunZIPAllowed(req))
		{
			res.addHeader("Content-Encoding", "gzip");
			res.setContentLength(data.length);

			res.getOutputStream().write(data);
			return;
		}

		//~: write the decompressed template
		GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
		IO.pump(gzip, res.getOutputStream());
		gzip.close();
	}


	/* private: encapsulated data */

	private String did;
	private String dataSourceName;
	private byte[] template;
	private String fileName;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.str(o, did);
		IO.str(o, dataSourceName);
		IO.str(o, fileName);
		IO.obj(o, template);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		did = IO.str(i);
		dataSourceName = IO.str(i);
		fileName = IO.str(i);
		template = IO.obj(i, byte[].class);
	}
}