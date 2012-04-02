package com.tverts.genesis;

/**
 * This generator dispatches creation of entities
 * day-by-day for the period defined. Starting from
 * the most old day it invokes all the generators
 * registered in the random order with the weights.
 *
 * Each sub-generator registered has the integer weight
 * assigned. The number of objects created in a day is
 * selected from random range. For each object the
 * generated is chosen by it's wight.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DaysGenDisp extends GenesisPartBase
{
	/* public: Genesis interface */

	public void generate()
	  throws GenesisError
	{}


	/* public static: Genesis struct */
	
	public static class GenStruct
	{

	}

}