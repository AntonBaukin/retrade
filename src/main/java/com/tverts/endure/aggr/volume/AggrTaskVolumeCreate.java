package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTaskBase;


/**
 * Send this aggregation task to add volume to the
 * aggregated value with items of class {@link AggrItemVolume}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrTaskVolumeCreate extends AggrTaskBase
{
	public static final long serialVersionUID = 0L;


	/* public: AggrTaskVolumeCreate (bean) interface */

	public BigDecimal getVolumePositive()
	{
		return volumePositive;
	}

	public void       setVolumePositive(BigDecimal volumePositive)
	{
		this.volumePositive = volumePositive;
	}

	public BigDecimal getVolumeNegative()
	{
		return volumeNegative;
	}

	public void       setVolumeNegative(BigDecimal volumeNegative)
	{
		this.volumeNegative = volumeNegative;
	}


	/* private: the volumes */

	private BigDecimal volumePositive;
	private BigDecimal volumeNegative;
}