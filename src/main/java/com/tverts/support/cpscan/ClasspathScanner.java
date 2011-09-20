package com.tverts.support.cpscan;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tverts.objects.StringsReference;
import com.tverts.support.SU;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

/**
 * This system utility class does scanning the classes
 * in the classpath and provides the resulting set.
 *
 * It adopts Spring implementation, but introduses own
 * class filters.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ClasspathScanner
{
	/* public: ClasspathScanner interface */

	public Set<Class> getClassesSet()
	{
		List<MetadataReader> mrs = loadMetadataFiltered();
		Set<Class>           res = new HashSet<Class>(mrs.size());
		ClassLoader          cll = Thread.currentThread().getContextClassLoader();
		String               cln = Void.class.getName();

		for(MetadataReader mr : mrs) try
		{
			cln = mr.getClassMetadata().getClassName();
			res.add(cll.loadClass(cln));
		}
		catch(Exception e)
		{
			throw new RuntimeException(String.format(
			  "Error occured while loading scanned class '%s'!", cln), e);
		}

		return res;
	}

	public Class[]    getClassesArray()
	{
		Set<Class> set = getClassesSet();
		return set.toArray(new Class[set.size()]);
	}


	/* public: ClasspathScanner (bean) interface */

	/**
	 * The array of root packages to scan.
	 */
	public String[] getPackages()
	{
		return packages;
	}

	public void setPackages(String[] packages)
	{
		this.packages = packages;
	}

	/**
	 * Set this string reference to list the root
	 * scan packages with ';' separator.
	 */
	public StringsReference getPackageNames()
	{
		return packageNames;
	}

	public void setPackageNames(StringsReference packageNames)
	{
		this.packageNames = packageNames;
	}

	public CPScanFilterReference getFilter()
	{
		return filter;
	}

	public void setFilter(CPScanFilterReference filter)
	{
		this.filter = filter;
	}


	/* protected: classpath scanning support */

	protected Set<String>          collectRootPackages()
	{
		Set<String> acc = new HashSet<String>(7);

		//~: add direct packages list
		if(getPackages() != null)
			acc.addAll(Arrays.asList(getPackages()));

		//~: add the packages referred
		if(getPackageNames() != null)
			for(Object s : getPackageNames().dereferObjects())
				acc.addAll(Arrays.asList(SU.s2a((String)s, ';')));

		Set<String> res = new HashSet<String>(acc.size());

		//~: remove the empty strings
		for(String s : acc)
			res.add(SU.s2s(s));
		res.remove(null);

		return res;
	}

	protected Set<String>          collectScanURIs()
	{
		Set<String> pks = collectRootPackages();
		Set<String> res = new HashSet<String>(pks.size());

		for(String pkg : pks)
			res.add(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
			  pkg.replace('.', '/') + "/**/*.class");

		return res;
	}

	protected List<MetadataReader> loadAllMetadata()
	{
		Set<String>                 uris = collectScanURIs();
		ResourcePatternResolver     rpr  = new PathMatchingResourcePatternResolver();
		SimpleMetadataReaderFactory mrf  = new SimpleMetadataReaderFactory();
		Set<Resource>               rcls = new HashSet<Resource>(101);

		//~: collect all the resources found at all the paths
		for(String uri : uris) try
		{
			rcls.addAll(Arrays.asList(rpr.getResources(uri)));
		}
		catch(Exception e)
		{
			throw new RuntimeException(String.format(
			  "Error occured while scanning classes in the URI [%s]!", uri), e);
		}

		List<MetadataReader>        res  = new ArrayList<MetadataReader>(rcls.size());

		//~: load metadata for all that resources & filter them
		for(Resource rcl : rcls) try
		{
			res.add(mrf.getMetadataReader(rcl));
		}
		catch(Exception e)
		{
			throw new RuntimeException(String.format(
			  "Error occured while reading metadata of class at %s!", rcl.toString()), e);
		}

		return res;
	}

	protected List<MetadataReader> loadMetadataFiltered()
	{
		//~: collect the filters
		List<CPScanFilter>   fts = (getFilter() == null)?(null):
		  (getFilter().dereferObjects());

		//?: {there are no filters} no classes must be returned
		if((fts == null) || fts.isEmpty())
			return new ArrayList<MetadataReader>(0);

		//~: load the metadata
		List<MetadataReader> all = loadAllMetadata();
		List<MetadataReader> res = new ArrayList<MetadataReader>(all.size()/10);

		//~: filter it...
		for(MetadataReader mr : all)
			if(isClassOfInterest(mr, fts))
				res.add(mr);

		return res;
	}

	protected boolean              isClassOfInterest
	  (MetadataReader mr, List<CPScanFilter> fts)
	{
		for(CPScanFilter filter : fts)
			if(filter.isClassOfInterest(mr))
				return true;

		return false;
	}


	/* private: scanning parameters */

	private String[]              packages;
	private StringsReference      packageNames;
	private CPScanFilterReference filter;
}