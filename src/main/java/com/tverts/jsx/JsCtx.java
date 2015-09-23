package com.tverts.jsx;

/* Java */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/* Java Scripting */

import javax.script.Bindings;
import javax.script.ScriptContext;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;
import com.tverts.support.Perks;
import com.tverts.support.streams.BytesStream;
import com.tverts.support.streams.EmptyReader;
import com.tverts.support.streams.NullWriter;

/**
 * Parameters and the results of
 * Script Execution.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsCtx implements AutoCloseable
{
	/* Initialization */

	/**
	 * Tells to create the context with discarding
	 * standard and error streams.
	 */
	public static final Object VOID = "void";

	/**
	 * Tells to create the context
	 * with discarding error stream.
	 */
	public static final Object XERR = "no-error";

	/**
	 * Creates context with empty input stream
	 * and buffers for error and standard streams.
	 *
	 * The perks supported are: VOID; XERR;
	 * byte[], or Input Stream, or Reader
	 * as the input; Output Stream, or
	 * Writer as the output.
	 *
	 * The encoding supposed is UTF-8.
	 */
	public JsCtx init(Object... perks)
	{
		Perks ps = new Perks(perks);

		if(ps.when(VOID))
		{
			this.output = new PrintWriter(new NullWriter());
			this.error  = new PrintWriter(new NullWriter());
		}
		else try
		{
			//?: {has no error stream}
			if(ps.when(XERR))
				this.error = new PrintWriter(new NullWriter());
			//~: create the default error
			else
			{
				this.errorBytes = new BytesStream();
				this.error = new PrintWriter(
				  new OutputStreamWriter(this.errorBytes, "UTF-8"));
			}

			//~: bytes -> input reader
			ps.find(byte[].class, bs -> this.input =
			  new InputStreamReader(new ByteArrayInputStream(bs), "UTF-8")
			);

			//~: input stream -> input reader
			ps.find(InputStream.class, is -> this.input =
			  new InputStreamReader(is, "UTF-8")
			);

			//~: reader -> input reader
			ps.find(Reader.class, r -> this.input = r);

			//~: output stream -> output writer
			ps.find(OutputStream.class, os -> this.output =
			  new PrintWriter(new OutputStreamWriter(os, "UTF-8"))
			);

			//~: writer -> output writer
			ps.find(Writer.class, w -> this.output =
			  (w instanceof PrintWriter)?((PrintWriter) w):(new PrintWriter(w, true))
			);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		//?: {create default empty input}
		if(this.input == null)
			this.input = new EmptyReader();

		//?: {create default bytes output}
		if(this.output == null) try
		{
			this.outputBytes = new BytesStream();
			this.output = new PrintWriter(new OutputStreamWriter(
			  this.outputBytes, "UTF-8"));
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		//?: {create default bytes error stream}
		if(this.error == null) try
		{
			this.errorBytes = new BytesStream();
			this.error = new PrintWriter(new OutputStreamWriter(
			  this.errorBytes, "UTF-8"));
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		return this;
	}


	/* Variables */

	public JsCtx put(String name, Object value)
	{
		vars.put(name, value);
		return this;
	}

	protected final Map<String, Object> vars =
	  new HashMap<>(3);

	/**
	 * The result object of the
	 * script execution (if any).
	 */
	public Object result;


	/* Streams */

	public JsCtx setInput(Reader input)
	{
		this.input = (input != null)?(input):(new EmptyReader());
		return this;
	}

	protected Reader input;

	public JsCtx setOutput(PrintWriter output)
	{
		this.output = (output != null)?(output):
		  new PrintWriter(new NullWriter());

		if(outputBytes != null)
			outputBytes.closeAlways();
		outputBytes = null;

		return this;
	}

	protected PrintWriter output;

	public JsCtx setError(PrintWriter error)
	{
		this.error = (error != null)?(error):
		  new PrintWriter(new NullWriter());

		if(errorBytes != null)
			errorBytes.closeAlways();
		errorBytes = null;

		return this;
	}

	protected PrintWriter error;

	/**
	 * Returns Bytes Stream created by the default
	 * initialization procedure. Undefined when
	 * user installs own stream.
	 *
	 * The text is encoded in UTF-8.
	 */
	public BytesStream getOutputBytes()
	{
		return outputBytes;
	}

	protected BytesStream outputBytes;

	public BytesStream getErrorBytes()
	{
		return errorBytes;
	}

	protected BytesStream errorBytes;


	/* Closeable */

	/**
	 * Closes all the streams assigned
	 * to this context.
	 */
	public void close()
	{
		new IO.Closer(input, output, error,
		  outputBytes, errorBytes).close();
	}

	public void flush()
	{
		try
		{
			output.flush();
			error.flush();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}


	/* Scripts Execution */

	public ScriptContext create(JsEngine jse)
	{
		ScriptContext ctx = jse.createContext();

		//=: reader
		ctx.setReader(this.input);

		//=: writer
		ctx.setWriter(this.output);

		//=: error writer
		ctx.setErrorWriter(this.error);

		return ctx;
	}
}