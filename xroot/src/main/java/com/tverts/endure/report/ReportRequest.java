package com.tverts.endure.report;

/* com.tverts: endure (core) */

import java.util.Date;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.TxEntity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.AuthSession;

/* com.tverts: data sources */

import com.tverts.data.ReportFormat;


/**
 * Instance of report requested by a user.
 *
 * @author anton.baukin@gmail.com
 */
public class      ReportRequest
       extends    NumericBase
       implements DomainEntity, TxEntity
{
	public Domain getDomain()
	{
		return (template == null)?(null):(template.getDomain());
	}

	public ReportTemplate getTemplate()
	{
		return template;
	}

	public void setTemplate(ReportTemplate template)
	{
		this.template = template;
	}

	public AuthSession getAuthSession()
	{
		return authSession;
	}

	public void setAuthSession(AuthSession authSession)
	{
		this.authSession = authSession;
	}

	/**
	 * The timestamp of this request.
	 */
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	/**
	 * The UI Model saved as XML-serialized Java Bean.
	 * Defined only for Data Sources working with UI.
	 * (That have configuration window.)
	 */
	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public ReportFormat getFormat()
	{
		return format;
	}

	public void setFormat(ReportFormat format)
	{
		this.format = format;
	}

	/**
	 * Set to the time when the user
	 * had downloaded the report file.
	 */
	public Date getLoadTime()
	{
		return loadTime;
	}

	public void setLoadTime(Date loadTime)
	{
		this.loadTime = loadTime;
	}

	/**
	 * True when the report file is produced.
	 */
	public boolean isReady()
	{
		return ready;
	}

	public void setReady(boolean ready)
	{
		this.ready = ready;
	}

	/**
	 * The resulting report file.
	 */
	public byte[] getReport()
	{
		return report;
	}

	public void setReport(byte[] report)
	{
		this.report = report;
	}


	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}


	/* persisted attributes */

	private ReportTemplate template;
	private AuthSession    authSession;
	private Date           time;
	private String         model;
	private ReportFormat   format;
	private Date           loadTime;
	private boolean        ready;
	private byte[]         report;
}