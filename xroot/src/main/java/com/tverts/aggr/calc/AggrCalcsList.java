package com.tverts.aggr.calc;

/* com.tverts: objects */

import com.tverts.objects.ObjectsRedirector;


/**
 * A list of references to aggregation calculators.
 *
 * @author anton.baukin@gmail.com
 */
public class      AggrCalcsList
       extends    ObjectsRedirector<AggrCalculator>
       implements AggrCalcReference
{}