package com.tverts.retrade.domain.goods;

/* Java */

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.actions.ActionsPoint.actionResult;
import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: endure (core + tree) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeDomain;
import com.tverts.endure.tree.TreeFolder;
import com.tverts.endure.tree.TreeItem;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Adds test Good Units to the goods tree.
 * Reads 'GenTestGoodsTree.xml' file.
 *
 * @author anton.baukin@gmail.com.
 */
public class GenTestGoodsTree extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		try
		{
			createProcessor(ctx).
			  process(getDataFile().toString());
		}
		catch(Throwable e)
		{
			e = EX.xrt(e);

			if(e instanceof GenesisError)
				throw (GenesisError)e;
			else
				throw new GenesisError(e, this, ctx);
		}
	}


	/* protected: generation */

	protected URL  getDataFile()
	{
		return EX.assertn(
		  getClass().getResource("GenTestGoodsTree.xml"),
		  "No GenTestGoodsTree.xml file found!"
		);
	}

	protected void ensureFolder(GenCtx ctx, GenState s)
	{
		TreeFolder tf = new TreeFolder();
		Folder      f = s.folders.get(s.folders.size() - 1);

		//?: {previously added}
		EX.assertx(!s.addedFolders.contains(f.code),
		  "Goods Tree Folder code [", f.code, "] appeared twice!"
		);
		s.addedFolders.add(f.code);

		//~: get the tree domain
		if(s.domian == null) s.domian = EX.assertn(bean(GetTree.class).
			 getDomain(ctx.get(Domain.class).getPrimaryKey(), Goods.TYPE_GOODS_TREE, null)
		);

		//=: tree domain
		tf.setDomain(s.domian);

		//=: code
		tf.setCode(f.code);

		//=: name
		tf.setName(f.name);

		//=: {parent folder}
		if(s.folders.size() > 1)
			tf.setParent(EX.assertn(s.folders.get(s.folders.size() - 2).folder));

		//!: ensure it
		f.folder = actionResult(TreeFolder.class, actionRun(
		  ActionType.ENSURE, tf, ActTreeFolder.PARAM_TYPE, Goods.TYPE_GOODS_FOLDER
		));

		LU.I(log(ctx), logsig(), " had ",
		  (tf == f.folder)?("created"):("found"),
		  " test goods Tree Folder [", f.folder.getPrimaryKey(),
		  "] with code [", f.folder.getCode(), "] named: ", f.folder.getName()
		);
	}

	@SuppressWarnings("unchecked")
	protected void addGood(GenCtx ctx, GenState s, String code)
	{
		//?: {previously added}
		EX.assertx(!s.addedGoods.contains(code),
		  "Goods Unit code [", code, "] is already added to else Folder!"
		);
		s.addedGoods.add(code);

		TreeFolder f = EX.assertn(s.folders.get(s.folders.size() - 1).folder);

		//~: generated goods mapping
		Map<String, GoodUnit> gmap = (Map<String, GoodUnit>)
		  EX.assertn(ctx.get((Object) GoodUnit.class));

		//~: get the good
		GoodUnit   g = EX.assertn(gmap.get(code),
		  "Good Unit with code [", code, "] was not generated!"
		);

		//?: {has good in the tree}
		List<TreeItem> tis = bean(GetTree.class).getTreeItems(
		  f.getDomain(), g.getPrimaryKey());

		if(!tis.isEmpty())
		{
			List<String> codes = new ArrayList<String>(1);
			for(TreeItem ti : tis) codes.add(ti.getFolder().getCode());

			LU.I(log(ctx), logsig(), " found Good Unit [", g.getCode(),
			  "] in the following goods Folders: [", SU.scats(", ", codes), "]"
			);

			return;
		}

		//!: add good to the folder on the top of the stack
		actionRun(ActTreeFolder.ADD, f, ActTreeFolder.PARAM_ITEM, g);

		LU.I(log(ctx), logsig(), " added good [", g.getCode(),
		  "] into goods folder: [", f.getCode(), "]");

		//~: add all sub-goods
		for(String sc : gmap.keySet())
			if(sc.startsWith(code) && !sc.equals(code))
			{
				//?: {not sub-good}
				GoodUnit sg = gmap.get(sc);
				if(!sc.equals(Goods.subCode(sg)))
					continue;

				EX.assertx(sg.isSubGood());
				EX.assertx(CMP.eq(g.getUnity(), sg.getUnity()));

				//!: add sub-good to the same folder
//				actionRun(ActTreeFolder.ADD, f, ActTreeFolder.PARAM_ITEM, sg);
//
//				LU.I(log(ctx), logsig(), " added sub-good [",
//				  g.getCode(), "] measure [", sg.getMeasure().getCode(),
//				  "] into goods folder: [", f.getCode(), "]");
			}
	}


	/* protected: XML Processor */

	protected static class GenState
	{
		TreeDomain domian;

		Set<String> addedFolders =
		  new HashSet<String>(17);

		Set<String> addedGoods =
		  new HashSet<String>(101);

		/**
		 * The stack of folders.
		 */
		public List<Folder> folders =
		  new ArrayList<Folder>(4);
	}

	protected static class Folder
	{
		public String     code;
		public String     name;

		public TreeFolder folder;
	}

	protected SaxProcessor<? extends GenState> createProcessor(GenCtx ctx)
	{
		return new ReadTestGoodsTree(ctx);
	}

	protected class ReadTestGoodsTree extends SaxProcessor<GenState>
	{
		public ReadTestGoodsTree(GenCtx ctx)
		{
			this.ctx = ctx;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			//?: {root}
			if(islevel(0))
				event().state(new GenState());
		}

		protected void open()
		{
			//?: <folder>
			if(istag("folder"))
			{
				Folder f = new Folder();

				//=: code + name
				f.code = EX.asserts(event().attr("code"));
				f.name = EX.asserts(event().attr("name"));

				//~: push the folder
				state(0).folders.add(f);

				//~: ensure it
				ensureFolder(ctx, state(0));
			}

			//?: {good}
			else if(istag("good"))
			{
				EX.asserte(state(0).folders);
				addGood(ctx, state(0), EX.asserts(event().attr("code")));
			}
		}

		protected void close()
		{
			//?: <folder>
			if(istag("folder"))
			{
				//!: pop folder from the stack
				EX.asserte(state(0).folders);
				state(0).folders.remove(state(0).folders.size() - 1);
			}
		}


		/* genesis context */

		private GenCtx ctx;
	}
}