package com.tverts.endure.cats;

/* SAX Parser */

import org.xml.sax.InputSource;

/* com.tverts: support */

import static com.tverts.support.SU.sXe;
import com.tverts.support.xml.SaxProcessor;


/**
 * Reads catalogue items from the XML file.
 * The format of the file is any root tag
 * tag with nested 'item'. Both attributes
 * 'code', 'name' and the same named tags
 * are supported.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ReadCatItems<C extends CatItem>
{
	/* public: constructors */

	public    ReadCatItems(Class<C> itemClass)
	{
		this.itemClass = itemClass;
	}

	protected ReadCatItems()
	{}


	/* callback interface */

	public static interface OnCatItem<C extends CatItem>
	{
		/* public: OnCatItem interface */

		public void onCatItem(C item);
	}


	/* public: ReadCatItems interface */

	public Class<C> getItemClass()
	{
		return itemClass;
	}

	public void     process(InputSource src, OnCatItem<C> callback)
	{
		createHandler(callback).process(src);
	}

	public void     process(String uri, OnCatItem<C> callback)
	{
		createHandler(callback).process(uri);
	}


	/* sax handler */

	public static class CatItemState<C extends CatItem>
	{
		/* public: constructor */

		public CatItemState(C catItem)
		{
			this.catItem = catItem;
		}


		/* public: CatItemState interface */

		public C item()
		{
			return catItem;
		}


		/* private: CatItemState interface */

		private C catItem;
	}

	public class CatItemHandler extends SaxProcessor<CatItemState<C>>
	{
		/* public: constructor */

		public CatItemHandler(OnCatItem<C> callback)
		{
			if(callback == null)
				throw new IllegalArgumentException();
			this.callback = callback;
		}


		/* protected: SaxProcessor interface */

		protected void    createState()
		{
			if(isItemTag())
				event().state(ReadCatItems.this.createState());
		}

		protected void    open()
		{}

		protected void    close()
		{
			String code = "", name = "";

			if(level() == 2)
			{
				//~: code tag
				if(codeTag().equals(event().tag()))
					code = event().text();

				//~: name tag
				if(nameTag().equals(event().tag()))
					name = event().text();

				if(!sXe(code))
					state(1).item().setCode(code.trim());

				if(!sXe(name))
					state(1).item().setName(name.trim());
			}

			if(isItemTag())
			{
				//~: code attribute
				if(!codeAttr().isEmpty())
					code = event().attrs().getValue(codeAttr());

				//~: name attribute
				if(!nameAttr().isEmpty())
					name = event().attrs().getValue(nameAttr());

				if(!sXe(code) && sXe(state().item().getCode()))
					state().item().setCode(code.trim());

				if(sXe(state().item().getName()))
				{
					//~: take <item> body as the item name
					if(sXe(name))
						name = event().text().trim();

					if(!sXe(name))
						state().item().setName(name.trim());
				}

				//~: check code & name exists
				if(sXe(state().item().getCode()))
					throw new IllegalStateException();
				if(sXe(state().item().getName()))
					throw new IllegalStateException();

				//!: invoke the callback
				callback.onCatItem(state().item());
			}
		}

		protected boolean isItemTag()
		{
			return (level() == 1) && itemTag().equals(event().tag());
		}


		/* the callback */

		protected final OnCatItem<C> callback;
	}


	/* protected: processing internals */

	protected String          itemTag()
	{
		return "item";
	}

	protected String          codeAttr()
	{
		return "code";
	}

	protected String          nameAttr()
	{
		return "code";
	}

	protected String          codeTag()
	{
		return "code";
	}

	protected String          nameTag()
	{
		return "code";
	}

	protected CatItemState<C> createState()
	{
		try
		{
			return new CatItemState<C>(
			  getItemClass().newInstance()
			);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	protected SaxProcessor<CatItemState<C>>
	                          createHandler(OnCatItem<C> callback)
	{
		return new CatItemHandler(callback);
	}


	/* private: the catalogue item class */

	private Class<C> itemClass;
}