package com.tverts.retrade.domain.invoice;

/**
 * Good of a Move Invoice.
 *
 * @author anton.baukin@gmail.com
 */
public class      MoveGood
       extends    InvGood
       implements NeedCalcGood
{
	/* public: InvGood (bean) interface */

	public MoveData getData()
	{
		return (MoveData) super.getData();
	}

	public void setData(MoveData data)
	{
		super.setData(data);
	}

	/**
	 * This flag tells what this position in
	 * an altered Move Invoice is doing.
	 *
	 * Undefined is the default: do move, or
	 * produce. True means to add the good
	 * to the destination Store not taking
	 * it from the source one. False means
	 * vice-versa: to take the good (or it's
	 * calculation) from the source Store
	 * not placing it to the destination one.
	 *
	 * null  : move;
	 * false : take-only;
	 * true  : place-only.
	 *
	 */
	public Boolean getMoveOn()
	{
		return moveOn;
	}

	public void setMoveOn(Boolean moveOn)
	{
		this.moveOn = moveOn;
	}

	/**
	 * Need-calc flag is considered for altered Move
	 * Invoices, called Transformations. If this flag
	 * is undefined, the transformation is default:
	 * products, not semi-ready, are calculated. And
	 * to require calculation on semi-ready products,
	 * set true, to forbid it for any products, false.
	 *
	 * Note that in Transformation Invoices the
	 * resulting products are taken from the source
	 * Store, and the listed products are placed to
	 * the destination Store.
	 */
	public Boolean getNeedCalc()
	{
		return needCalc;
	}

	public void setNeedCalc(Boolean needCalc)
	{
		this.needCalc = needCalc;
	}


	/* persisted attributes */

	private Boolean moveOn;
	private Boolean needCalc;
}